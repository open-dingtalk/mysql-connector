package com.dingtalk.open.example.support.command;

import com.dingtalk.open.example.model.Category;
import com.dingtalk.open.example.model.SpiResult;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.model.command.SpiCommand;
import com.dingtalk.open.example.model.response.GetIntegrationObjectsResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：这个SPI的实现，表示，当类目选择完成，可以根据类目的选择情况，获取集成元素（MYSQL数据表）的选择列表
 *
 * @author donghuai.zjj
 * @date 2022/12/06
 */
@SuppressWarnings("unused")
@SpiCommand("getIntegrationObjects")
public class GetIntegrationObjects extends BaseSpiCommand<GetIntegrationObjectsResponse> {
    public static final int SELECT_TABLE_LIST_IN_DATABASE = 1;
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
    public SpiResult<GetIntegrationObjectsResponse> execute() {
        int level = CollectionUtils.size(categories);
        if (level == SELECT_TABLE_LIST_IN_DATABASE) {// 第二级是查询表列表
            return selectTableList();
        }
        SpiResult<GetIntegrationObjectsResponse> result = new SpiResult<>();
        result.setSuccess(false);
        return result;
    }

    private SpiResult<GetIntegrationObjectsResponse> selectTableList() {
        String databaseName = ListUtils.emptyIfNull(categories).stream().findFirst()
                .map(Category::getValue)
                .orElse(null);
        List<String> tableNames = namedParameterJdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = :database",
                Collections.singletonMap("database", databaseName), String.class);
        List<Category> records = tableNames.stream().map(tableName -> {
            Category category = new Category();
            category.setName(tableName);
            category.setValue("mysql://" + getCorpId() + "/" + databaseName + "?table=" + tableName);
            return category;
        }).collect(Collectors.toList());
        GetIntegrationObjectsResponse response = new GetIntegrationObjectsResponse();
        response.setRecords(records);
        response.setLevel(CollectionUtils.size(categories) + 1);
        return SpiResult.success(response);
    }

}
