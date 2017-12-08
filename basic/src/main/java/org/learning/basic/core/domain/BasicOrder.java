package org.learning.basic.core.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.learning.basic.utils.BeanUtils.Type;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class BasicOrder extends Basic {

	/**
	 * 排序字段
	 */
	@JsonProperty("order")
	private Integer order;

	@Override
	public <T extends Basic> T copyTo(T basic, Type type) {
		BasicOrder bo = (BasicOrder) super.copyTo(basic, type);
		bo.order = getOrder();
		return basic;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
