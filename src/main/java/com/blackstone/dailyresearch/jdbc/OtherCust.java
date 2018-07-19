package com.blackstone.dailyresearch.jdbc;

public class OtherCust {
    private String mobile;
    private String tag1;
    private String source_from;
    private String isFirst;

    public OtherCust(String mobile, String tag1, String source_from) {
        this.mobile = mobile;
        this.tag1 = tag1;
        this.source_from = source_from;
    }

    public String getMobile() {
        return mobile;
    }

    public String getTag1() {
        return tag1;
    }

    public String getSource_from() {
        return source_from;
    }

    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public String toString() {
        return "OtherCust{" +
                "mobile='" + mobile + '\'' +
                ", tag1='" + tag1 + '\'' +
                ", source_from='" + source_from + '\'' +
                ", isFirst='" + isFirst + '\'' +
                '}';
    }
}
