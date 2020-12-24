package com.distributed.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;

/**
 * Hello world!
 */
public class RedisLockDemo {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.useSingleServer().setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);
        RLock rLock = redissonClient.getLock("lock");
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                rLock.lock();
                System.out.println(Thread.currentThread().getName() + " 获取锁");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    rLock.unlock();
                }
            }).start();
        }
        //等待子线程运行结束
        System.in.read();
        //关闭redis客户端
        redissonClient.shutdown();
    }
}
