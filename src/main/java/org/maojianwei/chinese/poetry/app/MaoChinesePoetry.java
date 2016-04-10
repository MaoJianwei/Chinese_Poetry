package org.maojianwei.chinese.poetry.app;

import org.maojianwei.chinese.poetry.database.DatabaseCallable;
import org.maojianwei.chinese.poetry.database.PoetryItem;
import org.maojianwei.chinese.poetry.search.SearchCallable;
import org.maojianwei.chinese.poetry.spider.SpiderCallable;

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
    public static final int MAX_PAGE_COUNT = 3;

    public static void main(String args[]){

        LinkedBlockingQueue<String> linkQueue = new LinkedBlockingQueue();
        LinkedBlockingQueue<PoetryItem> poetryQueue = new LinkedBlockingQueue();
        AtomicBoolean needShutdown = new AtomicBoolean(false);
        AtomicBoolean pageComplete = new AtomicBoolean(false);

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(new SearchCallable(linkQueue,
                                       POETRY_URL_HEAD,
                                       FIRST_PAGE_SUFFIX,
                                       MAX_PAGE_COUNT,
                                       pageComplete,
                                       needShutdown));
        pool.submit(new SpiderCallable(linkQueue,
                                       poetryQueue,
                                       pageComplete,
                                       needShutdown));
        pool.submit(new DatabaseCallable(poetryQueue,
                                         needShutdown));
        pool.shutdown();
    }
}
