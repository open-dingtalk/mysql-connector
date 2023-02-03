package com.dingtalk.open.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 说明：钉钉连接器要求的SPI结果格式
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@SuppressWarnings("unused")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpiResult<T> {
    /**
     * 当前SPI执行是否成功
     */
    boolean success;
    /**
     * 执行失败的错误标识，需要按照约定返回
     */
    String errCode;
    /**
     * 执行失败的错误信息，业务错误信息可以通过该字段返回提示用户
     */
    String errorMsg;
    /**
     * SPI的执行结果内容
     */
    T result;

    /**
     * 成功结果
     *
     * @param result 结果内容
     * @param <T>    结果内容类型
     * @return SPI返回
     */
    public static <T> SpiResult<T> success(T result) {
        SpiResult<T> spiResult = new SpiResult<>();
        spiResult.setResult(result);
        spiResult.setSuccess(true);
        return spiResult;
    }

    /**
     * 失败结果
     *
     * @param <T> 结果内容类型
     * @return 失败返回
     */
    public static <T> SpiResult<T> fail() {
        return fail("business_error", "系统异常");
    }


    /**
     * 失败结果
     *
     * @param errCode  错误码
     * @param errorMsg 错误信息
     * @param <T>      结果内容类型
     * @return 失败返回
     */
    public static <T> SpiResult<T> fail(String errCode, String errorMsg) {
        SpiResult<T> spiResult = new SpiResult<>();
        spiResult.setSuccess(false);
        spiResult.setErrCode(errCode);
        spiResult.setErrorMsg(errorMsg);
        return spiResult;
    }

    /**
     * 业务失败结果
     * @param errorMsg 错误信息
     * @return 失败返回
     * @param <T> 结果内容类型
     */
    public static <T> SpiResult<T> fail(String errorMsg) {
        return fail("business_error", errorMsg);
    }
}
