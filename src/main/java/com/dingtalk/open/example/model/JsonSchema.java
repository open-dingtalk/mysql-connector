package com.dingtalk.open.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 说明：钉钉连接平台JsonSchema的Pojo类，用来描述Json数据结构的结构
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@SuppressWarnings("unused")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSchema {
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private String type;
    /**
     * 描述
     */
    private String description;
    /**
     * 类型为数组时的子元素类型
     */
    private JsonSchema items;
    /**
     * 类型为对象时的必填字段
     */
    private Set<String> required;
    /**
     * 类型为对象时的字段及字段类型
     */
    private Map<String, JsonSchema> properties;
}
