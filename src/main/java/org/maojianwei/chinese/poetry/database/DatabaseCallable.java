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
    private LinkedBlockingQueue poetryItemQueue;
    private AtomicBoolean needShutdown;
    public static final int QUEUE_POLL_TIMEOUT = 500;

    public DatabaseCallable(LinkedBlockingQueue poetryItemQueue, AtomicBoolean needShutdown){
        this.db = null;
        this.poetryItemQueue = poetryItemQueue;
        this.needShutdown = needShutdown;
    }

    public Integer call(){
        db = new PoetryDatabase();
        boolean ret = db.initDatabase("MaoPoetry.db");
        System.out.print("Database: DB init -> ");
        System.out.println(ret);

        while(!needShutdown.get()){

            try {

                Object objPoetry = poetryItemQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);

                if(needShutdown.get()){
                    break;
                }

                if(objPoetry == null){
                    continue;
                }
                MaoPoetryItem item = (MaoPoetryItem) objPoetry;

                System.out.println("Database: get poetry Item");

                try {
                    System.out.println("Database: checking item exist...");
                    if(db.checkExist(item)){
                        System.out.println("Database: item exist, deleting...");
                        ret = db.deleteEntry(item);
                        System.out.println("Database: item exist, delete OK!");
                    }
                    System.out.println("Database: insert item...");
                    ret = db.insertEntry(item);
                    System.out.println("Database: insert item OK!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                System.out.print("Database: poetry count: ");
                System.out.println(db.getRowCount());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print("Database: shutdown, releasing...");
        db.releaseDatabase();
        System.out.print("Database: shutdown, release OK!");
        return 0;
    }
}
