package fun.lain.robot.cache;

import com.alibaba.fastjson.JSONObject;
import fun.lain.robot.config.properties.BlogProperties;
import fun.lain.robot.config.properties.XinJieProperties;
import fun.lain.robot.constants.ApiConstants;
import fun.lain.robot.utils.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储一些三方服务的验证字段的缓存
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/20 0:52
 */
@Component
@AllArgsConstructor
@Slf4j
public class AuthCache {
    private final XinJieProperties xinJieProperties;
    private final BlogProperties blogProperties;

//    @Cacheable(value = "authCache",key = "#p0")
    public Map<String,String> getXinjieCookies(String key) throws IOException {
        Map<String,String> param = new HashMap<>();
        param.put("email",xinJieProperties.getEmail());
        param.put("passwd",xinJieProperties.getPassword());
        Connection.Response execute = Jsoup.connect(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_LOGIN).data(param).method(Connection.Method.POST).execute();
        log.info("cookies:{}",execute.cookies());
        return execute.cookies();

    }
    @Cacheable(value = "authCache",key = "#p0")
    public String getBlogToken(String key) throws UnexpectedException {
        RestTemplate restTemplate = BeanUtils.getBean(RestTemplate.class);
        JSONObject reqBody = new JSONObject();
        reqBody.put("username",blogProperties.getUsername());
        reqBody.put("password",blogProperties.getPassword());
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(blogProperties.getLoginUrl(), reqBody, JSONObject.class);
        if(responseEntity.getBody() !=null &&
                responseEntity.getBody().containsKey("status") &&
                responseEntity.getBody().getInteger("status") == 200){
            return responseEntity.getBody().getJSONObject("data").getString("access_token");
        }
        throw new UnexpectedException("获取博客Token异常！");
    }
}
