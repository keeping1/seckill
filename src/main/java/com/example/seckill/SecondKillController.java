package com.example.seckill;

import org.redisson.api.RBucket;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondKillController {
    @Autowired
    RedissonClient redissonClient;

    private Integer number;

    @RequestMapping("/release/{number}")
    public String release(@PathVariable Integer number) {
        RSemaphore r = redissonClient.getSemaphore("secondKillsemaphore");
        r.release(number);//释放信号
        return "ok";
    }
    @RequestMapping("/setStoreNumber/{number}")
    public String setStoreNumber(@PathVariable Integer number){
        RBucket<Integer> rBucket = redissonClient.getBucket("storeOfNumber");
        rBucket.set(number);
        return "ok";
    }

}
