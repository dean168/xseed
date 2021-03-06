package org.learning.basic.core.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.IDictService;
import org.learning.basic.core.domain.Dict;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.utils.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.List;

import static org.learning.basic.core.Errors.Patterns.handler;

public class DictServiceImpl implements IDictService {

    private IHibernateOperations hibernateOperations;

    @SuppressWarnings("unchecked")
    @Override
    public <D extends Dict> D get(Dict dict) {
        return prepared(dict, (D) hibernateOperations.get(forName(dict.getType()), dict.getId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <D extends Dict> D name(Class<D> type, String name) {
        name = StringUtils.trim(name);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        D dict = BeanUtils.instantiateClass(type);
        SQL sql = new SQL();
        sql.append("from ").append(type);
        sql.append(" where name = ?", name);
        sql.append(" or alias like ?", SQL.LIKE + dict.delimiters() + name + dict.delimiters() + SQL.LIKE);
        List<D> list = (List<D>) hibernateOperations.findByPagination(sql.getSQL(), 0, 1, sql.getParams());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            dict.setType(type.getName());
            dict.setName(name);
            dict.setDesc(name);
            return hibernateOperations.xmerge(dict);
        }
    }

    public <D extends Dict> Pagination<D> search(Dict dict, int offset, int limit) {
        SQL sql = new SQL();
        sql.append("from ").append(dict.getType()).append(" where 1 = 1");
        if ("null".equals(dict.getParent())) {
            sql.append(" and parent is null");
        } else {
            sql.appendIfExist(" and parent = ?", dict.getParent());
        }
        sql.appendIfExist(" and id = ?", dict.getId());
        if (StringUtils.isNotEmpty(dict.getName())) {
            sql.append(" and ( name like ?", SQL.LIKE + dict.getName() + SQL.LIKE);
            String alias = SQL.LIKE + dict.delimiters() + SQL.LIKE + dict.getName() + SQL.LIKE + dict.delimiters() + SQL.LIKE;
            sql.append(" or alias like ? )", alias);
        }
        sql.appendIfExistForLike(" and desc like ?", dict.getDesc());
        sql.append(" order by order");
        Pagination<D> pagination = hibernateOperations.findForPagination(sql.getSQL(), offset, limit, sql.getParams());
        prepared(dict, pagination.getResult());
        return pagination;
    }

    public <D extends Dict> Pagination<D> list(Dict dict, int offset, int limit) {
        SQL sql = new SQL();
        sql.append("from ").append(dict.getType()).append(" where 1 = 1");
        if ("null".equals(dict.getParent())) {
            sql.append(" and parent is null");
        } else {
            sql.appendIfExist(" and parent = ?", dict.getParent());
        }
        sql.appendIfExist(" and id = ?", dict.getId());
        if (StringUtils.isNotEmpty(dict.getName())) {
            sql.append(" and ( name = ?", dict.getName());
            String alias = SQL.LIKE + dict.delimiters() + dict.getName() + dict.delimiters() + SQL.LIKE;
            sql.append(" or alias like ? )", alias);
        }
        sql.appendIfExist(" and desc = ?", dict.getDesc());
        sql.append(" order by order");
        Pagination<D> pagination = hibernateOperations.findForPagination(sql.getSQL(), offset, limit, sql.getParams());
        prepared(dict, pagination.getResult());
        return pagination;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <D extends Dict> List<D> list(Class<D> clazz, String... ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        SQL sql = new SQL();
        sql.append("from ").append(clazz).append(" where id in (").addInParams(ids).append(")");
        return (List<D>) hibernateOperations.find(sql);
    }

    private <D extends Dict> List<D> prepared(Dict dict, List<D> list) {
        if (dict.getCcount() != null && dict.getCcount() != 0) {
            list.forEach(d -> {
                SQL sql = new SQL();
                sql.append("select count(*) from ").append(dict.getType());
                sql.append(" where parent = ?");
                d.setCcount(hibernateOperations.count(sql.getSQL(), d.getId()));
            });
        }
        if ("true".equals(dict.getXpath())) {
            list.forEach(d -> d.setXpath(xpath(d)));
        }
        if ("true".equals(dict.getUname())) {
            list.forEach(d -> d.setUname(uname(d)));
        }
        return list;
    }

    @Override
    public <D extends Dict> D prepared(Dict dict, D target) {
        if (target != null) {
            if (dict.getCcount() != null && dict.getCcount() != 0) {
                SQL sql = new SQL();
                sql.append("select count(*) from ").append(dict.getType());
                sql.append(" where parent = ?");
                target.setCcount(hibernateOperations.count(sql.getSQL(), target.getId()));
            }
            if (StringUtils.isNotEmpty(dict.getXpath())) {
                target.setXpath(xpath(target));
            }
            if (StringUtils.isNotEmpty(dict.getUname())) {
                target.setUname(uname(target));
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    private <D extends Dict> String xpath(D dict) {
        return StringUtils.isNotEmpty(dict.getParent()) ? xpath((D) hibernateOperations.load(ClassUtils.getUserClass(dict), dict.getParent())) + "/" + dict.getId() : "/" + dict.getId();
    }

    @SuppressWarnings("unchecked")
    private <D extends Dict> String uname(D dict) {
        return StringUtils.isNotEmpty(dict.getParent()) ? uname((D) hibernateOperations.load(ClassUtils.getUserClass(dict), dict.getParent())) + "/" + dict.getName() : "/" + dict.getName();
    }

    @Override
    @Transactional
    public <D extends Dict> D store(D dict) {
        return hibernateOperations.merge(dict);
    }

    @SuppressWarnings("unchecked")
    private <D extends Dict> Class<D> forName(String type) {
        try {
            return (Class<D>) ClassUtils.forName(type, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            return handler(null, e);
        }
    }

    @Transactional
    public void delete(Class<?> clazz, String... ids) {
        hibernateOperations.delete(clazz, ids);
    }

    public void setHibernateOperations(IHibernateOperations hibernateOperations) {
        this.hibernateOperations = hibernateOperations;
    }

}
