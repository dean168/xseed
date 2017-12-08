package org.learning.basic.shiro.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class ShiroRole {

	@JsonProperty("id")
	private String id;
    @JsonProperty("code")
	private String code;
    @JsonProperty("desc")
	private String desc;
    @JsonProperty("perms")
	private Set<ShiroPermission> perms = new HashSet<>();

	public Set<String> getPermissionCodes() {
		Set<String> codes = new HashSet<>();
		for (ShiroPermission permission : perms) {
			codes.add(permission.getCode());
		}
		return codes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
