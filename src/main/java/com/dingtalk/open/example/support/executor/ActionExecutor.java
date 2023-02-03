package com.dingtalk.open.example.support.executor;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.support.exception.SpiRuntimeException;

/**
 * 说明：Action执行
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
public interface ActionExecutor {
    /**
     * 执行Action操作
     *
     * @param corpId    组织
     * @param database  数据库
     * @param tableName 表
     * @param input     输入JSON
     * @return 出参
     * @throws SpiRuntimeException 接口运行异常
     */
    Object execute(String corpId,
              String database,
              String tableName,
              JSONObject input) throws SpiRuntimeException;
}
