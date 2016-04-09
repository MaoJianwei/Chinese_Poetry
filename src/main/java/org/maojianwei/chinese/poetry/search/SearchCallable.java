package org.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mao on 4/9/16.
 */
public class SearchCallable implements Callable {

    String firstPagesuffix;
    final String POETRY_URL_HEAD;// = "http://so.gushiwen.org";
    final int MAX_PAGE_COUNT;// = 3;
    LinkedBlockingQueue linkQueue;


    public SearchCallable(LinkedBlockingQueue queue, String poetryUrlHead, String firstPagesuffix, int maxPageCount) {

        this.linkQueue = queue;
        this.POETRY_URL_HEAD = poetryUrlHead;
        this.MAX_PAGE_COUNT = maxPageCount;
        this.firstPagesuffix = firstPagesuffix;
    }


    public Integer call() {

        String pageLink = POETRY_URL_HEAD + firstPagesuffix;
        int pageCount = 1;

        while (!pageLink.equals("") && pageCount <= MAX_PAGE_COUNT) {
            try {
                Document doc = Jsoup.connect(pageLink).get();

                Elements elements = doc.getElementsByClass("sons");
                for (Element div : elements) {
                    for (Element ele : div.children()) {
                        if (ele.tag().getName().equals("p")) {
                            if (ele.children() != null) {
                                String poetryLink = ele.child(0).attr("href");
                                if (!poetryLink.equals("")) {
                                    if (!linkQueue.offer(POETRY_URL_HEAD + poetryLink)) {
                                        System.out.println("---------------------------------LinkedBlockingQueue Offer False !!!");//push
                                        System.out.println(POETRY_URL_HEAD + poetryLink);
                                    } else {
                                        System.out.println(POETRY_URL_HEAD + poetryLink);
                                    }
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
                                    System.out.println(pageLink);
                                    break;
                                } else {
                                    System.out.println("------------------Next Url False !!!");
                                }
                            }
                        }
                    }
                }else{
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
