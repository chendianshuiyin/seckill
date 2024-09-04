package com.cytus.seckilldemo;

import com.cytus.seckilldemo.pojo.Goods;
import com.cytus.seckilldemo.service.IGoodsService;
import com.cytus.seckilldemo.vo.GoodsVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillDemoApplicationTests {

    @Test
    void contextLoads() {
    }
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private RedisScript redisScript;
//    @Test
//    public void testLock01()
//    {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // 占位，key不存在才能设置成功
//        Boolean isLock = valueOperations.setIfAbsent("k1","v1");
//        // 如果占位成功
//        if(isLock)
//        {
//            valueOperations.set("name","cytus");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name="+name);
//            // 操作结束，删除锁
//            redisTemplate.delete("k1");
//        }else
//        {
//            System.out.println("有线程在使用，请稍后再尝试");
//        }
//    }
//    @Test
//    public void testlock02()
//    {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // 占位，key不存在才能设置成功
//        Boolean isLock = valueOperations.setIfAbsent("k1","v1",5, TimeUnit.SECONDS);
//        // 如果占位成功
//        if(isLock)
//        {
//            valueOperations.set("name","cytus");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name="+name);
//            // 操作结束，删除锁
//            redisTemplate.delete("k1");
//        }else
//        {
//            System.out.println("有线程在使用，请稍后再尝试");
//        }
//    }
//
//    @Test
//    public void testlock03()
//    {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // 占位，key不存在才能设置成功
//        String value =  UUID.randomUUID().toString();// 为锁生成一个随机key
//        Boolean isLock = valueOperations.setIfAbsent("k1",value,5, TimeUnit.SECONDS); // 设置一个失效时间
//        // 如果占位成功
//        if(isLock)
//        {
//            valueOperations.set("name","cytus");
//            String name = (String) valueOperations.get("name");
//            System.out.println("name="+name);
//            System.out.println("锁的key值"+valueOperations.get("k1"));
//            // 操作结束，删除锁
//            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"),value);
//            System.out.println(result);
//        }else
//        {
//            System.out.println("有线程在使用，请稍后再尝试");
//        }
//    }
}
