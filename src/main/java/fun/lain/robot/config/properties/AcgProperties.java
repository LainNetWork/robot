package fun.lain.robot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/19 2:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "acg")
public class AcgProperties {
    //图库API地址
    private String imageApiUrl;
    private String imageApiKey;
}
