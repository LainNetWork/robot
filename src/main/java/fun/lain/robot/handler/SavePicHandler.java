package fun.lain.robot.handler;

import com.alibaba.fastjson.JSONObject;
import fun.lain.robot.cache.AuthCache;
import fun.lain.robot.cache.ImageCache;
import fun.lain.robot.config.properties.BlogProperties;
import fun.lain.robot.config.properties.RobotProperties;
import fun.lain.robot.constants.TokenEnum;
import fun.lain.robot.utils.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.rmi.UnexpectedException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 1:47
 */
@Slf4j
@Component
@AllArgsConstructor
public class SavePicHandler implements MessageHandler {
    private final BlogProperties blogProperties;
    private final RobotProperties robotProperties;
    private final RestTemplate restTemplate;
    private final AuthCache authCache;
    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "SavePic";
    }

    @Override
    public String describe() {
        return "保存图片到云端";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        if(contact.getSender().getId() != robotProperties.getMasterAccount()){
            contact.getSubject().sendMessage("私密马神，只有主人才能调用我哦~");
            return;
        }
        contact.getMessage().stream().filter(e->e instanceof QuoteReply).forEach(e->{
            Contact subject = contact.getSubject();
            QuoteReply quoteReply = (QuoteReply)e;
            //从缓存中取数据
            ImageCache imageCache = BeanUtils.getBean(ImageCache.class);
            String key = contact.getSubject().getId()+"_" + List.of(contact.getSource().getInternalIds()).stream().map(String::valueOf).collect(Collectors.joining(","));
            List<String> imageIds = imageCache.get(subject.getId() + "_" +key);
            log.info("获取缓存 key: {}", subject.getId() + "_" + quoteReply.getSource().getTime());
            if(CollectionUtils.isEmpty(imageIds)){
                subject.sendMessage("私密马神，没有缓存到该消息中的图片哦");
                return;
            }
            for (String imageId : imageIds) {
                Image offlineImage = Image.fromId(imageId);
                if(contact instanceof GroupMessageEvent){
                    String s = Image.queryUrl(offlineImage);
                    log.info("qq url:{}",s);
                    ResponseEntity<byte[]> image = restTemplate.getForEntity(s, byte[].class);
                    if(image.getBody() == null){
                        subject.sendMessage("下载图片失败惹！");
                        return;
                    }
                    //请求博客token
                    String blogToken;
                    try {
                        blogToken = authCache.getBlogToken(TokenEnum.BLOG.name());
                    } catch (UnexpectedException ex) {
                        subject.sendMessage("登录博客出错了KIRA⭐~ 获取Token失效了哦！");
                        return;
                    }
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("admin-authorization",blogToken);
                    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                    MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
                    body.add("file",new ByteArrayResource(image.getBody()){
                        @Override
                        public String getFilename() {
                            MediaType contentType = image.getHeaders().getContentType();
                            if(contentType != null){
                                return String.format("%s_%s.%s","image",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), contentType.getSubtype());
                            }
                            return "image_save_from_qq.jpg";
                        }
                    });
                    HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(body,httpHeaders);
                    ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(blogProperties.getUploadUrl(), HttpMethod.POST, entity, JSONObject.class);
                    if(responseEntity.getBody() != null){
                        JSONObject responseEntityBody = responseEntity.getBody();
                        if(responseEntityBody.containsKey("status") && responseEntityBody.getInteger("status") == 200){
                            subject.sendMessage("保存成功！地址为："+ responseEntityBody.getJSONObject("data").getString("path"));
                        }else{
                            subject.sendMessage("上传失败！");
                        }
                    }else {
                        subject.sendMessage("上传失败！");
                    }
                }

            }
            OfflineMessageSource source = (OfflineMessageSource) quoteReply.getSource();
            source.getOriginalMessage().stream().filter(e1 ->e1 instanceof Image).forEach(image->{
                subject.sendMessage(Image.queryUrl((Image)image));
            });
        });
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return msg.getMessage().stream().filter(e->e instanceof At).anyMatch(e->((At) e).getTarget() == msg.getBot().getId())
                && msg.getMessage().stream().filter(e->e instanceof PlainText).anyMatch(e->((PlainText) e).getContent().trim().equals("save"));
    }
}
