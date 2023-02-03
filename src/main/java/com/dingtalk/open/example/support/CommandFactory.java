package com.dingtalk.open.example.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.open.example.model.command.BaseSpiCommand;
import com.dingtalk.open.example.model.command.SpiCommand;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 说明：指令工场
 *
 * @author donghuai.zjj
 * @date 2022/12/05
 */
@Component
public class CommandFactory implements InitializingBean {
    /**
     * 指令名与指令实体类的对应MAP配置
     */
    private Map<String, Class<? extends BaseSpiCommand<?>>> commandMap = Collections.emptyMap();
    /**
     * 注入Spring Bean到指令中的处理器
     */
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public CommandFactory(@Autowired AutowireCapableBeanFactory autowireCapableBeanFactory) {
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
    }

    /**
     * 将接口入参转换为实际要执行的SPI指令
     *
     * @param requestBody 入参
     * @return 可执行SPI指令
     */
    public BaseSpiCommand<?> buildCommand(JSONObject requestBody) {
        String commandName = requestBody.getString("command");
        // 获取指令实例
        Class<? extends BaseSpiCommand<?>> commandType = Optional.ofNullable(commandMap.get(commandName))
                .orElseThrow(() -> new IllegalArgumentException("unknown requestBody[" + commandName + "], onlySupport" + JSON.toJSONString(commandMap.keySet())));
        // JSON转换为指令数据
        BaseSpiCommand<?> spiCommand = requestBody.toJavaObject(commandType);
        // 注入依赖
        autowireCapableBeanFactory.autowireBean(spiCommand);
        return spiCommand;
    }

    @Override
    public void afterPropertiesSet() {
        // 包扫描加载各类SPI指令实现
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("com.dingtalk.open.example")
                .addScanners(Scanners.SubTypes));
        //noinspection rawtypes
        Set<Class<? extends BaseSpiCommand>> commandClasses = reflections.getSubTypesOf(BaseSpiCommand.class);
        Map<String, Class<? extends BaseSpiCommand<?>>> commandMap = new HashMap<>(commandClasses.size());
        //noinspection rawtypes
        for (Class<? extends BaseSpiCommand> commandClass : commandClasses) {
            // 获取指名名对应的指令类，并注册路由关系MAP
            String commandName = Optional.ofNullable(commandClass.getAnnotation(SpiCommand.class))
                    .map(SpiCommand::value)
                    .orElseGet(commandClass::getSimpleName);
            if (commandMap.containsKey(commandName) && !commandMap.get(commandName).equals(commandClass)) {
                throw new BeanInitializationException(String.format("duplicated command registered, commandName=%s, commandClasses=%s",
                        commandName, JSON.toJSONString(Arrays.asList(
                                commandMap.get(commandName).getName(),
                                commandClass.getName()
                        ))));
            }
            //noinspection unchecked,CastCanBeRemovedNarrowingVariableType
            commandMap.put(commandName, (Class<? extends BaseSpiCommand<?>>) commandClass);
        }
        this.commandMap = commandMap;
    }
}
