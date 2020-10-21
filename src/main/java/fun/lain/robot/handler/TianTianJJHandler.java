package fun.lain.robot.handler;

import fun.lain.robot.cache.ImageCache;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import org.checkerframework.checker.units.qual.Prefix;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/21 21:43
 */
@Component
@AllArgsConstructor
public class TianTianJJHandler implements MessageHandler {
    private static final String PREDICT_REALTIME = "http://j4.dfcfw.com/charts/pic6/%s.png";
    private static final String PREFIX = "tt ";

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
        if(command.startsWith("predict ")){
            String code = command.substring("predict ".length()).trim();
            String url = String.format(PREDICT_REALTIME,code);
            Image image = subject.uploadImage(new URL(url));
            MessageReceipt<Contact> contactMessageReceipt = subject.sendMessage(image);
            int id = contactMessageReceipt.getSource().getId();
            imageCache.put(subject.getId() + "_" + id, List.of(image.getImageId()));
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return getFirstPlainTextMsg(msg).startsWith("tt ");
    }
}
