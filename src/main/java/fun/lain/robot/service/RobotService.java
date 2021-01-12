package fun.lain.robot.service;

import fun.lain.robot.config.properties.RobotProperties;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:34
 */
@Getter
@Setter
public class RobotService {
    private HandlerService handlerService;
    private RobotProperties robotProperties;
    private Bot bot;

    public void login(){
        bot.login();
    }

    public boolean isOnline(){
        return bot.isOnline();
    }

    public void init(){
        Bot bot = BotFactory.INSTANCE.newBot(robotProperties.getAccount(), robotProperties.getPassword(), new BotConfiguration() {
            {
                //保存设备信息到文件
                fileBasedDeviceInfo("device.json");
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                // setLoginSolver();
                // setBotLoggerSupplier();
            }
        });
        bot.getEventChannel().subscribeAlways(MessageEvent.class, e->{
            handlerService.dispatchMsg(e);
        });
        this.bot = bot;
    }
}
