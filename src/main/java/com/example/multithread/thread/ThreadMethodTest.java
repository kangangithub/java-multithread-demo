package com.example.multithread.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

/**
 * Thread中各种方法
 * <p/>
 * Created by SYSTEM on 2019/03/28.
 */
public class ThreadMethodTest {

    /**
     * start()才能启动线程
     * run()不能启动线程, 只是普通方法的调用, run方法体定义具体的执行逻辑
     * getName()获取调用线程名称
     * getId()获取调用线程ID
     * getState()获取调用线程状态
     * getPriority()获取线程优先级
     */
    @Test
    public void test1() {
        Thread thread1 = new MyThread();
        Thread thread2 = new MyThread();

        thread1.start();
        thread2.run();

        // thread1已经启动, thread2未启动(未调用start()启动)
        System.out.println("thread1状态: " + thread1.getState()); // RUNNABLE
        System.out.println("thread2状态: " + thread2.getState()); // NEW

        System.out.println("thread1的ID: " + thread1.getId()); // 12
        System.out.println("thread1的name: " + thread1.getName()); // Thread-0
        System.out.println("thread1的优先级: " + thread1.getPriority()); // 5
    }

    /**
     * currentThread()获取当前线程对象
     * isAlive()判断当前线程是否处于活动状态, 因为判断的是当前线程的活动状态, 所以避免用线程实例去调用该方法
     * 活动状态: 已经启动且尚未终止, 阻塞,运行,可运行状态都属于活动状态
     * sleep()阻塞当前线程且不释放锁标记
     */
    @Test
    public void test2() throws InterruptedException {
        Thread thread1 = new MyThread();
        Thread thread2 = new MyThread();

        thread1.start();
        thread2.run();

        // thread1已经启动, thread2未启动(未调用start()启动)
        System.out.println("thread1状态: " + thread1.isAlive()); // true
        System.out.println("thread2状态: " + thread2.isAlive()); // false

        // 当前线程sleep2秒
        Thread.sleep(2000);

        // 当前线程处于活动状态, 而thread1已经在2秒内执行完毕, 所以thread1.isAlive()为false
        System.out.println(Thread.currentThread() + "状态: " + Thread.currentThread().isAlive()); // main线程 true
        System.out.println("thread1状态: " + thread1.isAlive()); // false
        System.out.println("thread2状态: " + thread2.isAlive()); // false
    }

    /**
     * join()/join(long millis)阻塞其他线程，等待调用线程终止。thread1.join()，则阻塞main，直到thread1进程运行结束，main再由阻塞转为就绪状态。
     * join()底层通过wait()实现
     * yield()暂停当前线程，不阻塞当前线程, 把执行机会让给其他线程。使得线程放弃当前分得的 CPU 时间，但是不使当前线程阻塞，即线程仍处于可执行状态，
     * 随时可能再次分得 CPU 时间。调用 yield() 的效果等价于调度程序认为该线程已执行了足够的时间从而转到另一个线程.
     */
    @Test
    public void test3() throws InterruptedException {
        Thread thread1 = new Thread("thread1") {
            @Override
            public void run() {
                while (true) {
                    System.out.println("thread1");
                }
            }
        };
        thread1.start();

        // 等待thread1线程终止, thread1的run()运行结束.main方法的打印才进行
        thread1.join();

        while (true) {
            System.out.println("main");
        }
    }

    /**
     * interrupt()打断调用线程, 仅仅在线程中打停止标记, 线程不会立刻停止
     * isInterrupted()判断调用线程是否被打断, 不清除停止标记
     * interrupted()判断当前线程是否被打断, 清除停止标记
     * public static boolean interrupted() {
     * return currentThread().isInterrupted(true);
     * }
     */
    @Test
    public void test4() {
        try {
            Thread thread1 = new MyThread();
            thread1.start();

            // 打断thread1线程
            thread1.interrupt();
            System.out.println(thread1.isInterrupted()); // true
            System.out.println(thread1.isInterrupted()); // true

            // 打断main线程
            Thread.currentThread().interrupt();
            System.out.println(Thread.interrupted()); // true
            System.out.println(Thread.interrupted()); //false
        } catch (Exception e) {
            System.out.println("线程被打断");
            e.printStackTrace();
        }
    }

    /**
     * wait()阻塞当前线程并释放锁
     * notify()随机唤醒一个线程
     * notifyAll()唤醒所有线程
     */
    @Test
    public void test5() {
    }

    /**
     * MyThread类
     */
    @Data
    @NoArgsConstructor
    class MyThread extends Thread {

        @Override
        public void run() {
            try {
                System.out.println("线程名称: " + Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
