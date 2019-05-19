package com.example.multithread.lock;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁ReentrantLock: 支持重进入的锁，它表示该锁能够支持一个线程对资源的重复加锁
 * Condition提供监视器方法,
 * condition.await(); //this.wait();
 * condition.signal(); //this.notify();
 * condition.signalAll(); //this.notifyAll();
 *
 * @Auther: Akang
 * @Date: 2019/4/11 11:44
 * @Description:
 */
public class ReentrantLockAndConditionTest {
    // 重入锁
    private Lock lock = new ReentrantLock();
    // Condition
    private Condition condition = lock.newCondition();
    // 静态属性,打印初始值,两个线程竞争打印--共享资源
    private static int a = 1;

    @Test
    public void awaitAndSignal() throws InterruptedException {
        new Thread(() -> print()).start();
        Thread.sleep(10);
        new Thread(() -> print()).start();
        Thread.sleep(1000);
    }

    /**
     * tryLock()/lock()获取锁
     * unlock()释放锁
     * signal()随机唤醒一个线程,同notify()
     * await()阻塞当前线程,释放锁,同wait()
     */
    public void print() {
        int i = 0;
        while (a < 100) {
            try {
                lock.tryLock();
                System.out.println(Thread.currentThread().getName() + " : " + a);
                a++;
                i++;

                if (i > 2) {
                    i = 0;
                    condition.signal();
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
