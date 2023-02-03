package com.dingtalk.open.example.support.schema;

import com.dingtalk.open.example.model.JsonSchema;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.response.GetIntegrationObjectSchemaResponse;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 说明：删除单条数据的出入参模型描述
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class DeleteSchemaProvider implements ActionSchemaProvider {
    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Override
    public GetIntegrationObjectSchemaResponse getSchema(String corpId, String database, String table) throws SpiRuntimeException {
        Table tableInfo = Optional.ofNullable(mysqlDatabaseHelper.getTableInfo(database, table))
                .orElseThrow(() -> new SpiRuntimeException("integration_object_not_exists", "表不存在"));
        // 设置更新时要求传入的数据唯一标识信息
        JsonSchema outputSchema = new JsonSchema();
        outputSchema.setType("number");
        outputSchema.setTitle("写入记录数");
        outputSchema.setDescription("更新数据所影响的行数");
        GetIntegrationObjectSchemaResponse response = new GetIntegrationObjectSchemaResponse();
        response.setInputSchema(tableInfo.toIdJsonSchema());
        response.setOutputSchema(outputSchema);
        response.setName(table);
        response.setIntegrationObject("mysql://" + corpId + "/" + database + "?table=" + table);
        return response;
    }

}
