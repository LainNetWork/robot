package fun.lain.robot.config;

import fun.lain.robot.config.properties.RobotProperties;
import fun.lain.robot.handler.*;
import fun.lain.robot.listener.MsgDispatcherListener;
import fun.lain.robot.service.HandlerService;
import fun.lain.robot.service.RobotService;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:44
 */
@Configuration
public class RobotConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Bean
    public RobotService robotService(RobotProperties robotProperties){
        RobotService robotService = new RobotService();
        robotService.setMsgDispatcherListener(msgDispatcherListener());
        robotService.setRobotProperties(robotProperties);
        robotService.init();
        return robotService;
    }


    private MsgDispatcherListener msgDispatcherListener(){
        MsgDispatcherListener msgDispatcherListener = new MsgDispatcherListener();
        HandlerService handlerService = new HandlerService();
        Map<String, MessageHandler> beansOfType = applicationContext.getBeansOfType(MessageHandler.class);
        beansOfType.values().forEach(handlerService::add);
        msgDispatcherListener.setHandlerService(handlerService);
        return msgDispatcherListener;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
