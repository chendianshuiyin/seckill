package com.cytus.seckilldemo.controller;

import com.cytus.seckilldemo.pojo.SeckillGoods;
import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.service.IGoodsService;
import com.cytus.seckilldemo.service.IUserService;
import com.cytus.seckilldemo.vo.DetailVo;
import com.cytus.seckilldemo.vo.GoodsVo;
import com.cytus.seckilldemo.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
@Slf4j //打印日志
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver; // 手动渲染

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){
        // 页面跳转
//        if(StringUtils.isEmpty(ticket))
//        {
//            // 未登录，跳转登录页面
//            return "login";
//        }
//        // Cookie中有ticket，ticket可以用作key在session中找到User
//        //User user = (User) session.getAttribute(ticket);
//        // 通过redis获取用户信息
//        User user = userService.getUserByCookie(ticket,request,response);
        model.addAttribute("user",user);// 用户对象传到前端页面去
        model.addAttribute("goodsList",goodsService.findGoodsVo());//将商品列表传进去
        // 不再做页面跳转,直接返回缓存的页面，或者手动渲染
        //return "goodsList";
        // 从redis获取页面，不为空则返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html))
        {
            return html;
        }
        // 如果为空手动渲染
        var application = JakartaServletWebApplication.buildApplication(request.getServletContext());
        var exchange = application.buildExchange(request, response);
        WebContext context =  new WebContext(exchange,request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",context);
        if(!StringUtils.isEmpty(html))
        {
            // 渲染成功
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
    }
    /* 跳转商品详情页(页面未静态化)
    */
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable(value = "goodsId") Long goodsId,HttpServletRequest request, HttpServletResponse response)
    {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:"+goodsId); // 从redis中根据商品id获取对应页面缓存
        if (!StringUtils.isEmpty(html))
        {
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo =  goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀倒计时
        int remainSeconds = 0;
        // 秒杀还未开始
        if(nowDate.before(startDate))
        {
            secKillStatus = 0;
            remainSeconds = ((int)(startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate))
        {
            // 秒杀结束
            remainSeconds = -1;
            secKillStatus = 2;
        }else
        {
            // 秒杀中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("secKillStatus",secKillStatus);
        // 如果为空手动渲染
        var application = JakartaServletWebApplication.buildApplication(request.getServletContext());
        var exchange = application.buildExchange(request, response);
        WebContext context =  new WebContext(exchange,request.getLocale(),model.asMap());
        html =  thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if(!StringUtils.isEmpty(html))
        {
            valueOperations.set("goodsDetail"+goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
    }

    /* 跳转商品详情页（静态化处理）
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable(value = "goodsId") Long goodsId)
    {
        GoodsVo goodsVo =  goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀倒计时
        int remainSeconds = 0;
        // 秒杀还未开始
        if(nowDate.before(startDate))
        {
            secKillStatus = 0;
            remainSeconds = ((int)(startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate))
        {
            // 秒杀结束
            remainSeconds = -1;
            secKillStatus = 2;
        }else
        {
            // 秒杀中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }
}
