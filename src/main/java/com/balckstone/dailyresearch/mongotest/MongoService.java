/**
 * @Title MongoServiceImpl.java
 * @Package com.zldata.cacheservice.service.impl
 * @Description
 */
package com.balckstone.dailyresearch.mongotest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.balckstone.dailyresearch.util.DateHelper;
import com.balckstone.dailyresearch.util.ThreadPoolHelper;

public class MongoService {

    public static final String MONGODB_ID = "_id";
    public static final String OP_SET = "$set";
    public static final String OP_IN = "$in";
    private static final Logger LOG = Logger.getLogger(MongoService.class);
    /**
     * 每个线程执行的更新数量
     */
    private static final Integer UPDATE_COUNT_PER_THREAD = 50;


    /**
     * 创建新集合
     * @return
     * @create: 2013-7-29 下午04:42:45 
     * @history:
     */
    public DBCollection createNewCollection(String collName) {
        return createNewCollection(null, collName);
    }

    /**
     * 创建新集合
     * @return
     * @create:
     * @author:
     * @history:
     */
    public DBCollection createNewCollection(String dbName, String collName) {

        DB db = MongoHelper.getDB(dbName);
        DBCollection coll = db.getCollection(collName);

        // 先drop
        coll.drop();

        coll = db.getCollection(collName);

        LOG.info("MongoDB Collection Create Success,Collection:" + collName + ".");
        return coll;
    }

    /**
     * 创建集合索引
     * @param coll
     * @param fields
     * @history:
     */
    public void createIndexs(DBCollection coll, String... fields) {
        if (coll != null && fields != null) {
            for (String f : fields) {
                coll.createIndex(new BasicDBObject(f, -1));
                LOG.info("MongoDB Index Create Success,Collection:" + coll.getName() + ",Field:" + f + ".");
            }
        }
    }

    /**
     * 创建联合索引
     * @param coll
     * @param fields
     * @history:
     */
    public void createEnsureIndex(DBCollection coll, Boolean isUnique, String... fields) {
        if (coll != null && fields != null) {
            DBObject keys = new BasicDBObject();
            for (String f : fields) {
                keys.put(f, -1);
            }
            DBObject optionsIN = new BasicDBObject("unique", isUnique);
            coll.createIndex(keys, optionsIN);
        }
    }

    /**
     * 替换集合
     * @param srcColl 源集合
     * @param replace
     * @history:
     */
    public void replaceCollection(DBCollection srcColl, String replace) {
        DB db = MongoHelper.getDB();
        DBCollection targetColl = db.getCollection(replace);
        targetColl.drop();
        LOG.info("MongoDB Collection Replace,Source:" + srcColl.getName() + ",Dest:" + replace);
        srcColl.rename(replace, true);
        LOG.info("MongoDB Collection Replace Success.");
    }

    public DBCollection getCollection(String collName) {
        return MongoHelper.getDB().getCollection(collName);
    }


    /**
     * 使用Map构建一个DBObject
     * @throws Exception
     */
    public DBObject buildDBObject(Map<String, Object> data)   {
        if (data == null || data.isEmpty()) {
            LOG.warn("buildDBObject Error,Data Is Null!");
            return null;
        }

        BasicDBObjectBuilder objectBuilder = new BasicDBObjectBuilder();

        Set<Entry<String, Object>> entrySet = data.entrySet();
        Object value = null;
        for (Entry<String, Object> entry : entrySet) {
            value = entry.getValue();
            if (value instanceof BigDecimal) {
                value = ((BigDecimal) value).doubleValue();
            } else if (value instanceof Date) {
                // Date第一种显示yyyy-MM-dd HH:mm:ss
                objectBuilder.append(entry.getKey() + "_display1", DateHelper.formatDate((Date) value));
                // Date第二种显示yyyy-MM-dd
                objectBuilder.append(entry.getKey() + "_display2",
                        DateHelper.formatDate((Date) value, DateHelper.YYYY_MM_DD));
                // Date第三种显示yyyyMMdd
                objectBuilder.append(entry.getKey() + "_display3",
                        DateHelper.formatDate((Date) value, DateHelper.YYYYMMDD));
            } else if (value instanceof String) {
                value = ((String) value).trim();
            }

            objectBuilder.append(entry.getKey(), value);
        }

        return objectBuilder.get();
    }

