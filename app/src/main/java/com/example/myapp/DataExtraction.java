package com.example.myapp;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DataExtraction implements Runnable {

    NewsStore newsStore;
    String topic;
    int counter = 0;
    Document doc;
    Elements details;
    String[] url=new String[35];
    String[] img=new String[35];
    String[] text=new String[35];
    public DataExtraction()
    {

    }
    public DataExtraction(String topic) {
        this.topic = topic;
    }

    public void run()
    {
        try {

            doc = Jsoup.connect("https://indianexpress.com/?s="+this.topic).get();
            if(doc.getElementsByClass("search-result").get(0).children().get(0).text()!="Result: 0- 0 out of 0 Article found") {
                // get title of the page
                // get all links
                details = doc.getElementsByClass("details");


                for (Element detail : details) {

                    for (Element child : detail.children()) {
                            if (child.tagName() == "p") {
                            continue;
                        } else if (child.tagName() == "h3") {
                            for (Element subElement : child.children()) {
                                //System.out.println(subElement.attr("href").toString());
                                //System.out.println(subElement.attr("title").toString());
                                url[counter] = subElement.attr("href").toString();
                                text[counter] = subElement.attr("title").toString();

                            }
                        } else if (child.tagName() == "div") {

                            for (Element subElement : child.children()) {
                                for (Element subSubElement : subElement.children()) {
                                    if (subSubElement.tagName() == "img") {
                                        img[counter] = subSubElement.attr("data-lazy-src").toString();
                                    }
                                }
                            }
                        }


                    }
                    counter += 1;
                }
                this.newsStore=new NewsStore();
                this.newsStore.url=new String[counter];
                this.newsStore.img=new String[counter];
                this.newsStore.title=new String[counter];
                for(int i=0;i<counter;i++)
                {
                    this.newsStore.url[i]=url[i];
                    this.newsStore.img[i]=img[i];
                    this.newsStore.title[i]=text[i];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
