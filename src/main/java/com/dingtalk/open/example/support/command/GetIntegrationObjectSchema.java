package com.dingtalk.open.example.support.command;

import com.dingtalk.open.example.model.SpiResult;
import com.dingtalk.open.example.model.Table;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.model.command.SpiCommand;
import com.dingtalk.open.example.model.response.GetIntegrationObjectSchemaResponse;
import com.dingtalk.open.example.support.MysqlDatabaseHelper;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import com.dingtalk.open.example.support.schema.ActionSchemaProvider;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * 说明：这个SPI的实现，表示，当类目选择完成，根据集成元素列表确定集成元素后，返回集成元素对应的操作类型的出入参信息
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@SuppressWarnings("unused")
@SpiCommand("getIntegrationObjectSchema")
public class GetIntegrationObjectSchema extends BaseSpiCommand<GetIntegrationObjectSchemaResponse> {
    public static final String PROP_VALUE_ACTION = "action";
    public static final String PROP_VALUE_TRIGGER = "trigger";
    /**
     * 当前请求Schema的连接器操作类型（触发器/执行动作）
     */
    @Setter
    @Getter
    private String dingtalkIntegrationType;
    /**
     * 将MYSQL中的数据表作为集成元素时，表示MYSQL表的唯一标识, 格式为 mysql://{corpId}/{database}?table={tableName}
     */
    @Setter
    @Getter
    private String integrationObject;

    @Resource
    private Map<String, ActionSchemaProvider> beanNameActionSchemaProviderMap;

    @Resource
    private MysqlDatabaseHelper mysqlDatabaseHelper;

    @Override
    public SpiResult<GetIntegrationObjectSchemaResponse> execute() {
        // 这里根据在开放平台连接器配置中的额外属性，来定义不同属性的执行动作/触发器的数据结构
        Map<String, String> props = getProps();

        URI uri = URI.create(integrationObject);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        UriComponents uriComponents = uriComponentsBuilder.build();
        String databaseName = uriComponents.getPathSegments().stream().findFirst().orElse(null);
        String tableName = uriComponents.getQueryParams().getFirst("table");
        // 当集请求Schema的操作类型为执行动作时
        if (PROP_VALUE_ACTION.equals(dingtalkIntegrationType)) {
            String action = MapUtils.getString(props, PROP_VALUE_ACTION);
            String beanName = StringUtils.join(action, "SchemaProvider");
            ActionSchemaProvider actionSchemaProvider = beanNameActionSchemaProviderMap.get(beanName);
            if (actionSchemaProvider == null) {
                return SpiResult.fail("不支持的操作类型");
            }
            try {
                return SpiResult.success(actionSchemaProvider.getSchema(getCorpId(), databaseName, tableName));
            } catch (SpiRuntimeException e) {
                return SpiResult.fail(e.getErrorCode(), e.getErrorMsg());
            }
        } else if (PROP_VALUE_TRIGGER.equals(dingtalkIntegrationType)) {
            // 无论增删改查，都是使用一个事件出参模型，即数据完整实例
            try {
                return getTriggerSchema(getCorpId(), databaseName, tableName);
            } catch (SpiRuntimeException e) {
                return SpiResult.fail(e.getErrorCode(), e.getErrorMsg());
            }
        }
        return SpiResult.fail("不支持的操作类型");
    }

    private SpiResult<GetIntegrationObjectSchemaResponse> getTriggerSchema(String corpId, String databaseName, String tableName) throws SpiRuntimeException {
        Table tableInfo = Optional.ofNullable(mysqlDatabaseHelper.getTableInfo(databaseName, tableName))
                .orElseThrow(() -> new SpiRuntimeException("integration_object_not_exists", "数据表不存在"));
        GetIntegrationObjectSchemaResponse schemaResponse = new GetIntegrationObjectSchemaResponse();
        schemaResponse.setIntegrationObject("mysql://" + corpId + "/" + databaseName + "?table=" + tableName);
        schemaResponse.setOutputSchema(tableInfo.toJsonSchema());
        schemaResponse.setName(tableName);
        return SpiResult.success(schemaResponse);
    }

}
