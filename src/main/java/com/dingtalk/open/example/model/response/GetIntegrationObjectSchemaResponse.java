package com.dingtalk.open.example.model.response;

import com.dingtalk.open.example.model.JsonSchema;
import lombok.Data;

/**
 * 说明：集成元素执行动作、触发器对应的描述信息
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@Data
public class GetIntegrationObjectSchemaResponse {
    /**
     * 表的标识
     */
    private String integrationObject;
    /**
     * 表的名称
     */
    private String name;
    /**
     * 写入或更新的入参信息
     */
    private JsonSchema inputSchema;
    /**
     * 出参信息
     */
    private JsonSchema outputSchema;
}
