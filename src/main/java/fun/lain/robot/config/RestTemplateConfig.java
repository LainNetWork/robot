package fun.lain.robot.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/18 20:02
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0,buildFastJsonHttpMessageConverter());

//        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
//        simpleClientHttpRequestFactory.setProxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",8888)));
//        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
        return restTemplate;
    }
    private static FastJsonHttpMessageConverter buildFastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> supportType = new ArrayList<>();
        supportType.add(MediaType.APPLICATION_JSON);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportType);
        return fastJsonHttpMessageConverter;
    }

}
