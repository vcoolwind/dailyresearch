/**
 * @Title MongoObject.java 
 * @Package com.zlfund.cacheservice.model 
 * @Description 
 * @author 徐文凡
 * @date 2015-1-16 下午01:25:15 
 * @version V1.0   
 */
package com.blackstone.dailyresearch.mongotest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/** 
 * 扩展DBObject
 * @author: 徐文凡 
 * @since: 2015-1-16 下午01:25:15 
 * @history:
 */
public class MongoObject extends BasicDBObject {

    /** 
     * @Fields serialVersionUID  
     */
    private static final long serialVersionUID = -5903174409034893094L;

    public MongoObject() {
        super();
    }

    public MongoObject(DBObject dbObject) {
        super(dbObject == null ? new BasicDBObject().toMap() : dbObject.toMap());
    }

    public MongoObject(Map map) {
        super((map == null ? new HashMap() : map));
    }

    public MongoObject getMongoObjectValue(String key) {
        if (containsField(key) && get(key) != null && get(key) instanceof DBObject) {
            return new MongoObject(((DBObject)get(key)).toMap());
        }
        return new MongoObject();
    }

    public Integer getIntValue(String key) {
        if (containsField(key) && get(key) != null && get(key) instanceof Integer) {
            return getInt(key, 0);
        }
        return null;
    }

    public String getStringValue(String key) {
        if (containsField(key) && get(key) != null && get(key) instanceof String) {
            return getString(key, StringUtils.EMPTY);
        }
        return null;
    }

    public Double getDoubleValue(String key) {
        if (containsField(key) && get(key) != null && get(key) instanceof Double) {
            return getDouble(key, 0.00);
        }
        return null;
    }

    public Date getDateValue(String key) {
        if (containsField(key) && get(key) != null && get(key) instanceof Date) {
            return getDate(key, new Date());
        }
        return null;
    }
}
