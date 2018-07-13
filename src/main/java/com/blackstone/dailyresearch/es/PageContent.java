package com.blackstone.dailyresearch.es;

import com.blackstone.dailyresearch.util.DateHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class PageContent {
    private String title;
    private String keywords;
    private String content;
    private String url;

    private static String get(Map<String, Object> map,String key){
        return map.get(key)!=null?map.get(key).toString():"";
    }
    public PageContent(String url, String title, String keywords, String content) {
        this.url = url;
        this.title = title;
        this.keywords = keywords;
        this.content = content;
    }
    public PageContent(Map<String, Object> map) {
        this.url = get(map,"url");
        this.title = get(map,"title");
        this.keywords = get(map,"keywords");
        this.content = get(map,"content");
    }

    public boolean isValid(){
        return StringUtils.isNotBlank(content);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getID() {
            return url;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", title);
        map.put("keywords", keywords);
        map.put("updatedatetime",DateHelper.getCurrentDateTime());
        map.put("content", content);

        return map;
    }

    @Override
    public String toString() {
        return "PageContent{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", keywords=" + keywords +
                ", url='" + url + '\'' +
                '}';
    }
}