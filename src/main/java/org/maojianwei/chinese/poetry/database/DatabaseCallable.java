package org.maojianwei.chinese.poetry.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private AtomicBoolean needShutdown;

    private final int QUEUE_POLL_TIMEOUT;

    private Logger log = LoggerFactory.getLogger(getClass());


    public DatabaseCallable(LinkedBlockingQueue poetryItemQueue, int queuePollTimeout, AtomicBoolean needShutdown) {
        this.db = null;
        this.poetryItemQueue = poetryItemQueue;
        this.needShutdown = needShutdown;
        this.QUEUE_POLL_TIMEOUT = queuePollTimeout;
    }

    public Integer call() {

        Thread.currentThread().setName("Mao_Database");

        db = new PoetryDatabase();
        boolean ret = db.initDatabase("MaoPoetry.db");

        log.info("DB init -> {}", ret);


        while (!needShutdown.get()) {

            //Attention - Mao: should not use pageComplete to quit, because parse Web cost much time ---> should use needShutdown to control
            try {
                log.info("get poetry Item...");
                PoetryItem poetryItem = poetryItemQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);

                if (needShutdown.get()) {
                    break;
                }

                if (poetryItem == null) {
                    continue;
                }

                log.info("get poetry Item OK ---> {}", poetryItem.getTitle());

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

                log.info("insert item...");
                ret = db.insertEntry(poetryItem);
                log.info("insert item OK!");

                log.info("poetry count: {}", db.getRowCount());

            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("------------- poetryItemQueue poll error!!!");
            }
        }
        log.info("shutdown, releasing...");
        db.releaseDatabase();
        log.info("shutdown, release OK!");
        return 0;
    }
}
