package org.learning.portal.web;

import org.learning.basic.shiro.web.controller.ShiroAccountController;
import org.learning.portal.domain.Staff;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class PortalAccountController extends ShiroAccountController<Staff> {

    @Override
    protected Class<Staff> accountClass() {
        return Staff.class;
    }
}
