package fun.lain.robot.cache;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 3:13
 */
@Component
public class ImageCache {

    @CachePut(cacheNames = "imageIdCache",key = "#p0")
    public List<String> put(String key, List<String> ids){
        return ids;
    }


    @Cacheable(cacheNames = "imageIdCache",key = "#p0")
    public List<String> get(String key){
        return null;
    }
}
