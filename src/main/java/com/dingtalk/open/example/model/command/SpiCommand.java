package com.dingtalk.open.example.model.command;

import java.lang.annotation.*;

/**
 * 说明：spi指令配置
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiCommand {
    String value();
}
