package com.blackstone.dailyresearch.es;

import java.util.List;
import java.util.regex.Pattern;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class MyProcesser implements PageProcessor {

    private Site site = Site.me().setRetryTimes(2).setSleepTime(100);
private static final Pattern pattern = Pattern.compile("https://www.zhihu.com/question/\\d+", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    public static void main(String[] args) {
//        String link = "https://www.zhihu.com/question/60445616/answer/438282259";
//        if(pattern.matcher(link).find()) {
//            System.err.println(link);
//        }else{
//            System.out.println("不匹配");
//        }
        Spider spider = Spider.create(new MyProcesser()).addUrl("https://www.zhihu.com/")
                .addPipeline(new MyPipeLine()).thread(3);
        spider.run();
    }

    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().links().all();

        for (String link : links) {
            if(pattern.matcher(link).find()){
                page.addTargetRequests(links);
                System.out.println(link);
            }
        }

        page.putField("url", page.getUrl());
        page.putField("content", page.getHtml().$("#root > div > main").toString());
        page.putField("title", page.getHtml().xpath("/html/head/title/text()"));
        page.putField("keywords", page.getHtml().xpath("//*[@id=\"root\"]/div/main/div/meta[@itemProp=\"keywords\"]/@content").all());
    }

    @Override
    public Site getSite() {
        return site;
    }
}