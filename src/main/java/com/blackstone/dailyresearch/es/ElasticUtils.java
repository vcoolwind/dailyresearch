package com.blackstone.dailyresearch.es;

import static org.elasticsearch.action.DocWriteRequest.OpType.INDEX;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * desc:
 *
 * @author 王彦锋
 * @date 2018/7/13 16:26
 */
public class ElasticUtils {
    public static final String IK_TOKENIZER_MAX = "ik_max_word";
    public static final String IK_TOKENIZER_SMART = "ik_smart";

    private static TransportClient client;

    private static void init() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "my-es").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.85.166"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.85.167"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.85.168"), 9300));
    }

    private static TransportClient getClient() throws Exception {
        if (client == null) {
            init();
        }
        return client;
    }

    public static void writeJson(String index, String type, String id, Map<String, ?> dataMap) throws Exception {

        IndexResponse response = getClient().prepareIndex(index.toLowerCase(), type.toLowerCase())
                .setId(id)
                .setSource(dataMap, XContentType.JSON)
                .setOpType(INDEX)
                .get();
        System.out.println(response.getId());
    }

    /**
     * 对response结果的分析
     *
     * @param response
     */
    public static void showResponse(SearchResponse response) {
        // 命中的记录数
        //long totalHits = response.getHits().totalHits;
        for (SearchHit searchHit : response.getHits()) {
            System.out.println("----------------------------");
            System.out.println("id --> " + searchHit.getId());
            System.out.println("score --> " + searchHit.getScore());
            for (Map.Entry<String, Object> entry : searchHit.getSourceAsMap().entrySet()) {
                System.out.println(entry.getKey() + " --> " + entry.getValue());
            }

            System.out.println("highlight --> ");
            for (Map.Entry<String, HighlightField> entry : searchHit.getHighlightFields().entrySet()) {
                System.out.println("\t" + entry.getKey() + " --> " + entry.getValue());
            }

        }
    }

    /**
     * 查询遍历抽取
     *
     * @param queryBuilder
     */
    public static SearchResponse search(QueryBuilder queryBuilder, String highlightField, String... indeces) throws Exception {
        SearchRequestBuilder requestBuilder = getClient().prepareSearch(indeces)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .highlighter(SearchSourceBuilder.highlight().field("content"))
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(100);
        if (StringUtils.isNotBlank(highlightField)) {
            requestBuilder.highlighter(SearchSourceBuilder.highlight().field(highlightField));
        }
        SearchResponse response = requestBuilder.execute().actionGet();
        return response;
    }

    public static SearchResponse termsQuery(String index, String words, String field, String tokenizer, boolean highlight) throws Exception {
        IndicesAdminClient indicesAdminClient = getClient().admin().indices();

        AnalyzeRequestBuilder request = new AnalyzeRequestBuilder(indicesAdminClient, AnalyzeAction.INSTANCE, index, words);
        //Analyzer（分析器）
        //request.setAnalyzer("ik_max_word");
        if (StringUtils.isNotBlank(tokenizer)) {
            //Tokenizer（分词器）
            request.setTokenizer(tokenizer);
        }
        List<AnalyzeResponse.AnalyzeToken> listAnalysis = request.execute().actionGet().getTokens();
        ArrayList<String> terms = new ArrayList<String>();
        for (AnalyzeResponse.AnalyzeToken token : listAnalysis) {
            System.out.println(token.getTerm());
            terms.add(token.getTerm());
        }
        QueryBuilder queryBuilder = QueryBuilders.termsQuery(field, terms);
        String highlightField = highlight ? field : null;
        SearchResponse response = ElasticUtils.search(queryBuilder, highlightField, index);
        return response;
    }

    /**
     * 会自动应用ik的分词
     *
     * @param index
     * @param words
     * @param field
     * @param highlight
     * @return
     * @throws Exception
     */
    public static SearchResponse matchQuery(String index, String words, String field, boolean highlight) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, words);
        String highlightField = highlight ? field : null;

        SearchResponse response = ElasticUtils.search(queryBuilder, highlightField, index);
        return response;
    }


    /**
     * 创建类型并设置mapping
     *
     * @param indexName 索引名
     * @param typeName  类型名
     */
    public static void createMapping(String indexName, String typeName, AnalyzerProperty... analyzers) throws Exception {

            /*
    "content": {
        "type": "text",
                "analyzer": "ik_max_word",
                "search_analyzer": "ik_max_word"
    }
    */

//创建mapping
        PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(typeName);
        if (analyzers != null) {
            XContentBuilder source = XContentFactory.jsonBuilder()
                    .startObject().startObject("properties");
            for (AnalyzerProperty p : analyzers) {
                source.startObject(p.getFieldName())
                        .field("type", p.getType())
                        .field("analyzer", p.getAnalyzer())
                        .field("search_analyzer", p.getSearchAnalyzer())
                        .endObject();
            }
            source.endObject().endObject();
            mapping.source(source);
        }
        //创建索引
        getClient().admin().indices().prepareCreate(indexName).execute().actionGet();
        //为索引添加映射
        getClient().admin().indices().putMapping(mapping).actionGet();
        System.out.println("mapping创建成功");
    }

    public static void main(String[] args) throws Exception {

// 建立ik索引

//        createMapping("testindex","testtype"
//                ,new AnalyzerProperty("content","text",IK_TOKENIZER_MAX,IK_TOKENIZER_MAX)
//                ,new AnalyzerProperty("keywords","text",IK_TOKENIZER_MAX,IK_TOKENIZER_MAX)
//        );


//入库
//        List<PageContent> list = new ArrayList<>();
//        list.add(new PageContent(null, null, "美国 伊拉克", "美国留给伊拉克的是个烂摊子吗"));
//        list.add(new PageContent(null, null, "中韩 冲突", "中韩渔警冲突调查：韩警平均每天扣1艘中国渔船"));
//        list.add(new PageContent(null, null, "ElasticSearch Lucene", "ElasticSearch是一个基于Lucene的搜索服务器。" +
//                "它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，" +
//                "作为当前流行的企业级搜索引擎，用于云计算中，能够达到实时搜索，并且具有稳定，可靠，快速安装，使用方便等多种优点，得到大多数企业的青睐。"));
//        list.add(new PageContent(null, null, "无线网络 wifi wi-fi", "无线网络被澳洲媒体誉为澳洲有史以来最重要的科技发明，其发明人John O'Sullivan被澳洲媒体称为”Wi-Fi之父“并获得了澳洲的国家最高科学奖和全世界的众多赞誉，其中包括欧盟机构，欧洲专利局，European Patent Office（EPO）颁发的European Inventor Award 2012，即2012年欧洲发明者大奖。"));
//
//        for (PageContent content : list) {
//            writeJson("testindex", "testtype", DateHelper.getCurrentDateTime(), content.toMap());
//            Thread.sleep(1000);
//        }

        //查询
        String words = "稳定可靠";
        SearchResponse response = ElasticUtils.matchQuery("testindex", words, "content", true);
        showResponse(response);

    }
}
