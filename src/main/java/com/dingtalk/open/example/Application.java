package com.dingtalk.open.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 将Mysql数据表单作为集成元素的接入样例应用
 *
 * @author donghuai.zjj
 * @date 2022/12/07
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.dingtalk.open.example"
        }
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}