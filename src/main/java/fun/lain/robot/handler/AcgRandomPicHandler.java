package fun.lain.robot.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import fun.lain.robot.cache.ImageCache;
import fun.lain.robot.config.properties.AcgProperties;
import fun.lain.robot.utils.BeanUtils;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.*;

/**
 * 色图API由 https://api.lolicon.app/#/setu 提供
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 0:46
 */
@Component
@AllArgsConstructor
public class AcgRandomPicHandler implements MessageHandler {
    private AcgProperties acgProperties;
    private RestTemplate restTemplate;

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "AcgRandomPic";
    }

    @Override
    public String describe() {
        return "随机图片";
    }

    @Override
    public void handleMsg(MessageEvent event) throws Exception {

        String msg = getFirstPlainTextMsg(event);
        Contact contact = event.getSubject();
        String[] split = msg.split(" ");
        String keyword = "";
        if(split.length == 2){
            keyword = split[1];
        }
        Map<String,String> param = new HashMap<>();
        param.put("apikey",acgProperties.getImageApiKey());
        param.put("keyword",keyword);
        param.put("r18","2");
        param.put("size1200","true");
        ResponseEntity<JSONObject> forObject = null;
        try {
            forObject = restTemplate.getForEntity(acgProperties.getImageApiUrl(),JSONObject.class,param);
        }catch (Exception e){
           contact.sendMessage("请求st异常:"+ e.getMessage());
        }
        JSONObject body = forObject.getBody();
        if (body.containsKey("code") && body.getInteger("code") == 0) {
            JSONObject data = body.getJSONArray("data").getJSONObject(0);
            String url = data.getString("url");
            String title = data.getString("title");
            boolean isR18 = data.getBoolean("r18");
            Image image = contact.uploadImage(new URL(url));
            MessageChain text = new PlainText(title).plus(image);
            if(isR18){
                text = text.plus("由于包含不太纯洁的内容，8秒后自动撤回");
            }
            MessageReceipt<Contact> contactMessageReceipt = contact.sendMessage(text);
            String key = event.getSubject().getId() + "_" + contactMessageReceipt.getSource().getId();
            ImageCache imageCache = BeanUtils.getBean(ImageCache.class);
            imageCache.put(key,List.of(image.getImageId()));
            if(isR18){
                contactMessageReceipt.recallIn(8000);
            }
        }else{
            contact.sendMessage("请求st异常:"+ body.getString("msg"));
        }
    }

    @Override
    public boolean isMatch(MessageEvent messageEvent) {
        Optional<SingleMessage> firstText = messageEvent.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
        if(firstText.isEmpty()){
            return false;
        }
        SingleMessage singleMessage = firstText.get();
        String msg = singleMessage.contentToString();
        return !StringUtils.isEmpty(msg) && msg.startsWith("st ");
    }
}
