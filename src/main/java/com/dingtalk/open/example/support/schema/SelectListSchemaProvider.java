package com.dingtalk.open.example.support.schema;

import com.dingtalk.open.example.model.JsonSchema;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.response.GetIntegrationObjectSchemaResponse;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

/**
 * 说明：列表查询数据的出入参模型描述
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Component
public class SelectListSchemaProvider implements ActionSchemaProvider {
    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;
    @Override
    public GetIntegrationObjectSchemaResponse getSchema(String corpId, String database, String table) throws SpiRuntimeException {
        GetIntegrationObjectSchemaResponse response = new GetIntegrationObjectSchemaResponse();
        Table tableInfo = Optional.ofNullable(mysqlDatabaseHelper.getTableInfo(database, table))
                .orElseThrow(() -> new SpiRuntimeException("integration_object_not_exists", "表不存在"));
        JsonSchema tableSchema = tableInfo.toJsonSchema();
        JsonSchema outputSchema = new JsonSchema();
        outputSchema.setType("array");
        outputSchema.setTitle("表"+table+"数据列表");
        outputSchema.setItems(tableSchema);
        response.setOutputSchema(outputSchema);
        JsonSchema inputSchema = new JsonSchema();
        inputSchema.setType("object");
        inputSchema.setTitle("查询参数");
        HashSet<String> required = new HashSet<>();
        required.add("conditionTplSql");
        required.add("conditionParametersJsonString");
        inputSchema.setRequired(required);
        HashMap<String, JsonSchema> properties = new HashMap<>(2);
        JsonSchema conditionSchema = new JsonSchema();
        conditionSchema.setType("string");
        conditionSchema.setTitle("SQL条件模板");
        conditionSchema.setDescription("查询SQL时的条件模板, 占位符为 :parameterName, 比如 column_01 = :value1 AND column_02 = :value2 这样的形式");
        properties.put("conditionTplSql", conditionSchema);
        JsonSchema conditionParameters = new JsonSchema();
        conditionParameters.setType("string");
        conditionParameters.setTitle("SQL条件的值的JSON文本格式");
        conditionParameters.setDescription("查询SQL时输入的条件参数，使用JSON文本格式 如 {\"value1\":\"value\"}");
        properties.put("conditionParametersJsonString", conditionParameters);
        inputSchema.setProperties(properties);
        response.setInputSchema(inputSchema);
        return response;
    }
}
