package org.learning.basic.shiro.service;

import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;

public interface IShiroSubject {

    String SERVICE_ID = "basic.shiroSubject";

    Subject subject(HttpServletRequest request);
}
