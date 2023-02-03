package com.dingtalk.open.example.model;

import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import lombok.Data;

/**
 * 说明：数据库表下列的元信息
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@SuppressWarnings("unused")
@Data
public class TableColumn {
    /**
     * 列名
     */
    String name;
    /**
     * 该列是否为主键
     */
    boolean primaryKey;
    /**
     * 该列的数据类型
     */
    String dataType;
    /**
     * 该列的备注信息 comment
     */
    String description;
    /**
     * 该列是否不为空
     */
    boolean notnull;
    /**
     * 该列是否是自增列
     */
    boolean autoIncrement;

    /**
     * 将表列的类型信息转换为json格式
     *
     * @return 列对应的json格式
     */
    public JsonSchema toJsonSchema() {
        JsonSchema fieldSchema = new JsonSchema();
        fieldSchema.setType(MysqlDatabaseHelper.DATE_TYPE_MAP.get(this.getDataType()));
        fieldSchema.setTitle("列" + this.getName());
        fieldSchema.setDescription(this.getDescription());
        return fieldSchema;
    }
}
