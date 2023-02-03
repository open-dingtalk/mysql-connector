package com.dingtalk.open.example.support.exception;

import lombok.Getter;

/**
 * 说明：Spi实现调用运行时的异常
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
public class SpiRuntimeException extends Exception {
    /**
     * 异常错误码
     */
    @Getter
    private final String errorCode;
    /**
     * 异常提示信息
     */
    @Getter
    private final String errorMsg;

    public SpiRuntimeException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
