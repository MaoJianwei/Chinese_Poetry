package org.maojianwei.chinese.poetry.database;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/10/16.
 */
public class DatabaseCallable implements Callable {

    private PoetryDatabase db;
    private LinkedBlockingQueue<PoetryItem> poetryItemQueue;
    private AtomicBoolean pageComplete;
    private AtomicBoolean needShutdown;
    public static final int QUEUE_POLL_TIMEOUT = 500;

    public DatabaseCallable(LinkedBlockingQueue poetryItemQueue, AtomicBoolean pageComplete, AtomicBoolean needShutdown) {
        this.db = null;
        this.poetryItemQueue = poetryItemQueue;
        this.pageComplete = pageComplete;
        this.needShutdown = needShutdown;
    }

    public Integer call() {
        db = new PoetryDatabase();
        boolean ret = db.initDatabase("MaoPoetry.db");
        System.out.print("Database: DB init -> ");
        System.out.println(ret);

        while (!needShutdown.get()) {

            //Attention - Mao: should not use pageComplete to quit, because parse Web cost much time ---> should use needShutdown to control
            try {
                System.out.println("Database: get poetry Item...");
                PoetryItem poetryItem = poetryItemQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);

                if (needShutdown.get()) {
                    break;
                }

                if (poetryItem == null) {
                    if (pageComplete.get()) {
                        System.out.println("Database: pageComplete set");
                        break;
                    } else {
                        System.out.println("Database: queue empty, wait...");
                        continue;
                    }
                }

                System.out.print("Database: get poetry Item OK ---> ");
                System.out.println(poetryItem.getTitle());

                /**
                 * some poetry have same title
                 *
                System.out.println("Database: checking item exist...");

                    if (db.checkExist(poetryItem)) {
                        System.out.println("Database: item exist, deleting...");
                        ret = db.deleteEntry(poetryItem);
                        System.out.println("Database: item exist, delete OK!");
                    }
                */

                System.out.println("Database: insert item...");
                ret = db.insertEntry(poetryItem);
                System.out.println("Database: insert item OK!");


                System.out.print("Database: poetry count: ");
                System.out.println(db.getRowCount());

            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Database: --------------------- poetryItemQueue poll error!!!");
            }
        }
        System.out.println("Database: shutdown, releasing...");
        db.releaseDatabase();
        System.out.println("Database: shutdown, release OK!");
        return 0;
    }
}
