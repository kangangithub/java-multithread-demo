package com.example.multithread.concurrentutil;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 控制并发线程数的信号量Semaphore: 控制同时访问特定资源的线程数量
 * 信号量Semaphore比作是控制流量的红绿灯。比如××马路要限制流量，只允许同时有100辆车在这条路上行使，其他的都必须在路口等待，
 * 所以前100辆车会看到绿灯，可以开进这条马路，后面的车会看到红灯，不能驶入××马路，但是如果前100辆中有5辆车已经离开了××马路，
 * 那么后面就允许有5辆车驶入马路，这个例子里说的车就是线程，驶入马路就表示线程在执行，离开马路就表示线程执行完成，
 * 看见红灯就表示线程被阻塞，不能执行。
 *
 * @Auther: Akang
 * @Date: 2019/4/10 18:47
 * @Description:
 */
public class SemaphoreTest {
    // 线程池大小
    private final int POOL_SIZE = 10;
    // 创建最大线程数为10的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
    // 设置最大并发量5
    private Semaphore semaphore = new Semaphore(5);

    /**
     * 虽然executorService有10个线程在执行，但是只允许5个并发执行
     */
    @Test
    public void test1() throws InterruptedException {
        System.out.println("main thread start");

        // 创建20个Task执行
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                try {
                    // 获得许可证
                    semaphore.acquire();
                    // Task执行
                    System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().getId());
                    // 释放许可证
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        // 关闭线程池
        executorService.shutdown();

        // 线程池未执行完毕,主线程休眠1秒,直到线程池执行完毕,main线程再执行
        while (!executorService.isTerminated()) {
            Thread.sleep(1000L);
        }
        System.out.println("main thread end");
    }
}
