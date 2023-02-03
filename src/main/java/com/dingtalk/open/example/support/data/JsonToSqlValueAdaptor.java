package com.dingtalk.open.example.support.data;

/**
 * 说明：JSON值与SQL值的适配器
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
public interface JsonToSqlValueAdaptor {
    /***
     * 转换为SQL值
     * @param jsonValue json值
     * @return SQL值
     */
    Object toSqlValue(Object jsonValue);

    /**
     * 转换为Json值
     * @param sqlValue sql值
     * @return json值
     */
    default Object toJsonValue(Object sqlValue) {
        return sqlValue;
    }
}
