package com.dingtalk.open.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.SpiResult;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.support.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明：接收指令
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@RestController
@RequestMapping("/connect/v1/spi")
public class ConnectSpiController {

    private final CommandFactory commandFactory;


    @Autowired
    public ConnectSpiController(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * SPI指令接收接口
     *
     * @param requestBody 指令的请求体
     * @return 指令的执行结果
     */
    @PostMapping
    public SpiResult<?> onCommand(@RequestBody JSONObject requestBody) {
        BaseSpiCommand<?> spiCommand = commandFactory.buildCommand(requestBody);
        return spiCommand.execute();
    }

}
