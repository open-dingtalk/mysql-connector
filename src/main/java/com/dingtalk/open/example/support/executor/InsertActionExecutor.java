package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：插入类型的执行动作执行
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class InsertActionExecutor implements ActionExecutor{
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
        List<String> fields = tableInfo.getColumns().stream().map(TableColumn::getName).collect(Collectors.toList());
        String insertSql = "INSERT INTO " + database + "." + tableName + "(" + StringUtils.join(fields, ',') + ") VALUES ";
        insertSql += fields.stream().map(value ->":" + value).collect(Collectors.joining(",", "(", ")"));
        try {
            return namedParameterJdbcTemplate.update(insertSql, mySqlParameterSourceFactory.create(tableInfo, input));
        } catch (DuplicateKeyException e) {
            throw new SpiRuntimeException("business_error", "相同主键或唯一约束的数据已经存在");
        }
    }

}
