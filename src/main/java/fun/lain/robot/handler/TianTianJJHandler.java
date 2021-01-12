package fun.lain.robot.handler;

import fun.lain.robot.cache.ImageCache;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/21 21:43
 */
@Component
@AllArgsConstructor
public class TianTianJJHandler implements MessageHandler {
    private static final String PREDICT_REALTIME = "http://j4.dfcfw.com/charts/pic6/%s.png";
    private static final String PREFIX = "kkp ";
    private final RestTemplate restTemplate;

    private final ImageCache imageCache;

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "TianTianJJ";
    }

    @Override
    public String describe() {
        return "天天基金网插件";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        Contact subject = contact.getSubject();
        String firstMsg = getFirstPlainTextMsg(contact);
        String command = firstMsg.substring(PREFIX.length());
        if(NumberUtils.isNumber(command)){
            String url = String.format(PREDICT_REALTIME, command);
            ResponseEntity<byte[]> image = restTemplate.getForEntity(url, byte[].class);
            if(image.getBody() == null){
                subject.sendMessage("下载图片失败惹！");
                return;
            }
            Image uploadImage = subject.uploadImage(ExternalResource.create(image.getBody()));
            MessageReceipt messageReceipt = subject.sendMessage(uploadImage);
            String key = contact.getSubject().getId()+"_" + List.of(contact.getSource().getInternalIds()).stream().map(String::valueOf).collect(Collectors.joining(","));
            imageCache.put(subject.getId() + "_" + key, List.of(uploadImage.getImageId()));
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return getFirstPlainTextMsg(msg).startsWith(PREFIX);
    }
}
