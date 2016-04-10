package org.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/9/16.
 */
public class SearchCallable implements Callable {

    private final String FIRST_PAGE_SUFFIX;
    private final String POETRY_URL_HEAD;// = "http://so.gushiwen.org";
    private final int MAX_PAGE_COUNT;// = 3;

    private LinkedBlockingQueue<String> linkQueue;
    private AtomicBoolean needShutdown;
    private AtomicBoolean pageComplete;

    private Logger log = LoggerFactory.getLogger(getClass());



    public SearchCallable(
            LinkedBlockingQueue queue,
            String poetryUrlHead,
            String firstPageSuffix,
            int maxPageCount,
            AtomicBoolean pageComplete,
            AtomicBoolean needShutdown) {

        this.linkQueue = queue;
        this.POETRY_URL_HEAD = poetryUrlHead;
        this.MAX_PAGE_COUNT = maxPageCount;
        this.FIRST_PAGE_SUFFIX = firstPageSuffix;
        this.pageComplete = pageComplete;
        this.needShutdown = needShutdown;
    }


    public Integer call() {

        Thread.currentThread().setName("Mao_Search");

        String pageLink = POETRY_URL_HEAD + FIRST_PAGE_SUFFIX;
        int pageCount = 1;

        while (!needShutdown.get() && !pageLink.equals("") && pageCount <= MAX_PAGE_COUNT) {
            try {
                Document doc = Jsoup.connect(pageLink).get();

                if(needShutdown.get()){
                    break;
                }

                Elements elements = doc.getElementsByClass("sons");
                for (Element div : elements) {
                    for (Element ele : div.children()) {
                        if (ele.tag().getName().equals("p")) {
                            if (ele.children() != null) {
                                String poetryLink = ele.child(0).attr("href");
                                if (!poetryLink.equals("")) {
                                    if (!linkQueue.offer(POETRY_URL_HEAD + poetryLink)) {
                                        log.error("--------------------------------- linkQueue Offer False !!!");//push
                                    }
                                    log.info("push link -----> " + POETRY_URL_HEAD + poetryLink);
                                    break;
                                }
                            }
                        }
                    }
                }

                if (pageCount < MAX_PAGE_COUNT) {
                    elements = doc.getElementsByClass("pages");
                    for (Element ele : elements.first().children()) {
                        if (ele.tag().getName().equals("a")) {
                            if (ele.text().equals("下一页")) {
                                if (!ele.attr("href").equals("")) {
                                    pageLink = POETRY_URL_HEAD + ele.attr("href");
                                    pageCount++;
                                    log.info("Next Page >>> " + pageLink);
                                    break;
                                } else {
                                    log.error("------------------Next Url False !!!");
                                }
                            }
                        }
                    }
                }else{
                    pageComplete.set(true);
                    log.info("set pageComplete OK");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Jsoup connect error!!!");
            }
        }
        log.info("Search finish, Quit.");
        return 0;
    }
}
