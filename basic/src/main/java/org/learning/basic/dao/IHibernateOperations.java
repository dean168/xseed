package org.learning.basic.dao;

import org.hibernate.LockMode;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface IHibernateOperations extends HibernateOperations {

    String SERVICE_ID = "basic.hibernateOperations";

    Date currentTimestamp();

    <T> T xtx(TransactionalExtractor1<T> extractor);

    <T> T xtx(TransactionalExtractor2<T> extractor);

    void xtx(TransactionalHandler1 handler);

    void xtx(TransactionalHandler2 handler);

    List<?> find(SQL sql);

    List<?> findByPagination(String sql, int offset, int limit, Object... args);

    <T> Pagination<T> findForPagination(String sql, int offset, int limit, Object... args);

    <T> T findOne(Class<T> type, String sql, Object... args);

    void delete(Class<?> clazz, String... ids);

    void xdelete(Class<?> clazz, String... ids);

    int count(String sql, Object... args);

    int xbulkUpdate(String queryString, Object... values);

    void xdelete(Object entity, LockMode lockMode);

    void xdelete(Object entity);

    void xdelete(String entityName, Object entity, LockMode lockMode);

    void xdelete(String entityName, Object entity);

    void xdeleteAll(Collection<?> entities);

    <T> T xexecute(HibernateCallback<T> action);

    <T> T xmerge(String entityName, T entity);

    <T> T xmerge(T entity);

    void xpersist(Object entity);

    void xpersist(String entityName, Object entity);

    Serializable xsave(Object entity);

    Serializable xsave(String entityName, Object entity);

    void xsaveOrUpdate(Object entity);

    void xsaveOrUpdate(String entityName, Object entity);

    void xupdate(Object entity, LockMode lockMode);

    void xupdate(Object entity);

    void xupdate(String entityName, Object entity, LockMode lockMode);

    void xupdate(String entityName, Object entity);

    interface TransactionalExtractor1<T> {

        T extract() throws Exception;
    }

    interface TransactionalExtractor2<T> {

        T extract(IHibernateOperations hibernateOperations) throws Exception;
    }

    interface TransactionalHandler1 {

        void process() throws Exception;
    }

    interface TransactionalHandler2 {

        void process(IHibernateOperations hibernateOperations) throws Exception;
    }
}
