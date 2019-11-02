package org.learning.basic.shiro.service.support;

import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.SessionContext;
import org.learning.basic.core.domain.Basic;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ClassUtils;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.text.MessageFormat.format;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

public class ShiroBasicServiceImpl {

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    protected IHibernateOperations hibernateOperations;

    <B extends Basic> B create(B basic, Function<B, B> function) {
        if (StringUtils.isNotEmpty(basic.getId())) {
            SQL sql = new SQL();
            sql.append("select count(1) from ").append(ClassUtils.getUserClass(basic));
            sql.append(" where id = ?", basic.getId());
            isTrue(hibernateOperations.count(sql.getSQL(), sql.getParams()) == 0, format("invalid id: {0}", basic.getId()));
        }
        SessionContext context = SessionContext.get();
        if (context != null) {
            basic.setCreatedBy(context.accountId());
            basic.setUpdatedBy(context.accountId());
        }
        Date now = new Date();
        basic.setCreatedAt(now);
        basic.setUpdatedAt(now);
        basic = hibernateOperations.merge(basic);
        basic = function.apply(basic);
        return hibernateOperations.merge(basic);
    }

    <B extends Basic> B update(B basic, Function<B, B> function) {
        hasText(basic.getId(), "id must not be null");
        SQL sql = new SQL();
        sql.append("select count(1) from ").append(ClassUtils.getUserClass(basic));
        sql.append(" where id = ?", basic.getId());
        isTrue(hibernateOperations.count(sql.getSQL(), sql.getParams()) == 1, format("invalid id: {0}", basic.getId()));
        B basicToUse = function.apply(basic);
        SessionContext context = SessionContext.get();
        if (context != null) {
            basicToUse.setUpdatedBy(context.accountId());
        }
        basicToUse.setUpdatedAt(new Date());
        return hibernateOperations.merge(basicToUse);
    }
}
