package com.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hello world!
 */
public class SearchLinks {

    public static final String FIRST_PAGE_SUFFIX = "/type.aspx?p=1";
    public static final String POETRY_URL_HEAD = "http://so.gushiwen.org";
    public static final int MAX_PAGE_COUNT = 3;


    public static void main(String[] args) {

        LinkedBlockingQueue linkQueue = new LinkedBlockingQueue();
        AtomicBoolean needShutdown = new AtomicBoolean(false);
        AtomicBoolean pageComplete = new AtomicBoolean(false);

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(new SearchCallable(linkQueue,
                                       POETRY_URL_HEAD,
                                       FIRST_PAGE_SUFFIX,
                                       MAX_PAGE_COUNT,
                                       pageComplete,
                                       needShutdown));

        pool.shutdown();
    }


}
