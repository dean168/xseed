package org.learning.basic.core.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.learning.basic.utils.BeanUtils;
import org.springframework.util.ClassUtils;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 字典父类
 */
@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class Dict extends Ordered {

    /**
     * 字典类型
     */
    @JsonProperty("type")
    private String type;
    /**
     * 字典名称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 字典描述
     */
    @JsonProperty("desc")
    private String desc;
    /**
     * 字典父ID
     */
    @JsonProperty("parent")
    private String parent;
    /**
     * 子字典数量
     */
    @JsonProperty("cc")
    private Integer ccount;
    /**
     * 唯一路径
     */
    @JsonProperty("xpath")
    private String xpath;
    /**
     * 唯一名称
     */
    @JsonProperty("uname")
    private String uname;
    /**
     * 别名
     *
     * @see #delimiters()
     * 为了方便数据 like 查询, 别名前面和后面都加上分隔符, 如: |aaa|bbb|ccc|
     * 如要更改分隔符, 子类重写 delimiters() 方法
     */
    @JsonProperty("alias")
    private String alias;

    public Dict() {
    }

    public static <D extends Dict> D create(Class<D> clazz) {
        D dict = BeanUtils.instantiateClass(clazz);
        dict.setType(clazz.getName());
        return dict;
    }

    public String delimiters() {
        return "|";
    }

    public String getType() {
        return isNotEmpty(type) ? type : ClassUtils.getUserClass(this).getName();
    }

    public void setType(String type) {
        this.type = type;
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getCcount() {
        return ccount;
    }

    public void setCcount(Integer ccount) {
        this.ccount = ccount;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
