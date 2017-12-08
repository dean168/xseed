package org.learning.basic.dao.support;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.utils.DaoUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class HibernateTemplateSupport extends HibernateTemplate implements IHibernateOperations {

	private Dialect dialect;

	public HibernateTemplateSupport() {
		super();
	}

	public HibernateTemplateSupport(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
    public Date currentTimestamp() {
		Assert.isTrue(dialect.supportsCurrentTimestampSelection(), "dialect.supportsCurrentTimestampSelection() must be true.");
		return execute((Session session) -> {
			NativeQuery<Date> query = session.createNativeQuery(dialect.getCurrentTimestampSelectString());
			return query.getResultList().get(0);
		});
	}

    @Transactional
    public void xtx(TransactionalCallback callback) {
        try {
            callback.doInTransactional();
        } catch (Exception e) {
            throw new DataAccessException(null, e) {
                private static final long serialVersionUID = 1845491745123712093L;
            };
        }
    }

    public List<?> findByPagination(final String sql, final int offset, final int limit, final Object... args) {
		return execute((Session session) -> {
			Query<?> query = session.createQuery(sql);
			for (int i = 0; args != null && i < args.length; i++) {
				query.setParameter(i, args[i]);
			}
			if (limit > 0) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}
			prepareQuery(query);
			return query.getResultList();
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Pagination<T> findForPagination(String sql, int offset, int limit, Object... args) {
		List<T> result = (List<T>) findByPagination(sql, offset, limit, args);
		Pagination<T> pagination = new Pagination<>();
		pagination.setOffset(offset);
		pagination.setLimit(limit);
		pagination.setResult(result);
		if (limit > 0) {
//			String count = "select count(*) from " + StringUtils.substringAfter(sql, "from");
//			if (StringUtils.containsIgnoreCase(count, "order by")) {
//				String countToUse = StringUtils.lowerCase(count);
//				count = StringUtils.substring(count, 0, StringUtils.indexOf(countToUse, "order by"));
//			}
//			if (StringUtils.containsIgnoreCase(count, "group by")) {
//				String countToUse = StringUtils.lowerCase(count);
//				count = StringUtils.substring(count, 0, StringUtils.indexOf(countToUse, "group by"));
//			}
			String count = DaoUtils.count(sql);
			pagination.setTotal(count(count, args));
		} else {
			pagination.setTotal(result.size());
		}
		return pagination;
	}

	@SuppressWarnings("unchecked")
	public <T> T findOne(Class<T> type, String sql, Object... args) {
		List<T> list = (List<T>) find(sql, args);
		return !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public void delete(Class<?> type, Serializable... ids) {
		SQL sql = new SQL();
		sql.append("delete from ").append(type);
		sql.append(" where id in (").addInParams((Object[]) ids).append(")");
		bulkUpdate(sql.getSQL(), sql.getParams());
	}

    @Transactional
	public void xdelete(Class<?> type, Serializable... ids) {
        delete(type, ids);
	}

	public int count(String sql, Object... args) {
		List<?> list = find(sql, args);
		return !list.isEmpty() ? Integer.valueOf(String.valueOf(list.get(0))) : 0;
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#bulkUpdate(java.lang.String, java.lang.Object[])
	 */
	@Transactional
	public int xbulkUpdate(String queryString, Object... values) throws DataAccessException {
		return super.bulkUpdate(queryString, values);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(java.lang.Object, org.hibernate.LockMode)
	 */
	@Transactional
	public void xdelete(Object entity, LockMode lockMode) throws DataAccessException {
		super.delete(entity, lockMode);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(java.lang.Object)
	 */
	@Transactional
	public void xdelete(Object entity) throws DataAccessException {
		super.delete(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(java.lang.String, java.lang.Object, org.hibernate.LockMode)
	 */
	@Transactional
	public void xdelete(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
		super.delete(entityName, entity, lockMode);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#delete(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public void xdelete(String entityName, Object entity) throws DataAccessException {
		super.delete(entityName, entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#deleteAll(java.util.Collection)
	 */
	@Transactional
	public void xdeleteAll(Collection<?> entities) throws DataAccessException {
		super.deleteAll(entities);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#execute(org.springframework.orm.hibernate5.HibernateCallback)
	 */
	@Transactional
	public <T> T xexecute(HibernateCallback<T> action) throws DataAccessException {
		return super.execute(action);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#merge(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public <T> T xmerge(String entityName, T entity) throws DataAccessException {
		return super.merge(entityName, entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#merge(java.lang.Object)
	 */
	@Transactional
	public <T> T xmerge(T entity) throws DataAccessException {
		return super.merge(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#persist(java.lang.Object)
	 */
	@Transactional
	public void xpersist(Object entity) throws DataAccessException {
		super.persist(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#persist(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public void xpersist(String entityName, Object entity) throws DataAccessException {
		super.persist(entityName, entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#save(java.lang.Object)
	 */
	@Transactional
	public Serializable xsave(Object entity) throws DataAccessException {
		return super.save(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#save(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public Serializable xsave(String entityName, Object entity) throws DataAccessException {
		return super.save(entityName, entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#saveOrUpdate(java.lang.Object)
	 */
	@Transactional
	public void xsaveOrUpdate(Object entity) throws DataAccessException {
		super.saveOrUpdate(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#saveOrUpdate(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public void xsaveOrUpdate(String entityName, Object entity) throws DataAccessException {
		super.saveOrUpdate(entityName, entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(java.lang.Object, org.hibernate.LockMode)
	 */
	@Transactional
	public void xupdate(Object entity, LockMode lockMode) throws DataAccessException {
		super.update(entity, lockMode);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(java.lang.Object)
	 */
	@Transactional
	public void xupdate(Object entity) throws DataAccessException {
		super.update(entity);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(java.lang.String, java.lang.Object, org.hibernate.LockMode)
	 */
	@Transactional
	public void xupdate(String entityName, Object entity, LockMode lockMode) throws DataAccessException {
		super.update(entityName, entity, lockMode);
	}

	/**
	 * @see org.springframework.orm.hibernate5.HibernateTemplate#update(java.lang.String, java.lang.Object)
	 */
	@Transactional
	public void xupdate(String entityName, Object entity) throws DataAccessException {
		super.update(entityName, entity);
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
}
