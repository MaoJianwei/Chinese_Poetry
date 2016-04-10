package org.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/9/16.
 */
public class SearchCallable implements Callable {

    final String FIRST_PAGE_SUFFIX;
    final String POETRY_URL_HEAD;// = "http://so.gushiwen.org";
    final int MAX_PAGE_COUNT;// = 3;

    LinkedBlockingQueue<String> linkQueue;
    AtomicBoolean needShutdown = new AtomicBoolean();
    AtomicBoolean pageComplete = new AtomicBoolean();


    public SearchCallable(
            LinkedBlockingQueue queue,
            String poetryUrlHead,
            String firstPagesuffix,
            int maxPageCount,
            AtomicBoolean pageComplete,
            AtomicBoolean needShutdown) {

        this.linkQueue = queue;
        this.POETRY_URL_HEAD = poetryUrlHead;
        this.MAX_PAGE_COUNT = maxPageCount;
        this.FIRST_PAGE_SUFFIX = firstPagesuffix;
        this.pageComplete = pageComplete;
        this.needShutdown = needShutdown;
    }


    public Integer call() {

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
                                        System.out.println("--------------------------------- linkQueue Offer False !!!");//push
                                    }
                                    System.out.println("Search: push link ------> " + POETRY_URL_HEAD + poetryLink);
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
                                    System.out.println("Search: Next Page >>> " + pageLink);
                                    break;
                                } else {
                                    System.out.println("------------------Next Url False !!!");
                                }
                            }
                        }
                    }
                }else{
                    pageComplete.set(true);
                    System.out.println("Search: pageComplete!");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Search: Jsoup connect error!!!");
            }
        }
        System.out.println("Search: Quit");
        return 0;
    }
}
