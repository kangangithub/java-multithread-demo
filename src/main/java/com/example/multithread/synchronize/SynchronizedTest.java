package com.example.multithread.synchronize;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SynchronizedTest: synchronized同步锁
 *
 * @Auther: Akang
 * @Date: 2019/4/11 14:47
 * @Description:
 */
public class SynchronizedTest {
    // 初始值
    private volatile int i = 0;
    // 锁
    private final Object lock = new Object();
    // 创建最大线程数为10的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void test1() throws InterruptedException {
        System.out.println("main thread start");
        for (int j = 0; j < 20; j++) {
            executorService.submit((Runnable) this::synchronizedMethod);
//            executorService.submit((Runnable) this::synchronizedCodeBlock);
        }
        executorService.shutdown();

        // 阻塞main方法3秒
        while (!executorService.isTerminated()) {
            Thread.sleep(500);
        }
        System.out.println("main thread end");
    }

    /**
     * 同步方法
     */
    private synchronized int synchronizedMethod() {
        for (int j = 0; j < 10; j++) {
            i++;
        }
        System.out.println("synchronizedMethod:" + i);
        return i;
    }

    /**
     * 同步代码块
     */
    private int synchronizedCodeBlock() {
        synchronized (lock) {
            for (int j = 0; j < 10; j++) {
                i++;
            }
            System.out.println("synchronizedCodeBlock:" + i);
        }
        return i;
    }
}

/**
 * 懒汉式单例: 双检锁/双重校验锁（DCL，即 double-checked locking）
 */
class SingletonLazy {
    private volatile static SingletonLazy singletonLazy;

    private SingletonLazy() {
    }

    public static SingletonLazy getInstance() {
        if (singletonLazy == null) {
            synchronized (SingletonLazy.class) {
                if (singletonLazy == null) {
                    singletonLazy = new SingletonLazy();
                }
            }
        }
        return singletonLazy;
    }
}

/**
 * 饿汉式单例: 类加载时完成实例化,避免线程同步问题
 */
class SingletonHungry {
    private final static SingletonHungry SINGLETON_HUNGRY = new SingletonHungry();

    private SingletonHungry() {
    }

    public static SingletonHungry getInstance() {
        return SINGLETON_HUNGRY;
    }
}
