package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：写入或更新的逻辑
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class UpsertActionExecutor implements ActionExecutor{
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
                          JSONObject input) {
        Table tableInfo = mysqlDatabaseHelper.getTableInfo(database, tableName);
        List<String> fields = tableInfo.getColumns().stream().map(TableColumn::getName).collect(Collectors.toList());
        String insertSql = "INSERT INTO " + database + "." + tableName + "(" + StringUtils.join(fields, ',') + ") VALUES ";
        insertSql += fields.stream().map(value ->":" + value).collect(Collectors.joining(", ", "(", ")"));
        String updateSql = fields.stream().filter(field -> input.get(field) != null).map(field -> field + "=:" + field).collect(Collectors.joining(", "));
        if (updateSql.length() > 0) {
            insertSql += " ON DUPLICATE KEY UPDATE ";
            insertSql += updateSql;
        }
        return namedParameterJdbcTemplate.update(insertSql, mySqlParameterSourceFactory.create(tableInfo, input));
    }
}
