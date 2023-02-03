package com.dingtalk.open.example.support.data;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 说明：适配标识
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface JsonToSql {
    /**
     * 适配的JSON值类型
     * @return JsonSchema Type
     */
    String jsonType();

    /**
     * 适配的SQL字段类型
     * @return mysql data type
     */
    String sqlType();
}
