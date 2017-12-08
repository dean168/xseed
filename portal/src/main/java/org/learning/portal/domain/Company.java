package org.learning.portal.domain;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import org.learning.basic.core.domain.Dict;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class Company extends Dict {

    /**
     * 电话号码
     */
    @JsonProperty("phone")
    private String phone;
    /**
     * 电子邮箱
     */
    @JsonProperty("email")
    private String email;
    /**
     * 公司规模，多少人
     */
    @JsonProperty("persons")
    private String persons;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersons() {
        return persons;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }
}
