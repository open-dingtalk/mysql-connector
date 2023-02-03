package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.JsonSchema;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.data.JsonToSql;
import com.dingtalk.open.example.support.data.JsonToSqlValueAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明：MYSQL SQL参数值数据源工厂
 *
 * @author donghuai.zjj
 * @date 2022/12/08
 */
@Component
public class MySqlParameterSourceFactory  {

    @Resource
    private List<JsonToSqlValueAdaptor> jsonToSqlValueAdaptors;


    public SqlParameterSource create(Table table, JSONObject parameters) {
        return new MySqlParameterSource(parameters, table);
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    private class MySqlParameterSource implements SqlParameterSource {

        private final JSONObject parameters;

        private final Table table;

        @SuppressWarnings("NullableProblems")
        @Override
        public boolean hasValue(String paramName) {
            return true;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            TableColumn column = table.getColumns().stream().filter(tableColumn -> paramName.equals(tableColumn.getName())).findFirst().orElse(null);
            if (column == null) {
                return null;
            }
            JsonSchema columnSchema = column.toJsonSchema();
            String dataType = column.getDataType();
            JsonToSqlValueAdaptor jsonToSqlValueAdaptor = jsonToSqlValueAdaptors.stream()
                    .filter(adaptor -> {
                        JsonToSql annotation = adaptor.getClass().getAnnotation(JsonToSql.class);
                        if (annotation == null) {
                            return false;
                        }
                        return annotation.sqlType().equals(dataType) && annotation.jsonType().equals(columnSchema.getType());
                    })
                    .findFirst()
                    .orElseGet(() -> (value) -> value);
            return jsonToSqlValueAdaptor.toSqlValue(parameters.get(paramName));
        }
    }
}
