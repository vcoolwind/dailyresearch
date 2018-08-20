package com.blackstone.dailyresearch.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public final class DataSourceUtil {
    private static final Logger log = Logger.getLogger(DataSourceUtil.class);

    private static final String DS_CONFIG_FILE = "datasource.properties";

    private static DruidDataSource ds = null;

    private DataSourceUtil() {
    }

    private static void init() throws Exception {

        InputStream input_ds = Thread.currentThread().getContextClassLoader().getResourceAsStream(DS_CONFIG_FILE);
        Properties config_all = new Properties();
        config_all.load(input_ds);

        if (log.isInfoEnabled()) {
            log.info("ds url=" + config_all.getProperty("url"));
            log.info("ds username=" + config_all.getProperty("username"));
            log.info("ds password=" + config_all.getProperty("password"));
            log.info("ds initialSize=" + config_all.getProperty("initialSize"));
            log.info("ds minIdle=" + config_all.getProperty("minIdle"));
            log.info("ds minEvictableIdleTimeMillis=" + config_all.getProperty("minEvictableIdleTimeMillis"));
            log.info("ds validationQuery=" + config_all.getProperty("validationQuery"));
        }

        ds = (DruidDataSource)DruidDataSourceFactory.createDataSource(config_all);
    }

    public static synchronized DataSource getDataSource() throws Exception {
        if (ds == null) {
            init();
        }
        return ds;
    }

    public static Connection getConn() throws Exception {
        return getDataSource().getConnection();
    }

}
