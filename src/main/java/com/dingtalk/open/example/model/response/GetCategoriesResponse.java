package com.dingtalk.open.example.model.response;

import com.dingtalk.open.example.model.Category;
import lombok.Data;

import java.util.List;

/**
 * 说明：
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@Data
public class GetCategoriesResponse {
    Integer level;
    List<Category> records;
}
