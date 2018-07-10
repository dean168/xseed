package org.learning.basic.dao.dialect;

import org.hibernate.dialect.Dialect;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;

public class DialectFactory<D extends Dialect> implements FactoryBean<D> {

    private String dialect;
    private Class<D> dialectClass;
    private D dialectToUse;

    @PostConstruct
    public void init() throws ClassNotFoundException {
        Assert.hasText(dialect, "dialect must not be null");
        dialectClass = (Class<D>) ClassUtils.forName(dialect, ClassUtils.getDefaultClassLoader());
        dialectToUse = BeanUtils.instantiateClass(dialectClass);
    }

    @Override
    public D getObject() {
        return dialectToUse;
    }

    @Override
    public Class<?> getObjectType() {
        return dialectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
