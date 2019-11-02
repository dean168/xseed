package org.learning.basic.shiro.domain;

import java.io.Serializable;

public class RolePermission implements Serializable {

    private ShiroRole role;
    private ShiroPermission permission;

    public ShiroRole getRole() {
        return role;
    }

    public void setRole(ShiroRole role) {
        this.role = role;
    }

    public ShiroPermission getPermission() {
        return permission;
    }

    public void setPermission(ShiroPermission permission) {
        this.permission = permission;
    }
}
