package fun.lain.robot.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import fun.lain.robot.config.properties.TranslateProperties;
import fun.lain.robot.constants.ApiConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lain <tianshang360@163.com>
 * @date 2020/10/23 16:23
 */
@Service
@AllArgsConstructor
public class TranslateService {
    private final TranslateProperties translateProperties;
    private final RestTemplate restTemplate;


    public String baiduTranslate(String source,String origin,String target){
        String salt = UUID.randomUUID().toString();
        String token = encryptBaiduToken(source, salt);
        Map<String,String> param = new HashMap<>();
        param.put("q",source);
        param.put("from",origin);
        param.put("to",target);
        param.put("appid",translateProperties.getBaidu().getAppID());
        param.put("salt",salt);
        param.put("sign",token);
        String result = restTemplate.getForObject(ApiConstants.BAIDU_TRANSLATE, String.class, param);
        Object read = JSONPath.read(result, "$.trans_result[0].dst");
        return read == null ? "" : read.toString();
    }





    private String encryptBaiduToken(String query ,String salt){
        String key = translateProperties.getBaidu().getAppID() + new String(query.getBytes(), StandardCharsets.UTF_8) + salt + translateProperties.getBaidu().getSecretKey() ;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
