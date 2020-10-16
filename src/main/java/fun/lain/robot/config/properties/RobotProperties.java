package fun.lain.robot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:36
 */
@ConfigurationProperties(prefix = "bot")
@Component
@Data
public class RobotProperties{
    private Long account;
    private String password;
    private long masterAccount;
}
