package com.dingtalk.open.example.support.data;

import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 说明：时间戳格式转换
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@JsonToSql(
        jsonType = "number",
        sqlType = "timestamp"
)
public class TimestampValueAdaptor implements JsonToSqlValueAdaptor {
    @Override
    public Object toSqlValue(Object jsonValue) {
        long timestamp = NumberUtils.toLong(String.valueOf(jsonValue));
        return new Timestamp(timestamp);
    }
    @Override
    public Object toJsonValue(Object sqlValue) {
        if (sqlValue instanceof java.util.Date) {
            return ((Date) sqlValue).getTime();
        }
        if (sqlValue instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) sqlValue).getTime();
        }
        return sqlValue;
    }
}
