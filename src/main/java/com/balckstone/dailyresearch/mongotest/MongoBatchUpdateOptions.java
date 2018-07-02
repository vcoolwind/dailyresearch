package com.balckstone.dailyresearch.mongotest;

import com.mongodb.DBObject;

/**
 * desc:批量更新装配类
 *
 * @author 王彦锋
 * @date 2018/3/16 15:29
 */
public class MongoBatchUpdateOptions {

    /**
     * 查询条件
     */
    private DBObject query;

    /**
     * 更新内容
     */
    private DBObject update;

    /**
     * 是否upsert
     */
    private boolean upsert = false;

    private boolean multi = false;

    public DBObject getQuery() {
        return query;
    }

    public void setQuery(DBObject query) {
        this.query = query;
    }

    public DBObject getUpdate() {
        return update;
    }

    public void setUpdate(DBObject update) {
        this.update = update;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public void setUpsert(boolean upsert) {
        this.upsert = upsert;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }
}
