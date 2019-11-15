package org.learning.basic.core.support;

import org.learning.basic.utils.ServiceUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.learning.basic.core.Asserts.Patterns.notEmpty;

public class ExtensionsFactoryBean<T extends Ordered> implements FactoryBean<T> {

    private Class<T> interfaces;
    private T extensions;

    @PostConstruct
    public void init() {
        Map<String, T> beans = ServiceUtils.list(interfaces);
        notEmpty(beans, "beans not found by {0}", interfaces);
        beans.values().stream().min((o1, o2) -> Math.min(o1.getOrder(), o2.getOrder())).ifPresent(bean -> extensions = bean);
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
