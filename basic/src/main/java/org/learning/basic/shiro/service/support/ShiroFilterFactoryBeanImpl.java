package org.learning.basic.shiro.service.support;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.shiro.domain.RolePermission;
import org.learning.basic.shiro.service.IShiroRealmListener;
import org.learning.basic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ShiroFilterFactoryBeanImpl extends ShiroFilterFactoryBean implements IShiroRealmListener {

    private static final Logger logger = LoggerFactory.getLogger(ShiroFilterFactoryBeanImpl.class);

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    private IHibernateOperations hibernateOperations;

    private Map<String, String> defaults;

    @Override
    public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
        super.setFilterChainDefinitionMap(reloadChainDefinitions(defaults = filterChainDefinitionMap));
    }

    @Override
    public void changed(Class<?> type, String id) throws Exception {
        Map<String, String> definitions = reloadChainDefinitions(defaults);
        super.setFilterChainDefinitionMap(definitions);
        AbstractShiroFilter filter = (AbstractShiroFilter) getObject();
        PathMatchingFilterChainResolver resolver = (PathMatchingFilterChainResolver) filter.getFilterChainResolver();
        DefaultFilterChainManager manager = (DefaultFilterChainManager) resolver.getFilterChainManager();
        manager.getFilterChains().clear();
        definitions.entrySet().stream().forEach(entry -> manager.createChain(entry.getKey(), entry.getValue()));
    }

    Map<String, String> reloadChainDefinitions(Map<String, String> definitions) {
        // key: url, value: roles
        Map<String, Set<String>> definitions1 = new LinkedHashMap<>();
        // 从数据库中查询出来
        SQL sql = new SQL().append("select rp.permission.urls, rp.role.id from ").append(RolePermission.class).append(" rp");
        List<Object[]> rows = (List<Object[]>) hibernateOperations.find(sql);
        for (Object[] row : rows) {
            String key = String.valueOf(row[0]);
            String[] urls = StringUtils.tokenizeToStringArray(key, ",");
            for (String url : urls) {
                Set<String> roles = definitions1.get(url);
                if (roles == null) {
                    definitions1.put(url, roles = new LinkedHashSet<>());
                }
                roles.add(String.valueOf(row[1]));
            }
        }
        // 转换格式
        Map<String, String> definitions2 = new LinkedHashMap<>();
        for (Entry<String, Set<String>> entry : definitions1.entrySet()) {
            definitions2.put(entry.getKey(), "roles[" + entry.getValue().stream().collect(joining(", ")) + "]");
        }
        // 添加配置文件的
        for (Entry<String, String> entry : definitions.entrySet()) {
            String roles = definitions2.get(entry.getKey());
            roles = roles != null ? roles + ", " : EMPTY;
            definitions2.put(entry.getKey(), roles + entry.getValue());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("definitions:\n" + definitions2.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue() + "\n").collect(joining()));
        }
        return definitions2;
    }
}
