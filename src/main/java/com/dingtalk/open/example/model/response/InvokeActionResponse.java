package com.dingtalk.open.example.model.response;

import lombok.Data;

/**
 * 说明：
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@Data
public class InvokeActionResponse {
    String integrationObject;
    Object data;
}
