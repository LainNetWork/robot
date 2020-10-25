package fun.lain.robot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/20 0:47
 */
@Data
@Component
@ConfigurationProperties("xinjie")
public class XinJieProperties {
    private String email;
    private String password;
    private String baseURL;
    private List<Long> serviceGroup;
    private List<Long> masterId;
    private List<Long> userId;
}
