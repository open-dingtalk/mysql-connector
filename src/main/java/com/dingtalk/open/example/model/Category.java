package com.dingtalk.open.example.model;

import lombok.Data;

/**
 * 说明：spi声明的类目信息
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@SuppressWarnings("unused")
@Data
public class Category {
    /**
     * 图标
     */
    private String icon;
    /**
     * 分类名
     */
    private String name;
    /**
     * 分类标识
     */
    private String value;
}
