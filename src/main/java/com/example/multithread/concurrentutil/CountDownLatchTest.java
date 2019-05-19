package com.example.multithread.concurrentutil;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 等待线程完成CountDownLatch: 允许一个或多个线程等待其他线程完成操作。作用类似join()
 * CountDownLatch的构造函数接收一个int类型的参数作为计数器，如果你想等待N个点完成，这里就传入N。
 * 当我们调用CountDownLatch的countDown方法时，N就会减1，CountDownLatch的await方法会阻塞当前线程，直到N变成零。
 * 由于countDown方法可以用在任何地方，所以这里说的N个点，可以是N个线程，也可以是1个线程里的N个执行步骤。
 * 用在多个线程时，只需要把这个CountDownLatch的引用传递到线程里即可。
 * CountDownLatch不可能重新初始化或者修改CountDownLatch对象的内部计数器的值。
 * 一个线程调用countDown方法happen-before，另外一个线程调用await方法。
 * countDown()计数器N-1
 * await()阻塞当前线程，直到N变成零。
 *
 * @Auther: Akang
 * @Date: 2019/4/10 17:04
 * @Description:
 */
public class CountDownLatchTest {
    // 设置计数器10
    private CountDownLatch countDownLatch = new CountDownLatch(10);

    @Test
    public void test1() throws InterruptedException {
        System.out.println("main thread start");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 子线程计数-1
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName() + "执行完毕, 此时CountDownLatch.count=" + countDownLatch.getCount());
            }).start();
        }

        // 阻塞main线程至countDownLatch计数为0
        countDownLatch.await();
        System.out.println("main thread end");
    }

}
