package com.example.multithread.concurrentutil;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 同步屏障CyclicBarrier: 让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行。
 * CountDownLatch的计数器只能使用一次，而CyclicBarrier的计数器可以使用reset()方法重置。
 * <p>
 * CyclicBarrier（int parties），其参数表示屏障拦截的线程数量，每个线程调用await()方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。
 * CyclicBarrier（int parties，Runnable barrier-Action），用于在线程到达屏障时，优先执行barrierAction
 * await() 告诉CyclicBarrier当前线程已经到达了同步屏障
 * getParties() 获取到达同步屏障需要的线程数
 * getNumberWaiting() 返回屏障处等待的线程数
 * isBroken() 阻塞的线程是否被中断
 *
 * @Auther: Akang
 * @Date: 2019/4/10 17:31
 * @Description:
 */
public class CyclicBarrierTest {
    // 拦截10个线程
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> System.out.println("10个线程都到达同步屏障后，优先执行"));

    @Test
    public void test1() throws BrokenBarrierException, InterruptedException {
        System.out.println("main thread start");
        for (int i = 0; i < 9; i++) {
            new Thread(() -> {
                try {
                    // 当前线程到达同步屏障
                    System.out.println(Thread.currentThread().getName() + "到达了同步屏障,此时屏障处等待的线程数:" + cyclicBarrier.getNumberWaiting());
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 当前线程到达同步屏障
        System.out.println(Thread.currentThread().getName() + "到达了同步屏障,此时屏障处等待的线程数:" + cyclicBarrier.getNumberWaiting());
        cyclicBarrier.await();

        // 当10个线程都到达同步屏障后再执行main线程
        System.out.println("main thread end");
    }
}
