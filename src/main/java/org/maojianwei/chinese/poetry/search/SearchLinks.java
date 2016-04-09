package org.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 */
public class SearchLinks {

    public static final String firstPagesuffix = "/type.aspx?p=1";
    public static final String POETRY_URL_HEAD = "http://so.gushiwen.org";
    public static final int MAX_PAGE_COUNT = 3;


    public static void main(String[] args) {

        LinkedBlockingQueue linkQueue = new LinkedBlockingQueue();

        ExecutorService pool = Executors.newCachedThreadPool();

        pool.submit(new SearchCallable(linkQueue, POETRY_URL_HEAD, firstPagesuffix, MAX_PAGE_COUNT));

        pool.shutdown();
    }


}
