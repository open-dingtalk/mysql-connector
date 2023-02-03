package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.TableColumn;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：自定义列表查询执行动作的执行逻辑
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
@Slf4j
public class SelectListActionExecutor implements ActionExecutor{
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
        String conditionParametersJsonString = input.getString("conditionParametersJsonString");
        String conditionTplSql = input.getString("conditionTplSql");
        if (StringUtils.isBlank(conditionTplSql)) {
            throw new SpiRuntimeException("business_error", "SQL查询条件模板不能为空");
        }
        if (StringUtils.containsAnyIgnoreCase(conditionTplSql, MysqlDatabaseHelper.KEYWORDS)) {
            throw new SpiRuntimeException("business_error", "SQL查询条件模板不能包含以下关键字:" + JSON.toJSONString(MysqlDatabaseHelper.KEYWORDS));
        }
        if (!JSON.isValidObject(conditionParametersJsonString)) {
            throw new SpiRuntimeException("business_error", "SQL条件的值的JSON文本格式，需要是一个合法json object文本");
        }
        JSONObject parameters = JSONObject.parseObject(conditionParametersJsonString);
        // 生成查询语句
        String selectSql = fields.stream().collect(Collectors.joining(", ", "SELECT ", " FROM " + database +"." + tableName));
        // 主键作为条件
        selectSql += " WHERE " + conditionTplSql;
        // 按照schema进行返回
        SqlParameterSource sqlParameterSource = mySqlParameterSourceFactory.create(tableInfo, parameters);
        try {
            return namedParameterJdbcTemplate.queryForList(selectSql, sqlParameterSource).stream()
                    .map(data -> mysqlResultAdaptProcessor.process(data, tableInfo))
                    .collect(Collectors.toList());
        }catch (BadSqlGrammarException e) {
            log.warn("bad sql, corpId={}, database={}, tableName={}", corpId, database, tableName, e);
            throw new SpiRuntimeException("business_error", "SQL查询条件语法错误");
        }
    }

}
