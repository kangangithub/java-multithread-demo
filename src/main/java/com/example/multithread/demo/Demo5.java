package com.example.multithread.demo;

import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * 3个线程交替打印,线程1:A,线程2:B,线程3打印C,结果ABCABCABC....,保证线程1先打印
 * Semaphore--控制并发数
 * Semaphore是用来保护一个或者多个共享资源的访问，Semaphore内部维护了一个计数器，其值为可以访问的共享资源的个数。
 * 一个线程要访问共享资源，先获得信号量，如果信号量的计数器值大于1，意味着有共享资源可以访问，则使其计数器值减去1，再访问共享资源。
 * 如果计数器值为0,线程进入休眠。当某个线程使用完共享资源后，释放信号量，并将信号量内部的计数器加1，
 * 之前进入休眠的线程将被唤醒并再次试图获得信号量。
 * Semaphore使用时需要先构建一个参数来指定共享资源的数量，Semaphore构造完成后即是获取Semaphore、共享资源使用完毕后释放Semaphore。
 *
 * @Auther: Akang
 * @Date: 2019/5/15 17:42
 * @Description:
 */
public class Demo5 {
    private Semaphore semaphore1 = new Semaphore(1);
    private Semaphore semaphore2 = new Semaphore(0);
    private Semaphore semaphore3 = new Semaphore(0);

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> print("A", semaphore1, semaphore2)).start();
        Thread.sleep(10);
        new Thread(() -> print("B", semaphore2, semaphore3)).start();
        Thread.sleep(10);
        new Thread(() -> print("C", semaphore3, semaphore1)).start();
        Thread.sleep(1000);
    }

    private void print(String s, Semaphore s1, Semaphore s2) {
        for (int i = 0; i < 10; i++) {
            /**
             * 初始(A=1,B=0,C=0)—>第一次执行线程A时(A=1,B=0,C=0)—->第一次执行线程B时（A=0,B=1,C=0）—->
             * 第一次执行线程C时(A=0,B=0,C=1)—>第二次执行线程A(A=1,B=0,C=0)如此循环
             * 1.线程1,s1获取许可,s1信号量-1,打印A,s2释放许可,s2信号量+1 : (A=1,B=0,C=0) --> (A=0,B=1,C=0)
             * 2.线程2,s2获取许可,s2信号量-1,打印B,s3释放许可,s3信号量+1 : (A=0,B=1,C=0) --> (A=0,B=0,C=1)
             * 3.线程3,s3获取许可,s3信号量-1,打印C,s1释放许可,s1信号量+1 : (A=0,B=0,C=1) --> (A=1,B=0,C=0)
             * 4.线程1,s1获取许可,s1信号量-1,打印A,s2释放许可,s2信号量+1 : (A=1,B=0,C=0) --> (A=0,B=1,C=0)
             * .....
             */
            try {
                s1.acquire(); // s1获取信号执行,s1信号量-1,当s1信号量为0时将无法继续获得该信号量
                System.out.print(s);
                s2.release(); // s2释放信号量,s2信号量+1
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
