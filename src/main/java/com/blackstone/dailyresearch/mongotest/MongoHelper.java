package com.blackstone.dailyresearch.mongotest;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MongoHelper {

    private static final Logger logger = Logger.getLogger(MongoHelper.class);
    private static final String DEFAULT_DB_NAME = "fund";
    private static Mongo mongo = null;

    private MongoHelper() {
    }

    public static DB getDB() {
        return getDB(null);
    }

    public static DB getDB(String dbName) {
        if (null == mongo) {
            init(100, dbName);
        }

        String dbString = StringUtils.isEmpty(dbName) ? DEFAULT_DB_NAME : dbName;
        DB db = mongo.getDB(dbString);


        db.setReadPreference(ReadPreference.secondary());
        db.slaveOk();

        return db;
    }


    /**
     * 初始化连接池,设置参数.
     *
     * @param poolSize
     * @param dbName
     * @create: 2013-4-25 下午02:02:03
     * @author: 徐文凡
     * @history:
     */
    private static void init(int poolSize, String dbName) {
        System.setProperty("MONGO.POOLSIZE", String.valueOf(poolSize));
        try {
            // 改变默认设置
            MongoClientOptions.Builder optBuilder = MongoClientOptions.builder();
            // 自动重连,默认15秒
            //optBuilder.autoConnectRetry(true);
            // 连接池最大连接数
            optBuilder.connectionsPerHost(poolSize);
            // 最大等待线程数
            optBuilder.threadsAllowedToBlockForConnectionMultiplier(30);
            // 设置等待连接池超时
            optBuilder.maxWaitTime(30000);
            // 设置连接超时
            optBuilder.connectTimeout(5000);
            // 设置读超时
            optBuilder.socketTimeout(2 * 1000 * 60);
            // 解决can't say something问题
            optBuilder.socketKeepAlive(true);
            //optBuilder.readPreference(ReadPreference.secondaryPreferred());
            //optBuilder.readPreference(ReadPreference.secondary());
            optBuilder.writeConcern(WriteConcern.SAFE);
            MongoClientOptions mongoOptions = optBuilder.build();

            //认证
            String user = "zlfund";
            String password = "zlfund";
            String db = StringUtils.isEmpty(dbName) ? DEFAULT_DB_NAME : dbName;

            MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());

            List<ServerAddress> seeds = new LinkedList<ServerAddress>();
            seeds.add(new ServerAddress("172.18.10.72", 27017));

            // seeds.add(new ServerAddress("192.168.85.175", 27017));

            //seeds.add(new ServerAddress("192.168.85.172", 27017));
            //seeds.add(new ServerAddress("192.168.85.173", 27017));
            //mongo = new MongoClient(seeds, Arrays.asList(credential), mongoOptions);
            //mongo = new MongoClient(seeds,  mongoOptions);

            mongo = new MongoClient(new ServerAddress("172.18.10.72", 27017),  mongoOptions);
            //mongo.setReadPreference(ReadPreference.primary());
            // 使用安全模式写入
            //mongo.setWriteConcern(WriteConcern.SAFE);

        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
