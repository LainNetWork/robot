package fun.lain.robot.service;

import fun.lain.robot.config.properties.RobotProperties;
import fun.lain.robot.listener.MsgDispatcherListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:34
 */
@Getter
@Setter
public class RobotService {
    private MsgDispatcherListener msgDispatcherListener;
    private RobotProperties robotProperties;
    private Bot bot;

    public void login(){
        bot.login();
    }

    public boolean isOnline(){
        return bot.isOnline();
    }

    public void init(){
        Bot bot = BotFactoryJvm.newBot(robotProperties.getAccount(), robotProperties.getPassword(), new BotConfiguration() {
            {
                //保存设备信息到文件
                fileBasedDeviceInfo("deviceInfo.json");
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                // setLoginSolver();
                // setBotLoggerSupplier();
            }
        });
        Events.registerEvents(bot, msgDispatcherListener);
        this.bot = bot;
    }
}
