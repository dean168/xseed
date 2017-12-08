package org.learning.basic.core.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.learning.basic.utils.BeanUtils.Type;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.learning.basic.utils.BeanUtils.Type.SAVE;

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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createdAt;
	/**
	 * 创建人
	 */
	@JsonProperty("createdBy")
	private String createdBy;
	/**
	 * 最后更新时间
	 */
	@JsonProperty("updatedAt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatedAt;
	/**
	 * 最后更新人
	 */
	@JsonProperty("updatedBy")
	private String updatedBy;

	public void reset() {
		this.id = null;
	}

	public <T extends Basic> T copyTo(T basic, Type type) {
		if (SAVE.equals(type)) {
			SessionContext context = SessionContext.get();
			String cu = context != null ? context.currentAccountId() : null;
			Date now = new Date();
			basic.setCreatedAt(now);
			basic.setCreatedBy(cu);
			basic.setUpdatedAt(now);
			basic.setUpdatedBy(cu);
		} else {
			basic.setId(getId());
			basic.setCreatedAt(getCreatedAt());
			basic.setCreatedBy(getCreatedBy());
			basic.setUpdatedAt(getUpdatedAt());
			basic.setUpdatedBy(getUpdatedBy());
		}
		return basic;
	}

	public void created(String createdBy, Date createdAt) {
		this.createdBy = createdBy;
		this.createdAt = createdAt;
	}

	public void updated(String updatedBy, Date updatedAt) {
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}
