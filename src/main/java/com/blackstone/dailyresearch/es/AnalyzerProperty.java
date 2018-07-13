package com.blackstone.dailyresearch.es;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/7/13 18:15
 */
public class AnalyzerProperty {
    /*
    "content": {
        "type": "text",
                "analyzer": "ik_max_word",
                "search_analyzer": "ik_max_word"
    }
    */
    private String fieldName;
    private String type;
    private String analyzer;
    private String searchAnalyzer;

    public  AnalyzerProperty(){

    }

    public AnalyzerProperty(String fieldName, String type, String analyzer, String searchAnalyzer) {
        this.fieldName = fieldName;
        this.type = type;
        this.analyzer = analyzer;
        this.searchAnalyzer = searchAnalyzer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    @Override
    public String toString() {
        return "AnalyzerProperty{" +
                "fieldName='" + fieldName + '\'' +
                ", type='" + type + '\'' +
                ", analyzer='" + analyzer + '\'' +
                ", searchAnalyzer='" + searchAnalyzer + '\'' +
                '}';
    }
}
