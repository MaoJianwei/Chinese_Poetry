package org.maojianwei.chinese.poetry.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 */
public class SearchLinks {
    public static final String poetryUrlHead = "http://so.gushiwen.org";

    public static void main(String[] args) {
        searchLinks();
    }

    public static void searchLinks() {

        LinkedBlockingQueue linkQueue = new LinkedBlockingQueue();

        String pageLink = "http://so.gushiwen.org/type.aspx";

        while (!pageLink.equals("")) {
            try {
                Document doc = Jsoup.connect(pageLink).get();

//                Elements elements = doc.getElementsByClass("sons");
//
//                for (Element div : elements) {
//
//                    for (Element ele : div.children()) {
//                        if (!ele.tag().getName().equals("p")) {
//                            continue;
//                        } else {
//                            if (ele.children() != null) {
//                                String poetryLink = ele.child(0).attr("href");
//                                if (!poetryLink.equals("")) {
//                                    if (!linkQueue.offer(poetryUrlHead + poetryLink)) {
//                                        System.out.println("---------------------------------LinkedBlockingQueue Offer False !!!");//push
//                                    } else {
//                                        System.out.println(poetryUrlHead + poetryLink);
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }

                Elements elements = doc.getElementsByClass("pages");

                for(Element ele : elements.first().children()){
                    if(!ele.tag().getName().equals("a")){
                        continue;
                    }else if(ele.text().equals("下一页")){

                        if(!ele.attr("href").equals("")) {
                            pageLink = poetryUrlHead + ele.attr("href");
                            System.out.println(pageLink);
                        }else{
                            System.out.println("------------------Next Url False !!!");
                        }
                    }
                    int aaa = 0;
                }
                int a = 0;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int a = 0;
    }
}
