package fun.lain.robot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/19 1:47
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogProperties {
    private String uploadUrl;
    private String loginUrl;
    private String username;
    private String password;
}
