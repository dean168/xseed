package org.learning.basic.shiro.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.learning.basic.core.domain.Basic;

import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ShiroRole extends Basic {

    @JsonProperty("name")
    private String name;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("perms")
    private Set<ShiroPermission> perms = new HashSet<>();

    public Set<String> permissions() {
        Set<String> permsToUse = new HashSet<>();
        for (ShiroPermission permission : perms) {
            permsToUse.add(permission.getId());
        }
        return permsToUse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<ShiroPermission> getPerms() {
        return perms;
    }

    public void setPerms(Set<ShiroPermission> perms) {
        this.perms = perms;
    }
}
