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
 * 说明：更新数据操作时的Schema，大体是这样的一个结构
 * <pre>{ data -> 更新数据, idData -> 唯一标识 }</pre>
 * data部分表示要更新的数据，idData部分表示数据的唯一标识，是数据更新的条件
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class UpdateSchemaProvider implements ActionSchemaProvider {
    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Override
    public GetIntegrationObjectSchemaResponse getSchema(String corpId, String database, String table) throws SpiRuntimeException {
        Table tableInfo = Optional.ofNullable(mysqlDatabaseHelper.getTableInfo(database, table))
                .orElseThrow(() -> new SpiRuntimeException("integration_object_not_exists", "表不存在"));
        JsonSchema inputSchema = new JsonSchema();
        inputSchema.setType("object");
        inputSchema.setDescription("更新数据记录");
        inputSchema.setTitle("更新数据入参");
        HashMap<String, JsonSchema> properties = new HashMap<>(2);
        // 设置要更新的数据结构为表结构对应的jsonSchema
        properties.put("data", tableInfo.toJsonSchema());
        // 设置更新时要求传入的数据唯一标识信息
        JsonSchema idSchema = tableInfo.toIdJsonSchema();
        properties.put("idData", idSchema);
        inputSchema.setProperties(properties);
        JsonSchema outputSchema = new JsonSchema();
        outputSchema.setType("number");
        outputSchema.setTitle("写入记录数");
        outputSchema.setDescription("更新数据所影响的行数");
        GetIntegrationObjectSchemaResponse response = new GetIntegrationObjectSchemaResponse();
        response.setInputSchema(inputSchema);
        response.setOutputSchema(outputSchema);
        response.setName(table);
        response.setIntegrationObject("mysql://" + corpId + "/" + database + "?table=" + table);
        return response;
    }

}
