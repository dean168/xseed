package org.learning.basic.core.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class Basic {

    /**
     * id
     */
    @JsonProperty("id")
    private String id;
    /**
     * 创建时间
     */
    @JsonProperty("createdAt")
    private long createdAt;
    /**
     * 创建人
     */
    @JsonProperty("createdBy")
    private String createdBy;
    /**
     * 最后更新时间
     */
    @JsonProperty("updatedAt")
    private long updatedAt;
    /**
     * 最后更新人
     */
    @JsonProperty("updatedBy")
    private String updatedBy;

    public void reset() {
        this.id = null;
    }

    public void created(String createdBy, long createdAt) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public void updated(String updatedBy, long updatedAt) {
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
