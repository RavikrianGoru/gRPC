package in.rk;

import java.util.concurrent.CountDownLatch;

class MyRunnable implements Runnable
{
    CountDownLatch latch ;
    public MyRunnable(CountDownLatch latch)
    {
        this.latch=latch;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+": current latch count :(before):"+latch.getCount());
        try
        {
            Thread.sleep(3000);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        latch.countDown();
        System.out.println(Thread.currentThread().getName() + " : has reduced latch");
        System.out.println(Thread.currentThread().getName() + " : current latch count(after):"+latch.getCount());

    }
}
public class CountdownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("App started");
        CountDownLatch latch=new CountDownLatch(2);
        System.out.println("CountDownLatch is initialized with 2 and passed to all threads.");
        new Thread(new MyRunnable(latch)).start();
        new Thread(new MyRunnable(latch)).start();
        latch.await();
        System.out.println("Two threads started & called latch.await()");
        new Thread(new MyRunnable(latch)).start();
        System.out.println("App ended");

    }
}
