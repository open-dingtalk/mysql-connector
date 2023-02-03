package com.dingtalk.open.example.support.data;

import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * 说明：将数字时间戳转换为SQL日期
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@JsonToSql(
        jsonType = "number",
        sqlType = "date"
)
public class DateValueAdaptor implements JsonToSqlValueAdaptor{
    @Override
    public Object toSqlValue(Object jsonValue) {
        long timestamp = NumberUtils.toLong(String.valueOf(jsonValue));
        return new Date(timestamp);
    }
    @Override
    public Object toJsonValue(Object sqlValue) {
        if (sqlValue instanceof java.util.Date) {
            return ((java.util.Date) sqlValue).getTime();
        }
        if (sqlValue instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) sqlValue).getTime();
        }
        return sqlValue;
    }
}
