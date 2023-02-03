package com.dingtalk.open.example.support.command;

import com.dingtalk.open.example.model.Category;
import com.dingtalk.open.example.model.SpiResult;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.model.command.SpiCommand;
import com.dingtalk.open.example.model.response.GetCategoriesResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：这个SPI的实现，用于获取类目的级联查询列表，MYSQL数据表作为集成对象时，对表的分类类目即是MYSQL的数据库
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@SuppressWarnings("unused")
@SpiCommand("getCategories")
@EqualsAndHashCode(callSuper = true)
public class GetCategories extends BaseSpiCommand<GetCategoriesResponse> {
    public static final int SELECT_DATABASE_LIST = 0;
    /**
     * 当前级联查询的值
     */
    @Getter
    @Setter
    private List<Category> categories;
    /**
     * 关键字
     */
    @Getter
    @Setter
    private String keywords;
    /**
     * 当前级联的级别
     */
    @Getter
    @Setter
    private Integer currentLevel;
    /**
     * 集成的类型
     */
    @Getter
    @Setter
    private String dingtalkIntegrationType;
    /**
     * 集成钉钉的连接标识
     */
    @Getter
    @Setter
    private String dingtalkIntegrationId;
    /**
     * 操作人
     */
    @Getter
    @Setter
    private String userId;


    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public SpiResult<GetCategoriesResponse> execute() {
        int level = CollectionUtils.size(categories);
        if (level == SELECT_DATABASE_LIST) {
            // 第一级是查数据库列表
            return selectDatabaseList();
        }
        SpiResult<GetCategoriesResponse> result = new SpiResult<>();
        result.setSuccess(false);
        return result;
    }

    private SpiResult<GetCategoriesResponse> selectDatabaseList() {
        List<String> databaseNames = namedParameterJdbcTemplate.queryForList(
                "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA",
                Collections.emptyMap(), String.class);
        List<Category> records = databaseNames.stream().map(databaseName -> {
            Category category = new Category();
            category.setName(databaseName);
            category.setValue(databaseName);
            return category;
        }).collect(Collectors.toList());
        GetCategoriesResponse response = new GetCategoriesResponse();
        response.setRecords(records);
        response.setLevel(CollectionUtils.size(categories) + 1);
        return SpiResult.success(response);
    }
}
