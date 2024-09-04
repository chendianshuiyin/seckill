package com.cytus.seckilldemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.cytus.seckilldemo.config.AccessLimit;
import com.cytus.seckilldemo.exception.GlobalException;
import com.cytus.seckilldemo.pojo.Order;
import com.cytus.seckilldemo.pojo.SeckillMessage;
import com.cytus.seckilldemo.pojo.SeckillOrder;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.rabbitmq.MQSender;
import com.cytus.seckilldemo.service.*;
import com.cytus.seckilldemo.utils.JsonUtil;
import com.cytus.seckilldemo.vo.GoodsVo;
import com.cytus.seckilldemo.vo.RespBean;
import com.cytus.seckilldemo.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
* 秒杀相关Controller
* */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Long> script;
    @Autowired
    private MQSender mqSender;
    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    /*
    * 初始化时执行的方法
    * */
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化时加载商品库存到redis中
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list))
        {
            // 没有商品时直接返回
            return;
        }
        list.forEach(goodsVo -> {
            // lamda表达式，遍历每个商品，将id和库存数量存入redis中
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            // 标记每个商品的库存是否足够
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }

    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, @RequestParam("goodsId") Long goodsId){
        if(user == null)
        {
            //登录
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        // 库存判断
        if(goodsVo.getStockCount()<1)
        {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",user.getId()).eq("goods_id",goodsId));
        if(seckillOrder != null)
        {
            model.addAttribute("errmsg",RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user,goodsVo);
        model.addAttribute("order",order);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }

    /*
    * 静态化秒杀页面
    * */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable(value = "path") String path, User user, @RequestParam("goodsId") Long goodsId){
        if(user == null)
        {
            //登录
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 判断路径
        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check)
        {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 判断是否重复抢购，从redis获取数据
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null)
        {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 通过内存标记，判断库存是否足够
        if(EmptyStockMap.get(goodsId))
        {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 没有重复抢购，预减少库存,用lua脚本实现
        Long stock = valueOperations.decrement("seckillGoods:"+goodsId); // 使用decrement递减库存，并且这是一个原子操作
        //Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:"+goodsId),Collections.EMPTY_LIST);
        if(stock<0)
        {
            // 没有库存了修改标记
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);// 防止掉到负数
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 下单
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 库存判断
//        if(goodsVo.getStockCount()<1)
//        {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//        // 判断是否重复抢购
//        //SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",user.getId()).eq("goods_id",goodsId));
//        //从redis中获取数据
//        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
//        if(seckillOrder != null)
//        {
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
    }

    /*
    * 获取秒杀结果
    * */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, @RequestParam("goodsId") Long goodsId)
    {
        if(user == null)
        {
            return  RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    /*
    * 请求秒杀接口
    * */
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    public RespBean getPath(User user, @RequestParam("goodsId") Long goodsId, HttpServletRequest request)
    {
        if(user==null)
        {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // 限制访问次数，五秒内访问五次
//        String uri = request.getRequestURI();
//        Integer count = (Integer) valueOperations.get(uri+":"+user.getId());
//        if(count==null)
//        {
//            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
//        }else if(count<5)
//        {
//            valueOperations.increment(uri+":"+user.getId());
//        }else
//        {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /*
    * 验证码接口
    * */
    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(User user, @RequestParam("goodsId") Long goodsId, HttpServletResponse response)
    {
        log.info("进入验证码接口");
        if(user==null||goodsId<0)
        {
            throw  new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        // 生成验证码，放在redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48,3);
        log.info("准备存入redis中");
        //log.info(captcha.text());
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            log.info("输出验证码流");
            captcha.out(response.getOutputStream());
            log.info("返回结果");
        }catch (IOException e)
        {
            log.info("验证码生成失败"+e.getMessage());
        }
    }
}