    /**
     * 构造一个List
     */
    public BasicDBList buildDBList(List<Map<String, Object>> list) throws Exception {
        BasicDBList dbList = new BasicDBList();
        if (list != null) {
            for (Map<String, Object> map : list) {
                dbList.add(buildDBObject(map));
            }
        }
        return dbList;
    }

    /**
     * 查询并构造一个Mongo对象
     */
    public MongoObject findOne(String collectionName, DBObject q) {
        return findOne(collectionName, q, null);
    }

    /**
     * 指定字段的查询并构造一个Mongo对象(推荐)
     */
    public MongoObject findOne(String collectionName, DBObject q, DBObject f) {
        return new MongoObject(getCollection(collectionName).findOne(q, f));
    }

    /**
     * 根据条件查询
     */
    public List<MongoObject> find(String collectionName, DBObject q) {
        return find(collectionName, q, null);
    }

    /**
     * 指定字段查询Mongo对象
     * 可以减少网络数据的传输,但是牺牲了本地内存消耗(要把Cursor在本地转换成List<MongoObject>),用空间换时间是值得的
     * @param collectionName
     * @param q
     * @param f
     * @return
     * @create: 2015-1-16 下午06:21:33
     * @author: 徐文凡
     * @history:
     */
    public List<MongoObject> find(String collectionName, DBObject q, DBObject f) {
        DBCursor cursor = null;
        try {
            if (q == null) {
                // 如果查询条件为null,则默认查全部
                q = new BasicDBObject();
            }

            List<MongoObject> retList = new ArrayList<MongoObject>();
            cursor = getCollection(collectionName).find(q, f);
            while (cursor.hasNext()) {
                retList.add(new MongoObject(cursor.next()));
            }
            return retList;
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
    }


    /**
     * 判断集合是否存在
     */
    public Boolean collectionExists(String collName) {
        return collectionExists(null, collName);
    }

    /**
     * 判断指定集合是否存在
     */
    public Boolean collectionExists(String dbName, String collName) {
        return MongoHelper.getDB(dbName).collectionExists(collName);
    }

    /**
     * update方式更新MongoDB数据
     * @param collection
     * @param id
     * @param key
     * @param value 值可以为空, 如果为空, 就更新成null
     * @history:
     */
    public void update(DBCollection collection, Object id, String key, Object value) {
        // 参数校验, 除了value其余都不允许为null, 否则不更新
        if (collection == null || id == null || StringUtils.isBlank(key)) {
            return;
        }

        DBObject q = new BasicDBObject(MONGODB_ID, id);
        DBObject u = new BasicDBObject(key, value);
        DBObject o = new BasicDBObject(OP_SET, u);

        try {
            collection.update(q, o, true, false);

            if (LOG.isDebugEnabled()) {
                LOG.debug("MongoDB Update Data Succ, q:" + q + ", u:" + u);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    public void update(DBCollection collection, DBObject q, DBObject u) {
        update(collection, q, u, true);
    }

    public void update(DBCollection collection, DBObject q, DBObject u, Boolean setFlag) {
        if (collection == null || q == null || u == null) {
            return;
        }

        DBObject o = null;
        if (setFlag) {
            o = new BasicDBObject(OP_SET, u);
        } else {
            o = u;
        }

        try {
            collection.update(q, o, true, false);

            if (LOG.isDebugEnabled()) {
                LOG.debug("MongoDB Update Data Succ, q:" + q + ", u:" + u);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    public void save(DBCollection collection, DBObject s, Integer allowFailCount) {
        if (collection == null || s == null) {
            if (null == collection) {
                LOG.error("Save data to Mongo ERROR:Collection is null");
            }
            if (null == s) {
                LOG.error("Save data to Mongo ERROR:DBObject is null");
            }
            return;
        }

        try {
            collection.save(s);

            if (LOG.isDebugEnabled()) {
                LOG.debug("MongoDB Save Data Succ, s:" + s);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        // s = null;
    }

    public void save(DBCollection collection, DBObject s) {
        save(collection, s, 20);
    }

    private DBObject getQ(DBObject s) {
        if (s.containsField(MONGODB_ID)) {
            return new BasicDBObject(MONGODB_ID, s.get(MONGODB_ID));
        } else {
            return new BasicDBObject(s.toMap());
        }
    }

    public void remove(DBCollection collection, DBObject q) {
        if (collection == null || q == null) {
            return;
        }

        try {
            collection.remove(q);

            if (LOG.isDebugEnabled()) {
                LOG.debug("MongoDB Delete Data Succ, q:" + q);
            }
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    public Object eval(DB db, String script) {
        return db.eval(script);
    }

    public MongoBatchUpdateOptions setMongoBatchUpdateOptions(DBObject dbObj) {
        return setMongoBatchUpdateOptions((String) dbObj.get("_id"), null, dbObj);
    }

    public MongoBatchUpdateOptions setMongoBatchUpdateOptions(Object id, String key, DBObject value) {
        MongoBatchUpdateOptions upd = new MongoBatchUpdateOptions();
        upd.setQuery(new BasicDBObject(MONGODB_ID, id));
        DBObject o = null;
        if (StringUtils.isBlank(key)) {
            // 全量更新
            o = value;
        } else {
            // 部分更新
            DBObject u = new BasicDBObject(key, value);
            o = new BasicDBObject(OP_SET, u);
        }
        upd.setUpdate(o);
        upd.setUpsert(true);
        upd.setMulti(false);
        return upd;
    }

    public void executeBatchUpdate(DBCollection coll, List<MongoBatchUpdateOptions> batchUpdates, String opt, boolean isLastExecute) {
        executeBatchUpdate(coll, batchUpdates, opt, true, isLastExecute);
    }

    public void executeBatchUpdate(DBCollection coll, List<MongoBatchUpdateOptions> batchUpdates, String opt, boolean ordered, boolean isLastExecute) {
        if (batchUpdates.size() > 200 || (isLastExecute && batchUpdates.size() > 0)) {
            int updNum = batchUpdate(coll, coll.getName(), batchUpdates, ordered);
            // 基金估值更新时，日志太多，不打印，设置为debug级别
            LOG.info(opt + " bathUpdate numbers:" + updNum);
            batchUpdates.clear();
        }
    }

    /**
     * 批量更新MongoDB数据，可以upsert
     * @param dbCollection
     * @param collName 要更新的Collection名称
     * @param options 更新列表
     * @param ordered 是否要排序
     * @return 更新的条数
     *
     * @author 王彦锋
     * @date 2018/3/16 16:09
     *
     */
    public int batchUpdate(DBCollection dbCollection, String collName, List<MongoBatchUpdateOptions> options, boolean ordered) {
        DBObject command = new BasicDBObject();
        command.put("update", collName);
        List<BasicDBObject> updateList = new ArrayList<BasicDBObject>();
        for (MongoBatchUpdateOptions option : options) {
            BasicDBObject update = new BasicDBObject();
            update.put("q", option.getQuery());
            update.put("u", option.getUpdate());
            update.put("upsert", option.isUpsert());
            update.put("multi", option.isMulti());
            updateList.add(update);
        }
        command.put("updates", updateList);
        command.put("ordered", ordered);
        CommandResult commandResult = dbCollection.getDB().command(command);
        return Integer.parseInt(commandResult.get("n").toString());
    }

    /* 批量保存到MongoDB */

    /**
     * 批量更新MongoDB
     * @throws Exception
     */
    public void batchSaveToMongo(DBCollection coll, Queue<DBObject> qDBObj) throws Exception {
        ExecutorService executor = null;
        CompletionService<String> completionService = null;

        try {
            if (qDBObj.isEmpty()) {
                return;
            }

            int size = qDBObj.size();

            // logger.info("批量更新MongoDB开始...");
            // 动态计算线程数量
            Integer threadNumber = (qDBObj.size() / UPDATE_COUNT_PER_THREAD) + 1;
            Integer multiple = qDBObj.size() / UPDATE_COUNT_PER_THREAD;
            if (threadNumber > 1 && qDBObj.size() == multiple * UPDATE_COUNT_PER_THREAD) {
                threadNumber--;
            }
            // 限制线程数不得超过100
            if (threadNumber > 50) {
                threadNumber = 50;
            }
            // logger.info("批量更新MongoDB线程数:" + String.valueOf(threadNumber));
            // executor = Executors.newFixedThreadPool(threadNumber);
            executor = ThreadPoolHelper.createThreadPool("MongoServiceImpl-batchSaveToMongo", threadNumber, 50, 10, TimeUnit.MILLISECONDS, 1000);
            completionService = new ExecutorCompletionService<String>(executor);
            Queue<DBObject> qDBObjUpdate = null;
            Boolean startThread = false;
            while (!qDBObj.isEmpty()) {
                if (qDBObjUpdate == null || startThread) {
                    qDBObjUpdate = new ConcurrentLinkedQueue<DBObject>();
                    startThread = false;
                }
                qDBObjUpdate.offer(qDBObj.poll());
                if ((qDBObjUpdate.size() % UPDATE_COUNT_PER_THREAD) == 0 || qDBObj.isEmpty()) {
                    // 满了就放入线程中执行
                    completionService.submit(new UpdateMongoTask(coll, qDBObjUpdate));
                    startThread = true;
                }
            }

            for (int i = 0; i < threadNumber; i++) {
                completionService.take().get();
            }

            LOG.info("批量更新MongoDB完成,coll=" + coll.getName() + " cnt=" + String.valueOf(size));

        } catch (Exception e) {
            throw e;
        } finally {
            if (null != completionService) {
                completionService = null;
            }
            if (null != executor) {
                executor.shutdown();
                executor = null;
            }
        }
    }

    /**
     * 批量更新Mongo任务
     * @author liutao
     */
    class UpdateMongoTask implements Callable<String> {

        private Queue<DBObject> qDBObj;

        private DBCollection coll;

        private Integer updateSize = 0;

        public UpdateMongoTask(DBCollection coll, Queue<DBObject> dbos) {
            this.coll = coll;
            this.qDBObj = dbos;
            this.updateSize = qDBObj.size();
        }

        @Override
        public String call() throws Exception {
            Long threadId = Thread.currentThread().getId();
            if (qDBObj != null && !qDBObj.isEmpty()) {

                try {
                    while (!qDBObj.isEmpty()) {
                        DBObject dbObj = qDBObj.poll();
                        if (null != dbObj) {
                            try {
                                save(coll, dbObj);
                                // coll.update(new BasicDBObject("_id", (String)dbObj.get("_id")), dbObj, true, false,
                                // WriteConcern.SAFE);
                            } catch (Exception e) {
                                update(coll,
                                        new BasicDBObject(MONGODB_ID, (String) dbObj.get(MONGODB_ID)), dbObj);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    this.qDBObj = null;
                }
            } else {
                LOG.info("需要更新到MongoDB的数据为空[" + threadId + "]");
            }

            return "coll=" + coll.getName() + " cnt=" + String.valueOf(updateSize);
        }
    }

}
