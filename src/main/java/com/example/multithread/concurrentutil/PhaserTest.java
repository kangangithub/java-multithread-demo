package com.example.multithread.concurrentutil;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Phaser是一个更加复杂和强大的同步辅助类，它允许并发执行多阶段任务。当我们有并发任务并且需要分解成几步执行时，
 * （CyclicBarrier是分成两步），就可以选择使用Phaser。
 * Phaser类机制是在每一步结束的位置对线程进行同步，当所有的线程都完成了这一步，才允许执行下一步。
 * 跟其他同步工具一样，必须对Phaser类中参与同步操作的任务数进行初始化，不同的是，可以动态的增加或者减少任务数。
 * <p>
 * 一个Phaser对象有两种状态：
 * 活跃态（Active）：当存在参与同步的线程的时候，Phaser就是活跃的，并且在每个阶段结束的时候进行同步。
 * 终止态（Termination）：当所有参与同步的线程都取消注册的时候，Phaser就处于终止态，
 * 在终止状态下，Phaser没有任何参与者。当Phaser对象onAdvance()方法返回True时，Phaser对象就处于终止态。
 * 当Phaser处于终止态时，同步方法arriveAndAwaitAdvance()会立即返回，而且不会做任何同步操作。
 * <p>
 * 常用方法:
 * arriveAndAwaitAdvance():类似于CyclicBarrier的await()方法，等待其它线程都到来之后同步继续执行
 * arriveAndDeregister()：把执行到此的线程从Phaser中注销掉
 * isTerminated():判断Phaser是否终止
 * register():将一个新的参与者注册到Phaser中，这个新的参与者将被当成没有执行完本阶段的线程
 * forceTermination():强制Phaser进入终止态
 * <p>
 * 示例：
 * 这里模拟了一个结婚的场景
 * 自定义一个MarriagePhaser 继承 Phaser 重写Phaser的onAdvance方法定义了4个阶段（进入下一个阶段时该方法被自动调用）。阶段必须从0开始。
 * onAdvice的两个参数 phase是第几个阶段，registeredParties是目前有几个已注册线程参加。
 * 最后返回值为false表示流程未结束，继续执行下一阶段，返回true表示流程结束。
 */
public class PhaserTest {
    private final MarriagePhaser phaser = new MarriagePhaser();

    static class MarriagePhaser extends Phaser {
        /**
         * 当Phaser对象onAdvance()方法返回True时，Phaser对象就处于终止态。
         * @param phase 第几个阶段
         * @param registeredParties 目前有几个已注册线程参加
         * @return false表示流程未结束，继续执行下一阶段，返回true表示流程结束。
         */
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            // 定义4个阶段，一个阶段结束时该方法被自动调用
            switch (phase) {
                case 0:
                    System.out.println("所有人到齐！" + registeredParties + "\n");
                    return false; // 流程未结束，继续
                case 1:
                    System.out.println("所有人都吃完饭了！" + registeredParties + "\n");
                    return false; // 流程未结束，继续
                case 2:
                    System.out.println("客人都离开了！" + registeredParties + "\n");
                    return false; // 流程未结束，继续
                case 3:
                    System.out.println("婚礼结束!" + registeredParties + "\n");
                    return true; // 流程结束
                default:
                    return true;
            }
        }
    }

    // 定义Persion类，实现Runnable ，重写run方法参加婚礼
    class Persion implements Runnable {

        String name;

        public Persion(String name) {
            this.name = name;
        }

        // 提取sleep方法
        public void secondsSleep(int seconds) {
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 阶段 1 行为
        public void arrive() {
            secondsSleep(2);
            System.out.println(name + "到达现场！");
            phaser.arriveAndAwaitAdvance(); // 阶段结束放行
        }

        // 阶段 2 行为
        public void eat() {
            secondsSleep(2);
            System.out.println(name + "吃完饭！");
            phaser.arriveAndAwaitAdvance(); // 阶段结束放行
        }

        // 阶段 3 行为
        public void leave() {
            if ("新郎".equals(name) || "新娘".equals(name)) {
                secondsSleep(2);
                System.out.println(name + "留下！");
                phaser.arriveAndAwaitAdvance(); // 阶段结束放行
            } else {
                System.out.println(name + "离开！");
                /*
                 * 客人离开后从phaser中注销，不再受阶段控制
                 * 注意：这时客人的线程并没有结束，只是不再受phaser影响可自由往下执行
                 * 注销后可减少下一阶段中需要提前加入的线程，提高效率
                 */
                phaser.arriveAndDeregister(); // 从phaser中注销
            }

        }

        // 阶段 4 行为
        public void hug() {
            if ("新郎".equals(name) || "新娘".equals(name)) {
                secondsSleep(2);
                System.out.println(name + "拥抱！");
                phaser.arriveAndAwaitAdvance();
            }
        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            hug();
        }
    }

    @Test
    public void test1() throws InterruptedException {
        System.out.println("main thread start");
        // 注册7个线程,5个客人+新娘+新郎
        phaser.bulkRegister(7);

        for (int i = 0; i < 5; i++) {
            new Thread(new Persion("客人" + i)).start();
        }
        new Thread(new Persion("新郎")).start();
        new Thread(new Persion("新娘")).start();
        Thread.sleep(10000);
        System.out.println("main thread end");
    }

}
