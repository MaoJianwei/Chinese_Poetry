package org.maojianwei.chinese.poetry.spider;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class PoetrySpider
{
    public static void main( String[] args )
    {
        getOnePoetry("http://so.gushiwen.org/view_71139.aspx");
        getOnePoetry("http://so.gushiwen.org/view_71137.aspx");
        getOnePoetry("http://so.gushiwen.org/view_49386.aspx");
        getOnePoetry("http://so.gushiwen.org/view_7722.aspx");
    }

    private static void getOnePoetry(String url){

        StringBuilder poetry = new StringBuilder();

        try {

            Document doc = Jsoup.connect(url).get();

            poetry.append(getOnePoetryTitle(doc));
            poetry.append("\n");

            poetry.append(getOnePoetryDynasty(doc));
            poetry.append("  ");
            poetry.append(getOnePoetryAuthor(doc));
            poetry.append("\n");

            poetry.append(getOnePoetryContent(doc));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(poetry.toString());
    }

    private static String getOnePoetryTitle(Document doc){

        Elements elements = doc.getElementsByClass("son1");

        for (Element ele : elements) {

            if(ele.children().size() == 1){
                return ele.child(0).text().trim();
            }
        }
        return "mao unknown";
    }

    private static String getOnePoetryAuthor(Document doc){

        Elements elements = doc.getElementsByTag("span");

        for (Element ele : elements) {

            if (ele.text().equals("作者：")) {

                return ele.nextElementSibling().text().trim();
            }
        }
        return "mao unknown";
    }

    private static String getOnePoetryDynasty(Document doc){

        Elements elements = doc.getElementsByTag("span");

        for (Element ele : elements) {

            if (ele.text().equals("朝代：")) {

                return ele.nextSibling().toString();
            }
        }
        return "mao unknown";
    }

    private static String getOnePoetryContent(Document doc) {

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
                    content.append(((TextNode) element).text().trim().replaceAll("　",""));
                    content.append("\n");
                }

            } else if (element instanceof Element) {

                if (((Element) element).tagName().equals("br")) {

                    continue;

                } else if (((Element) element).tagName().equals("p")) {

                    if (((Element) element).textNodes().size() != 0) {

                        for (TextNode textNode : ((Element) element).textNodes()) {

                            content.append(textNode.text().trim().replaceAll("　",""));
                            content.append("\n");
                        }
                    } else {
                        content.append(((Element) element).text().trim().replaceAll("　",""));
                        content.append("\n");
                    }
                }

            } else {
                content.append("Warning !!!\n");
            }

        }

        return content.append("--- END ---").toString();
    }
}
