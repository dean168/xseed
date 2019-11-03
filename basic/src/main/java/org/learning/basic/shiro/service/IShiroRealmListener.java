package org.learning.basic.shiro.service;

public interface IShiroRealmListener {

    void changed(Class<?> type, String id) throws Exception;
}
