package com.example.multithread.lock;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock读写锁:
 * 分为读锁和写锁，多个读锁不互斥，读锁与写锁互斥，这是由jvm自己控制的，我们只要上好相应的锁即可。
 * 如果只读数据，可以很多人同时读，但不能同时写，那就上读锁；
 * 如果修改数据，只能有一个人在写，且不能同时读取，那就上写锁。
 * 总之，读的时候上读锁，写的时候上写锁！
 * <p>
 * 读写锁的机制：
 * "读-读" 不互斥
 * "读-写" 互斥
 * "写-写" 互斥
 *
 * @Auther: Akang
 * @Date: 2019/4/10 16:15
 * @Description:
 */
public class ReentrantReadWriteLockTest {
    static Map<String, Object> map = new HashMap<>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock r = rwl.readLock();
    static Lock w = rwl.writeLock();

    // 获取一个key对应的value
    public static final Object get(String key) {
        r.lock();
        try {
            System.out.println(Thread.currentThread().getName() + ":获取读锁:get");
            return map.get(key);
        } finally {
            System.out.println(Thread.currentThread().getName() + ":释放读锁:get");
            r.unlock();
        }
    }

    // 设置key对应的value，并返回旧的value
    public static final Object put(String key, Object value) {
        w.lock();
        try {
            System.out.println(Thread.currentThread().getName() + ":获取写锁:put");
            return map.put(key, value);
        } finally {
            System.out.println(Thread.currentThread().getName() + ":释放写锁:put");
            w.unlock();
        }
    }

    // 清空所有的内容
    public static final void clear() {
        w.lock();
        try {
            System.out.println(Thread.currentThread().getName() + ":获取写锁:clear");
            map.clear();
        } finally {
            System.out.println(Thread.currentThread().getName() + ":获取写锁:clear");
            w.unlock();
        }
    }

    @Test
    public void reentrantReadWriteLockTest() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread("thread:write:" + i) {
                @Override
                public void run() {
                    int value = new Random().nextInt(1000);
                    put("key", value);
                    System.out.println(Thread.currentThread().getName() + ":put:" + value);
                }
            }.start();

            new Thread("thread:read:" + i) {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ":get:" + get("key"));
                }
            }.start();
        }

        // 阻塞main方法3秒
        Thread.sleep(3000);
    }
}
