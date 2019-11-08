package org.learning.basic.web.controls;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.IDictService;
import org.learning.basic.core.domain.Dict;
import org.learning.basic.core.domain.Pagination;
import org.learning.basic.utils.ByteUtils;
import org.learning.basic.utils.JsonUtils.Jackson;
import org.learning.basic.utils.StatusUtils.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;

//@RestController
//@RequestMapping("dict")
public class DictController extends BasicController {

    private static final Logger logger = LoggerFactory.getLogger(DictController.class);

    @Autowired
    @Qualifier(IDictService.SERVICE_ID)
    protected IDictService dictService;

//    @RequestMapping(method = POST, value = "get", produces = {APPLICATION_JSON_VALUE})
    public <D extends Dict> Status<D> get(@RequestBody Dict dict) {
        return new Status<>(true, null, dictService.get(dict));
    }

//    @RequestMapping(method = POST, value = "search", produces = {APPLICATION_JSON_VALUE})
    public <D extends Dict> Status<Pagination<D>> search(@RequestBody Dict dict, @RequestParam int offset, @RequestParam int limit) {
        return new Status<>(true, null, dictService.search(dict, offset, limit));
    }

//    @RequestMapping(method = POST, value = "list", produces = {APPLICATION_JSON_VALUE})
    public <D extends Dict> Status<Pagination<D>> list(@RequestBody Dict dict, @RequestParam int offset, @RequestParam int limit) {
        return new Status<>(true, null, dictService.list(dict, offset, limit));
    }

    @SuppressWarnings("unchecked")
//    @RequestMapping(method = POST, value = "store", produces = {APPLICATION_JSON_VALUE})
    public Status<?> store(InputStream is) throws IOException, ClassNotFoundException {
        String content = IOUtils.toString(is, ByteUtils.CHARSET_NAME);
        Dict dict = Jackson.readValue(content, Dict.class);
        if (StringUtils.isEmpty(dict.getName())) {
            return new Status<>(false, "请输入名称");
        } else {
            Class<? extends Dict> clazz = (Class<? extends Dict>) ClassUtils.forName(dict.getType(), ClassUtils.getDefaultClassLoader());
            Dict dictToUse = Jackson.readValue(content, clazz);
            return new Status<>(true, "保存成功", dictService.store(dictToUse));
        }
    }

//    @RequestMapping(method = DELETE, value = "delete")
    public Status<?> delete(@RequestParam String type, @RequestParam String[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return new Status<>(false, "没有要删除的ID");
        } else {
            try {
                Class<?> clazz = Class.forName(type);
                dictService.delete(clazz, ids);
                return new Status<>(true, "删除成功");
            } catch (Exception e) {
                logger.error(null, e);
                return new Status<>(false, e.getMessage());
            }
        }
    }
}
