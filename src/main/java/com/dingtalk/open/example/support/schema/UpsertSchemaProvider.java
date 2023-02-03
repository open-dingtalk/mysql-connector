package com.dingtalk.open.example.support.schema;

import com.dingtalk.open.example.model.JsonSchema;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.response.GetIntegrationObjectSchemaResponse;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 说明：写入或更新单条数据的出入参模型描述
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class UpsertSchemaProvider implements ActionSchemaProvider {
    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Override
    public GetIntegrationObjectSchemaResponse getSchema(String corpId, String database, String table) throws SpiRuntimeException {
        Table tableInfo = Optional.ofNullable(mysqlDatabaseHelper.getTableInfo(database, table))
                .orElseThrow(() -> new SpiRuntimeException("integration_object_not_exists", "表不存在"));
        JsonSchema outputSchema = new JsonSchema();
        outputSchema.setType("number");
        outputSchema.setTitle("写入记录数");
        outputSchema.setDescription("插入数据所影响的行数");
        GetIntegrationObjectSchemaResponse response = new GetIntegrationObjectSchemaResponse();
        response.setInputSchema(tableInfo.toJsonSchema());
        response.setOutputSchema(outputSchema);
        response.setName(table);
        response.setIntegrationObject("mysql://" + corpId + "/" + database + "?table=" + table);
        return response;
    }
}
