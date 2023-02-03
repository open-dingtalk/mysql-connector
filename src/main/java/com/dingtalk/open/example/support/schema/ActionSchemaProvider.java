package com.dingtalk.open.example.support.schema;

import com.dingtalk.open.example.model.response.GetIntegrationObjectSchemaResponse;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;

/**
 * 说明：
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
public interface ActionSchemaProvider {
    /**
     * 获取Schema信息
     *
     * @param corpId   组织ID
     * @param database 数据库
     * @param table    表
     * @return 对应的出入参
     */
    GetIntegrationObjectSchemaResponse getSchema(String corpId, String database, String table) throws SpiRuntimeException;
}
