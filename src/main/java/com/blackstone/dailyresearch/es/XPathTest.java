package com.blackstone.dailyresearch.es;

import us.codecraft.webmagic.selector.Html;

public class XPathTest {
    public static void main(String[] args) {
        String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<bookstore>\n" +
                "  <storename>中华书店</storename>\n" +
                "  <book>\n" +
                "    <title lang=\"eng\">Harry Potter</title>\n" +
                "    <price>29.99</price>\n" +
                "  </book>\n" +
                "  <book>\n" +
                "    <title lang=\"eng\">Learning XML</title>\n" +
                "    <price>39.95</price>\n" +
                "  </book>\n" +
                "</bookstore>";
        //System.out.println(content);
        Html html = new Html(content);

        //选取 bookstore 元素的所有子节点。
        //System.out.println("bookstore -->\n" + html.xpath("bookstore"));
        //选取 bookstore 元素的所有子节点。
        //System.out.println("lang -->\n" + html.xpath("//@lang"));
        //选取所有 book 子元素，而不管它们在文档中的位置。
        //System.out.println("all book -->\n" + html.xpath("//book").all());
        //System.out.println("storename -->\n" + html.xpath("bookstore/storename/text()"));
        System.out.println("\n--title -->\n" + html.xpath("bookstore/book/title/text()").all());
        System.out.println("\n--title-lang -->\n" + html.xpath("bookstore/book/title/@lang").all());


    }
}
