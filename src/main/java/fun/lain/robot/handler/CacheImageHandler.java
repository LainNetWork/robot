package fun.lain.robot.handler;

import fun.lain.robot.cache.ImageCache;
import fun.lain.robot.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 3:32
 */
@Slf4j
@Component
public class CacheImageHandler implements MessageHandler{
    @Override
    public int order() {
        return -999;
    }

    @Override
    public String name() {
        return "CacheImage";
    }

    @Override
    public String describe() {
        return "缓存图片";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        List<String> ids = contact.getMessage().stream()
                .filter(e -> e instanceof Image).map(e -> ((Image) e).getImageId()).collect(Collectors.toList());
        String key = contact.getSubject().getId()+"_" +contact.getSource().getId();
        log.debug("缓存： key:{} ,value:{}",key,ids);
        ImageCache imageCache = BeanUtils.getBean(ImageCache.class);
        imageCache.put(key,ids);
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return true;
    }
}
