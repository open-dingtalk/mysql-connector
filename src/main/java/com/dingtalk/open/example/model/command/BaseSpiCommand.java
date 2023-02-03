package com.dingtalk.open.example.model.command;

import com.dingtalk.open.example.model.SpiResult;
import lombok.Data;

import java.util.Map;

/**
 * 说明：SPI指令
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@Data
public abstract class BaseSpiCommand<T> {
    /**
     * 指令名
     */
    private String command;
    /**
     * 配置属性
     */
    private Map<String, String> props;
    /**
     * 组织
     */
    private String corpId;

    /**
     * 执行命令
     *
     * @return 执行结果
     */
    public abstract SpiResult<T> execute();
}
