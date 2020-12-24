package com.distributed.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperLockDemo {
    private static String path = "/locking";

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("10.0.0.3:2181",
                5000, 5000, retryPolicy);
        client.start();
        InterProcessSemaphoreMutex interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(client, path);
        for (int i = 0; i < 20; i++) {
            //开启自线程
            new Thread(() -> {
                try {
                    //加锁
                    interProcessSemaphoreMutex.acquire();
                    System.out.println(Thread.currentThread().getName() + " 获取锁");
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    try {
                        interProcessSemaphoreMutex.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
