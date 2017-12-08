package org.learning.basic.core.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(creatorVisibility = Visibility.NONE, fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class Pagination<T> {

	@JsonProperty("offset")
	private int offset;
	@JsonProperty("limit")
	private int limit;
	@JsonProperty("total")
	private int total;
	@JsonProperty("result")
	private List<T> result;

	public Pagination() {
	}

    public Pagination(int total, List<T> result) {
        this(0, 0, total, result);
    }

	public Pagination(int offset, int limit, int total, List<T> result) {
		this.offset = offset;
		this.limit = limit;
		this.total = total;
		this.result = result;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
}
