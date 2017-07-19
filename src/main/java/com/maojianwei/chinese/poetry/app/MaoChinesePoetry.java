package com.maojianwei.chinese.poetry.app;

import com.maojianwei.chinese.poetry.database.DatabaseCallable;
import com.maojianwei.chinese.poetry.log.LogSystem;
import com.maojianwei.chinese.poetry.search.SearchCallable;
import com.maojianwei.chinese.poetry.database.PoetryItem;
import com.maojianwei.chinese.poetry.spider.SpiderCallable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/9/16.
 */
public class MaoChinesePoetry {

    public static final String FIRST_PAGE_SUFFIX = "/type.aspx?p=1";
    public static final String POETRY_URL_HEAD = "http://so.gushiwen.org";
    public static final int MAX_PAGE_COUNT = 200;
    public static final int QUEUE_POLL_TIMEOUT = 500;

    public static void main(String args[]){

        LogSystem.initAppLogSystem();

        LinkedBlockingQueue<String> linkQueue = new LinkedBlockingQueue();
        LinkedBlockingQueue<PoetryItem> poetryQueue = new LinkedBlockingQueue();
        AtomicBoolean needShutdown = new AtomicBoolean(false);
        AtomicBoolean linkComplete = new AtomicBoolean(false);
        AtomicBoolean pageComplete = new AtomicBoolean(false);

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(new SearchCallable(linkQueue,
                                       POETRY_URL_HEAD,
                                       FIRST_PAGE_SUFFIX,
                                       MAX_PAGE_COUNT,
                                       linkComplete,
                                       needShutdown));
        pool.submit(new SpiderCallable(linkQueue,
                                       poetryQueue,
                                       QUEUE_POLL_TIMEOUT,
                                       linkComplete,
                                       pageComplete,
                                       needShutdown));
        pool.submit(new DatabaseCallable(poetryQueue,
                                         QUEUE_POLL_TIMEOUT,
                                         pageComplete,
                                         needShutdown));
        pool.shutdown();
    }
}
