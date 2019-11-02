package org.learning.basic.shiro.domain;

import java.io.Serializable;

public class AccountRole implements Serializable {

    private ShiroAccount account;
    private ShiroRole role;

    public ShiroAccount getAccount() {
        return account;
    }

    public void setAccount(ShiroAccount account) {
        this.account = account;
    }

    public ShiroRole getRole() {
        return role;
    }

    public void setRole(ShiroRole role) {
        this.role = role;
    }
}
