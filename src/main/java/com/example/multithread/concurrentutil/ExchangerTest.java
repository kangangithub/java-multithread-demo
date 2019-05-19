package com.example.multithread.concurrentutil;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 线程间交换数据Exchanger: 用于进行线程间的数据交换。它提供一个同步点，在这个同步点，两个线程可以交换彼此的数据。
 * 这两个线程通过exchange()方法交换数据，如果第一个线程先执行exchange()方法，它会一直等待第二个线程也执行exchange方法，
 * 当两个线程都到达同步点时，这两个线程就可以交换数据，将本线程生产出来的数据传递给对方。如果两个线程有一个没有执行exchange()方法，
 * 则会一直等待，如果担心有特殊情况发生，避免一直等待，可以使用exchange（V x，long timeout，TimeUnit unit）设置最大等待时长。
 *
 * @Auther: Akang
 * @Date: 2019/4/11 11:10
 * @Description:
 */
public class ExchangerTest {
    // 设置计数器2
    private CountDownLatch countDownLatch = new CountDownLatch(2);
    // 线程间交换数据的exchanger
    private Exchanger exchanger = new Exchanger();

    /**
     * 用于校对工作，比如我们需要将纸制银行流水通过人工的方式录入成电子银行流水，为了避免错误，采用AB岗两人进行录入，
     * 录入到Excel之后，系统需要加载这两个Excel，并对两个Excel数据进行校对，看看是否录入一致
     *
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        System.out.println("main thread start");

        new Thread("thread1") {
            @Override
            public void run() {
                String a = "银行流水A";
                try {
                    // thread1执行exchange(),进入等待状态
                    String exchange = (String) exchanger.exchange(a, 1, TimeUnit.SECONDS); // 银行流水B
                    System.out.println(Thread.currentThread().getName() + ":" + exchange);
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread("thread2") {
            @Override
            public void run() {
                String b = "银行流水B";
                try {
                    // thread2执行exchange(),thread1和thread2到达同步点,交换数据,
                    String exchange = (String) exchanger.exchange(b); // 银行流水A
                    System.out.println(Thread.currentThread().getName() + ":" + exchange);
                    System.out.println("两个线程中的数据是否相同:" + StringUtils.equals(exchange, b)); //false
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        countDownLatch.await();
        System.out.println("main thread end");
    }
}
