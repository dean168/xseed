package org.learning.basic.core;

import org.learning.basic.core.domain.Dict;
import org.learning.basic.core.domain.Pagination;

import java.io.Serializable;
import java.util.List;

public interface IDictService {

	String SERVICE_ID = "basic.dictService";

	<D extends Dict> D get(Dict dict);

	<D extends Dict> Pagination<D> search(Dict dict, int offset, int limit);

	<D extends Dict> Pagination<D> list(Dict dict, int offset, int limit);

	<D extends Dict> List<D> list(Class<D> clazz, String... ids);

	<D extends Dict> D prepared(Dict dict, D target);

	<D extends Dict> D store(D dict);

	void delete(Class<?> clazz, Serializable... ids);
}
