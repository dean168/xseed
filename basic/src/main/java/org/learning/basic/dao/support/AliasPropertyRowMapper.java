package org.learning.basic.dao.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.learning.basic.core.Asserts.Patterns.isTrue;

public class AliasPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {

    private String[] names;
    private String[] alias;
    private PropertyDescriptor[] pds;
    private Map<String, Integer> labels;

    public AliasPropertyRowMapper(String[] names, String[] alias) {
        isTrue(ArrayUtils.getLength(names) == ArrayUtils.getLength(alias), "names.length must equals alias.length");
        this.names = names;
        this.alias = alias;
    }

    @Override
    protected void initialize(Class<T> mappedClass) {
        super.initialize(mappedClass);
        pds = new PropertyDescriptor[names.length];
        for (int i = 0; i < names.length; i++) {
            pds[i] = BeanUtils.getPropertyDescriptor(mappedClass, names[i]);
            names[i] = StringUtils.upperCase(names[i]);
        }
        for (int i = 0; i < alias.length; i++) {
            alias[i] = StringUtils.upperCase(alias[i]);
        }
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T bean = super.mapRow(rs, rowNumber);
        Map<String, Integer> labels = labels(rs.getMetaData());
        for (int i = 0; i < alias.length; i++) {
            if (labels.containsKey(alias[i]) && pds[i].getWriteMethod() != null) {
                ReflectionUtils.invokeMethod(pds[i].getWriteMethod(), bean, JdbcUtils.getResultSetValue(rs, labels.get(alias[i]), pds[i].getPropertyType()));
            }
        }
        return bean;
    }

    private Map<String, Integer> labels(ResultSetMetaData rsmd) throws SQLException {
        if (labels == null) {
            int count = rsmd.getColumnCount();
            labels = new HashMap<>(count);
            for (int i = 1; i <= count; i++) {
                String label = rsmd.getColumnLabel(i);
                if (StringUtils.isEmpty(label)) {
                    label = rsmd.getColumnName(i);
                }
                labels.put(StringUtils.upperCase(label), i);
            }
        }
        return labels;
    }

    public static <T> AliasPropertyRowMapper<T> newInstance(Class<T> mappedClass, String[] names, String[] alias) {
        AliasPropertyRowMapper<T> newInstance = new AliasPropertyRowMapper<>(names, alias);
        newInstance.setMappedClass(mappedClass);
        return newInstance;
    }
}
