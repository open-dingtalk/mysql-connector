package com.dingtalk.open.example.support;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 说明：一些Mysql数据库相关的处理工作逻辑
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class MysqlDatabaseHelper {
    /**
     * 敏感关键字
     */
    public final static String[] KEYWORDS = new String[]{
            "ADD",
            "ALL",
            "ALTER",
            "ANALYZE",
            "AS",
            "ASC",
            "ASENSITIVE",
            "BEFORE",
            "BETWEEN",
            "BIGINT",
            "BINARY",
            "BLOB",
            "BOTH",
            "BY",
            "CALL",
            "CASCADE",
            "CASE",
            "CHANGE",
            "CHAR",
            "CHARACTER",
            "CHECK",
            "COLLATE",
            "COLUMN",
            "CONDITION",
            "CONNECTION",
            "CONSTRAINT",
            "CONTINUE",
            "CONVERT",
            "CREATE",
            "CROSS",
            "CURRENT_DATE",
            "CURRENT_TIME",
            "CURRENT_TIMESTAMP",
            "CURRENT_USER",
            "CURSOR",
            "DATABASE",
            "DATABASES",
            "DAY_HOUR",
            "DAY_MICROSECOND",
            "DAY_MINUTE",
            "DAY_SECOND",
            "DEC",
            "DECIMAL",
            "DECLARE",
            "DEFAULT",
            "DELAYED",
            "DELETE"
    };
    /**
     * mysql数据类型到json类型的映射配置
     */
    public static final Map<String, String> DATE_TYPE_MAP;

    static {
        // 这里定义了，表的字段对应的json字段类型
        DATE_TYPE_MAP = new HashMap<>();
        DATE_TYPE_MAP.put("varchar", "string");
        DATE_TYPE_MAP.put("enum", "string");
        DATE_TYPE_MAP.put("binary", "string");
        DATE_TYPE_MAP.put("tinyblob", "string");
        DATE_TYPE_MAP.put("mediumblob", "string");
        DATE_TYPE_MAP.put("blob", "string");
        DATE_TYPE_MAP.put("longblob", "string");
        DATE_TYPE_MAP.put("tinytext", "string");
        DATE_TYPE_MAP.put("text", "string");
        DATE_TYPE_MAP.put("mediumtext", "string");
        DATE_TYPE_MAP.put("longtext", "string");
        DATE_TYPE_MAP.put("bit", "number");
        DATE_TYPE_MAP.put("tinyint", "number");
        DATE_TYPE_MAP.put("bigint", "number");
        DATE_TYPE_MAP.put("int", "number");
        DATE_TYPE_MAP.put("SMALLINT", "number");
        DATE_TYPE_MAP.put("mediumint", "number");
        DATE_TYPE_MAP.put("integer", "number");
        DATE_TYPE_MAP.put("float", "number");
        DATE_TYPE_MAP.put("double", "number");
        DATE_TYPE_MAP.put("double PRECISION", "number");
        DATE_TYPE_MAP.put("decimal", "number");
        DATE_TYPE_MAP.put("dec", "number");
        DATE_TYPE_MAP.put("set", "array");
        DATE_TYPE_MAP.put("date", "number");
        DATE_TYPE_MAP.put("datetime", "number");
        DATE_TYPE_MAP.put("timestamp", "number");
        DATE_TYPE_MAP.put("time", "number");
        DATE_TYPE_MAP.put("year", "number");
        DATE_TYPE_MAP.put("bool", "boolean");
        DATE_TYPE_MAP.put("boolean", "boolean");
    }

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 根据库名、表名获取数据表的元信息
     *
     * @param database  数据库名
     * @param tableName 数据表名
     * @return 数据表的元信息
     */
    public Table getTableInfo(String database, String tableName) {
        JSONObject paramMap = new JSONObject().fluentPut("database", database).fluentPut("tableName", tableName);
        return namedParameterJdbcTemplate.queryForList(
                        "SELECT TABLE_NAME,TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=:database AND TABLE_NAME=:tableName",
                        paramMap).stream()
                .findFirst()
                .map(value -> {
                    Table table = new Table();
                    table.setName(tableName);
                    table.setDatabase(database);
                    table.setDescription(String.valueOf(value.get("TABLE_COMMENT")));
                    List<Map<String, Object>> columnsResult = namedParameterJdbcTemplate.queryForList(
                            "SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE, COLUMN_COMMENT,COLUMN_KEY,EXTRA FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=:database AND TABLE_NAME=:tableName",
                            paramMap);
                    table.setColumns(columnsResult.stream()
                            .map(JSONObject::new)
                            .map(json -> {
                                TableColumn tableColumn = new TableColumn();
                                tableColumn.setName(json.getString("COLUMN_NAME"));
                                tableColumn.setDataType(json.getString("DATA_TYPE"));
                                tableColumn.setDescription(json.getString("COLUMN_COMMENT"));
                                tableColumn.setNotnull("NO".equals(json.getString("IS_NULLABLE")));
                                tableColumn.setPrimaryKey("PRI".equals(json.getString("COLUMN_KEY")));
                                tableColumn.setAutoIncrement(StringUtils.contains(json.getString("EXTRA"), "auto_increment"));
                                return tableColumn;
                            }).collect(Collectors.toList()));
                    return table;
                }).orElse(null);
    }
}
