package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：单条数据更新执行动作的执行逻辑
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class UpdateActionExecutor implements ActionExecutor{
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Resource
    private MySqlParameterSourceFactory mySqlParameterSourceFactory;

    @Override
    public Object execute(String corpId,
                          String database,
                          String tableName,
                          JSONObject input) throws SpiRuntimeException {
        Table tableInfo = mysqlDatabaseHelper.getTableInfo(database, tableName);
        // 根据schema约定，update操作数据分为data数据部分，idData主键数据部分
        JSONObject idDataJson = input.getJSONObject("idData");
        JSONObject dataJson = input.getJSONObject("data");
        List<String> dataFields = tableInfo.getColumns().stream()
                .filter(tableColumn -> !tableColumn.isPrimaryKey())
                .map(TableColumn::getName).collect(Collectors.toList());
        List<String> idFields = tableInfo.getColumns().stream().filter(TableColumn::isPrimaryKey).map(TableColumn::getName).collect(Collectors.toList());
        if (idFields.isEmpty()) {
            throw new SpiRuntimeException("business_error", "不支持无主键表的更新");
        }
        // 主键作为条件不能为空，进行校验
        for (String idField : idFields) {
            Object idValue = idDataJson.get(idField);
            if (idValue == null) {
                throw new SpiRuntimeException("invalid_integration_object_action_request", "字段[" + idField + "]值不能为空");
            }
        }
        // 没有要更新的字段，就不更新
        String setFieldValueSql = dataFields.stream().filter(field -> input.get(field) != null).map(field -> field + "=:" + field).collect(Collectors.joining(", "));
        if (setFieldValueSql.length() == 0) {
            return 0;
        }
        // 基本的SQL
        String updateSql = "UPDATE " + database + "." + tableName + " SET " + setFieldValueSql + " WHERE ";
        // 主键作为条件的SQL
        String conditionSql = idFields.stream().map(idField -> idField + "=:" + idField)
                .collect(Collectors.joining(" AND "));
        updateSql += conditionSql;
        JSONObject parameters = new JSONObject();
        parameters.putAll(dataJson);
        parameters.putAll(idDataJson);
        return namedParameterJdbcTemplate.update(updateSql, mySqlParameterSourceFactory.create(tableInfo, parameters));
    }

}
