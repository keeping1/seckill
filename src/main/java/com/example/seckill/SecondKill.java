package com.example.seckill;


import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class SecondKill {

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping()
    @Scheduled(fixedRate=100)
    public void kill() throws InterruptedException {
        //创建分布式信号量
        RSemaphore semaphore = redissonClient.getSemaphore("secondKillsemaphore");
        RBucket<Integer> rBucket = redissonClient.getBucket("storeOfNumber");
        if (semaphore.tryAcquire(10, TimeUnit.SECONDS)){
            RLock lock = redissonClient.getLock("secondKillLock");
            try {
                lock.lock();
                if (rBucket.get()<=0){
                    System.out.println("卖完了");
                    return;
                }
                rBucket.set(rBucket.get()-1);
                System.out.println("库存还剩："+rBucket.get());
            }finally {
                lock.unlock();//解锁
            }

        }else {
            System.out.println("没抢到·");
        }
    }

}
