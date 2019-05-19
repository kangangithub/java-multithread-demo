package com.example.multithread.lock;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport定义了一组的公共静态方法，这些方法提供了最基本的线程阻塞和唤醒功能，而LockSupport也成为构建同步组件的基础工具。
 * park开头的方法用来阻塞当前线程，以及unpark(Thread thread)方法来唤醒一个被阻塞的线程
 *
 * @Auther: Akang
 * @Date: 2019/4/12 10:17
 * @Description:
 */
public class LockSupportTest {

    @Test
    public void lockSupportTest() throws InterruptedException {
        Thread thread2 = new Thread(() -> {
            // 阻塞thread2线程
            LockSupport.park();
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }, "thread2");
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
            // thread1执行完毕后,唤醒thread2线程
            LockSupport.unpark(thread2);
        }, "thread1");

        thread1.start();
        thread2.start();

        // 阻塞main线程
        Thread.sleep(1000);


    }
}
