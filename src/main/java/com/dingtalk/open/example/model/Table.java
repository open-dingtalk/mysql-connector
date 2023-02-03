package com.dingtalk.open.example.model;

import lombok.Data;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 说明：数据库表的元信息
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@SuppressWarnings("unused")
@Data
public class Table {
    /**
     * 表名
     */
    String name;
    /**
     * 表的备注comment
     */
    String description;
    /**
     * 所属的mysql schema
     */
    String database;
    /**
     * 表的列信息
     */
    List<TableColumn> columns;

    /**
     * 得到表数据结构对应的SCHEMA
     *
     * @return JSON入参模型
     */
    public JsonSchema toJsonSchema() {
        JsonSchema dataSchema = new JsonSchema();
        dataSchema.setTitle("数据");
        dataSchema.setDescription("表" + name + "的单行数据");
        dataSchema.setType("object");
        // 数据表中不为空的字段
        Set<String> required = ListUtils.emptyIfNull(columns).stream()
                .filter(TableColumn::isNotnull)
                // 必填，但是自增的，可不用必填
                .filter(columns -> !columns.isAutoIncrement())
                .map(TableColumn::getName)
                .collect(Collectors.toSet());
        dataSchema.setRequired(required);
        // 将表的列变换为json的字段
        Map<String, JsonSchema> properties = ListUtils.emptyIfNull(columns).stream().map(column -> {
            JsonSchema fieldSchema = column.toJsonSchema();
            return Pair.of(column.getName(), fieldSchema);
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        dataSchema.setProperties(properties);
        return dataSchema;
    }

    /**
     * 得到主键部分的SCHEMA
     *
     * @return JSON入参模型
     */
    public JsonSchema toIdJsonSchema() {
        JsonSchema idSchema = new JsonSchema();
        idSchema.setTitle("表" + name + "的唯一标识");
        idSchema.setType("object");
        idSchema.setDescription("确定数据记录的唯一标识数据");
        // 所有的主键都是唯一标识
        List<TableColumn> primaryColumns = ListUtils.emptyIfNull(columns).stream().filter(TableColumn::isPrimaryKey)
                .collect(Collectors.toList());
        Map<String, JsonSchema> idFields = primaryColumns.stream()
                .collect(Collectors.toMap(TableColumn::getName, TableColumn::toJsonSchema));
        idSchema.setProperties(idFields);
        idSchema.setRequired(idFields.keySet());
        return idSchema;
    }
}
