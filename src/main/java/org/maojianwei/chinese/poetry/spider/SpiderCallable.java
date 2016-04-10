package org.maojianwei.chinese.poetry.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.maojianwei.chinese.poetry.database.PoetryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/9/16.
 */
public class SpiderCallable implements Callable {

    private LinkedBlockingQueue<String> linkQueue;
    private LinkedBlockingQueue<PoetryItem> poetryQueue;
    private AtomicBoolean pageComplete;
    private AtomicBoolean needShutdown;

    private final int QUEUE_POLL_TIMEOUT;

    private Logger log = LoggerFactory.getLogger(getClass());

    public SpiderCallable(LinkedBlockingQueue linkQueue, LinkedBlockingQueue poetryQueue, int queuePollTimeout, AtomicBoolean pageComplete, AtomicBoolean needShutdown) {
        this.linkQueue = linkQueue;
        this.poetryQueue = poetryQueue;
        this.needShutdown = needShutdown;
        this.pageComplete = pageComplete;
        this.QUEUE_POLL_TIMEOUT = queuePollTimeout;
    }


    public Integer call() {

        Thread.currentThread().setName("Mao_Spider");

        int count = 0;

        while (!needShutdown.get()) {

            String poetryUrl = null;
            try {
                poetryUrl = linkQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("--------------------- linkQueue poll error!!!");
            }

            if (poetryUrl == null) {
                if (pageComplete.get()) {
                    log.info("pageComplete is set");
                    break;
                } else {
                    log.info("queue empty, wait...");
                    continue;
                }
            }

            if (needShutdown.get()) {
                log.info("shutdown is set");
                break;
            }

            PoetryItem poetryItem = getOnePoetry(poetryUrl);
            if(poetryItem != null) {
                if (!poetryQueue.offer(poetryItem)) {
                    log.error("--------------------------------- poetryQueue Offer False !!!");//push
                }
                log.info("push poetry ------> {}", poetryItem.getTitle());

                log.info("poetry count: {}", ++count);
            }
        }
        needShutdown.set(true);
        log.info("set needShutdown, Quit");
        return 0;
    }

    private PoetryItem getOnePoetry(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("------------- Jsoup connect error!!!");
            return null;
        }


        String title = getOnePoetryTitle(doc);
        if (title.equals("mao unknown")) {
            log.warn("------------- parse Title fail!");
            return null;
        }
        String dynasty = getOnePoetryDynasty(doc);
        if (dynasty.equals("mao unknown")) {
            log.warn("------------- parse Dynasty fail!");
            return null;
        }
        String poet = getOnePoetryPoet(doc);
        if (poet.equals("mao unknown")) {
            log.warn("------------- parse Poet fail!");
            return null;
        }
        String poem = getOnePoetryContent(doc);

        PoetryItem poetryItem = new PoetryItem();
        poetryItem.setTitle(title);
        poetryItem.setDynasty(dynasty);
        poetryItem.setPoet(poet);
        poetryItem.setPoem(poem);


        StringBuilder poetry = new StringBuilder();
        poetry.append(title);
        poetry.append("\n");

        poetry.append(dynasty);
        poetry.append("  ");
        poetry.append(poet);
        poetry.append("\n");

        poetry.append(poem);

        log.info("Get one poetry:\n {}", poetry.append("--- END ---").toString());//TODO - check


        return poetryItem;
    }

    private String getOnePoetryTitle(Document doc) {

        Elements elements = doc.getElementsByClass("son1");

        for (Element ele : elements) {

            if (ele.children().size() == 1) {
                return ele.child(0).text().trim();
            }
        }
        return "mao unknown";
    }

    private String getOnePoetryPoet(Document doc) {

        Elements elements = doc.getElementsByTag("span");

        for (Element ele : elements) {

            if (ele.text().equals("作者：")) {

                if (ele.nextElementSibling() != null) {
                    return ele.nextElementSibling().text().trim();
                } else {
                    return ele.nextSibling().toString();
                }
            }
        }
        return "mao unknown";
    }

    private String getOnePoetryDynasty(Document doc) {

        Elements elements = doc.getElementsByTag("span");

        for (Element ele : elements) {

            if (ele.text().equals("朝代：")) {

                return ele.nextSibling().toString();
            }
        }
        return "mao unknown";
    }

    private String getOnePoetryContent(Document doc) {

        Elements elements = doc.getElementsByTag("span");

        Element ele = null;

        for (Element element : elements) {

            if (element.text().equals("原文：")) {

                ele = element.parent();
                break;
            }
        }

        StringBuilder content = new StringBuilder();

        for (Node element = ele.nextSibling(); element != null; element = element.nextSibling()) {

            if (element instanceof TextNode) {

                if (!((TextNode) element).text().trim().isEmpty()) {
                    content.append(((TextNode) element).text().trim().replaceAll("　", ""));
                    content.append("\n");
                }

            } else if (element instanceof Element) {

                if (((Element) element).tagName().equals("br")) {

                    continue;

                } else if (((Element) element).tagName().equals("p")) {

                    if (((Element) element).textNodes().size() != 0) {

                        for (TextNode textNode : ((Element) element).textNodes()) {

                            content.append(textNode.text().trim().replaceAll("　", ""));
                            content.append("\n");
                        }
                    } else {
                        content.append(((Element) element).text().trim().replaceAll("　", ""));
                        content.append("\n");
                    }
                }

            } else {
                content.append("Warning !!!\n");
                log.error("getOnePoetryContent parse warning!!!");
            }

        }

        return content.toString();
    }
}
