package org.learning.basic.dao.support;

import com.alibaba.druid.pool.DruidDataSource;
import org.learning.basic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;

public class DruidDataSourceImpl extends DruidDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DruidDataSourceImpl.class);

    private String envs;
    private String inits;
    private String delimiters;

    @Override
    public void init() throws SQLException {
        if (!inited) {
            if (StringUtils.isNotEmpty(envs)) {
                if (logger.isInfoEnabled()) {
                    logger.info("envs: " + envs);
                }
                String[] envsToUse = StringUtils.tokenizeToStringArray(envs, StringUtils.isNotEmpty(delimiters) ? delimiters : ",");
                for (String env : envsToUse) {
                    String key = StringUtils.substringBefore(env, "=");
                    String value = StringUtils.substringAfter(env, "=");
                    System.setProperty(key, value);
                }
            }
            if (StringUtils.isNotEmpty(inits)) {
                if (logger.isInfoEnabled()) {
                    logger.info("inits: " + inits);
                }
                String[] initsToUse = StringUtils.tokenizeToStringArray(inits, StringUtils.isNotEmpty(delimiters) ? delimiters : ",");
                setConnectionInitSqls(Arrays.asList(initsToUse));
            }
            super.init();
        }
    }

    public void setEnvs(String envs) {
        this.envs = envs;
    }

    public void setInits(String inits) {
        this.inits = inits;
    }

    public void setDelimiters(String delimiters) {
        this.delimiters = delimiters;
    }
}
