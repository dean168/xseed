package org.learning.basic.dao.support;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.learning.basic.utils.UUIDUtils;

import java.io.Serializable;
import java.util.Properties;

import static org.learning.basic.core.Errors.Patterns.handler;

public class AssignedUUIDGenerator implements IdentifierGenerator, Configurable {

    private String name;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        name = params.getProperty(ENTITY_NAME);
        if (name == null) {
            handler("no entity name");
        }
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(name, object).getIdentifier(object, session);
        return id == null ? UUIDUtils.random() : id;
    }
}
