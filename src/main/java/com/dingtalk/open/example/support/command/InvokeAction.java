package com.dingtalk.open.example.support.command;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.SpiResult;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.model.command.SpiCommand;
import com.dingtalk.open.example.model.response.InvokeActionResponse;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;
import com.dingtalk.open.example.support.executor.ActionExecutor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;

/**
 * 说明：调用Action的SPI实现
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@SuppressWarnings("unused")
@SpiCommand("invokeAction")
public class InvokeAction extends BaseSpiCommand<InvokeActionResponse> {
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

    @Setter
    @Getter
    private Map<String, Object> input;

    @Resource
    private Map<String, ActionExecutor> beanNameActionExecutorMap;
    @Override
    public SpiResult<InvokeActionResponse> execute() {
        // 这里根据在开放平台连接器配置中的额外属性，来定义不同属性的执行动作/触发器的数据结构
        Map<String, String> props = getProps();
        URI uri = URI.create(integrationObject);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        UriComponents uriComponents = uriComponentsBuilder.build();
        String databaseName = uriComponents.getPathSegments().stream().findFirst().orElse(null);
        String tableName = uriComponents.getQueryParams().getFirst("table");
        // 当集请求Schema的操作类型为执行动作时
        if ("action".equals(dingtalkIntegrationType)) {
            String action = MapUtils.getString(props, "action");
            String beanName = StringUtils.join(action, "ActionExecutor");
            ActionExecutor actionExecutor = beanNameActionExecutorMap.get(beanName);
            if (actionExecutor == null) {
                return SpiResult.fail("不支持的操作类型");
            }
            try {
                Object result = actionExecutor.execute(getCorpId(), databaseName, tableName, new JSONObject(input));
                InvokeActionResponse response = new InvokeActionResponse();
                response.setData(result);
                response.setIntegrationObject(uri.toString());
                return SpiResult.success(response);
            } catch (SpiRuntimeException e) {
                return SpiResult.fail(e.getErrorCode(), e.getErrorMsg());
            }
        }
        return SpiResult.fail("不支持的操作类型");
    }
}
