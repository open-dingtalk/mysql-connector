package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：单条数据根据主键查询的执行动作执行逻辑
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class SelectActionExecutor implements ActionExecutor{
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Resource
    private MySqlParameterSourceFactory mySqlParameterSourceFactory;

    @Resource
    private MysqlResultAdaptProcessor mysqlResultAdaptProcessor;

    @Override
    public Object execute(String corpId,
                          String database,
                          String tableName,
                          JSONObject input) throws SpiRuntimeException {
        Table tableInfo = mysqlDatabaseHelper.getTableInfo(database, tableName);
        // 根据schema约定，update操作数据分为data数据部分，idData主键数据部分
        List<String> fields = tableInfo.getColumns().stream().map(TableColumn::getName).collect(Collectors.toList());
        List<String> idFields = tableInfo.getColumns().stream().filter(TableColumn::isPrimaryKey).map(TableColumn::getName)
                .collect(Collectors.toList());
        if (idFields.isEmpty()) {
            throw new SpiRuntimeException("business_error", "不支持无主键表的更新");
        }
        // 主键作为条件不能为空，进行校验
        for (String idField : idFields) {
            Object idValue = input.get(idField);
            if (idValue == null) {
                throw new SpiRuntimeException("invalid_integration_object_action_request", "字段[" + idField + "]值不能为空");
            }
        }
        // 生成查询语句
        String selectSql = fields.stream().collect(Collectors.joining(", ", "SELECT ", " FROM " + database +"." + tableName));
        // 主键作为条件
        selectSql += " WHERE " + idFields.stream().map(idField -> idField + "=:" + idField)
                .collect(Collectors.joining(" AND "));
        // 按照schema进行返回
        SqlParameterSource sqlParameterSource = mySqlParameterSourceFactory.create(tableInfo, input);
        return namedParameterJdbcTemplate.queryForList(selectSql, sqlParameterSource).stream()
                .map(data -> mysqlResultAdaptProcessor.process(data, tableInfo))
                .findFirst().orElse(null);
    }

}
