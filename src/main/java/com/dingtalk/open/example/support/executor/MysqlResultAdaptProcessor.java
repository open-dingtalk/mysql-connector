package com.dingtalk.open.example.support.executor;

import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.data.JsonToSql;
import com.dingtalk.open.example.support.data.JsonToSqlValueAdaptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：Mysql查询结果适配为Json类型的处理
 *
 * @author donghuai.zjj
 * @date 2022/12/08
 */
@Component
public class MysqlResultAdaptProcessor {
    @Resource
    private List<JsonToSqlValueAdaptor> jsonToSqlValueAdaptors;

    public Map<String, Object> process(Map<String, Object> result, Table table) {
        if (result == null) {
            return null;
        }
        HashMap<String, Object> processedResult = new HashMap<>(result.size());
        for (TableColumn column : table.getColumns()) {
            processedResult.put(column.getName(), result.get(column.getName()));
            jsonToSqlValueAdaptors.stream().filter(adaptor -> {
                JsonToSql annotation = adaptor.getClass().getAnnotation(JsonToSql.class);
                return annotation.sqlType().equals(column.getDataType());
            }).findFirst().ifPresent(adaptor -> {
                Object value = result.get(column.getName());
                Object jsonValue = adaptor.toJsonValue(value);
                processedResult.put(column.getName(), jsonValue);
            });
        }
        return processedResult;
    }
}
