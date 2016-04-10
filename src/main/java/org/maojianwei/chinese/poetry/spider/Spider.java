package org.maojianwei.chinese.poetry.spider;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hello world!
 *
 */
public class Spider
{
    public static void main( String[] args )
    {
        //Deprecated, 2016.04.10

        LinkedBlockingQueue linkQueue = new LinkedBlockingQueue();
        AtomicBoolean needShutdown = new AtomicBoolean(false);
        AtomicBoolean pageComplete = new AtomicBoolean(false);

        linkQueue.offer("http://so.gushiwen.org/view_71139.aspx");
        linkQueue.offer("http://so.gushiwen.org/view_71137.aspx");
        linkQueue.offer("http://so.gushiwen.org/view_49386.aspx");
        linkQueue.offer("http://so.gushiwen.org/view_7722.aspx");

        ExecutorService pool = Executors.newCachedThreadPool();
        //pool.submit(new SpiderCallable(linkQueue, pageComplete, needShutdown));
        pool.shutdown();

        pageComplete.set(true);

    }
}
