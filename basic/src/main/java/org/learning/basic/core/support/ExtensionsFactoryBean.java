package org.learning.basic.core.support;

import org.learning.basic.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Map.Entry;

import static org.learning.basic.core.Asserts.Patterns.notEmpty;

public class ExtensionsFactoryBean<T extends Ordered> implements FactoryBean<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionsFactoryBean.class);

    private Class<T> interfaces;
    private T extensions;

    @PostConstruct
    public void init() {
        Map<String, T> beans = ServiceUtils.list(interfaces);
        notEmpty(beans, "beans not found by {0}", interfaces);
        String using = null;
        for (Entry<String, T> entry : beans.entrySet()) {
            if (extensions == null || entry.getValue().getOrder() < extensions.getOrder()) {
                using = entry.getKey();
                extensions = entry.getValue();
            }
        }
        logger.info("{} implements using {}", interfaces.getName(), using);
    }

    @Override
    public T getObject() {
        return extensions;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaces;
    }

    public void setInterfaces(Class<T> interfaces) {
        this.interfaces = interfaces;
    }
}
