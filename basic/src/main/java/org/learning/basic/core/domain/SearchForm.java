package org.learning.basic.core.domain;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class SearchForm extends Basic {

	/**
	 * 关键字
	 */
	@JsonProperty("keywords")
	private String keywords;

	/**
	 * 排序类型
	 */
	@JsonProperty("order")
	private String order;
	
	/**
	 * 排序方式
	 */
	@JsonProperty("sort")
	private SortType sort;

	/**
	 * 分页参数,从第几条开始
	 */
	@JsonProperty("offset")
	private int offset;

	/**
	 * 分页参数,返回多少条
	 */
	@JsonProperty("limit")
	private int limit;

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public SortType getSort() {
		return sort;
	}

	public void setSort(SortType sort) {
		this.sort = sort;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
