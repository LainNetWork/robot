package fun.lain.robot.config.properties;

import fun.lain.robot.config.properties.vo.BaiduVO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/23 20:38
 */
@ConfigurationProperties(prefix = "translate")
@Component
@Data
public class TranslateProperties {
    private BaiduVO baidu;
}
