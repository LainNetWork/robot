package fun.lain.robot.handler;

import fun.lain.robot.service.TranslateService;
import fun.lain.robot.utils.EmojiUtils;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lain <tianshang360@163.com>
 * @date 2020/10/23 12:12
 */
@Component
@AllArgsConstructor
public class HitokotoHandler implements MessageHandler{
    private final RestTemplate restTemplate;
    private final TranslateService translateService;
    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "HitokotoHandler";
    }

    @Override
    public String describe() {
        return "一言";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        Contact subject = contact.getSubject();
        Optional<SingleMessage> first = contact.getMessage().stream().filter(e -> e instanceof QuoteReply).findFirst();
        if(first.isPresent()){
            QuoteReply quoteReply = (QuoteReply)first.get();
            String hitokoto = quoteReply.getSource().getOriginalMessage().contentToString();
            String jp = translateService.baiduTranslate(hitokoto, "auto", "jp");
            long fromId = quoteReply.getSource().getFromId();
            if(subject instanceof Group){
                Group group = (Group) subject;
                Member member = group.get(fromId);
                String avatarUrl = member.getAvatarUrl();
                ResponseEntity<byte[]> image = restTemplate.getForEntity(avatarUrl, byte[].class);
                if(image.getBody() == null){
                    return;
                }
                BufferedImage read = ImageIO.read(new ByteArrayInputStream(image.getBody()));
                BufferedImage cao = EmojiUtils.avatarImageEmoji(read, jp,24);
                BufferedImage result = EmojiUtils.avatarImageEmoji(cao, hitokoto,15);

                Image image1 = subject.uploadImage(result);
                subject.sendMessage(image1);
            }
        }else {
            Optional<Image> image = contact.getMessage().stream().filter(e -> e instanceof Image).map(e->(Image)e).findFirst();
            Optional<SingleMessage> content = contact.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
            if(image.isPresent() && content.isPresent()){
                String contentToString = content.get().contentToString();
                String jp = translateService.baiduTranslate(contentToString, "auto", "jp");
                String imageUrl = contact.getBot().queryImageUrl(image.get());
                ResponseEntity<byte[]> imageData = restTemplate.getForEntity(imageUrl, byte[].class);
                if(imageData.getBody() == null){
                    subject.sendMessage("获取图片失败惹，一定是服务器被网线所蒙蔽了！");
                    return;
                }
                BufferedImage cao = EmojiUtils.imageImageEmoji(ImageIO.read(new ByteArrayInputStream(imageData.getBody())), jp,24);
                BufferedImage result = EmojiUtils.imageImageEmoji(cao, contentToString,15);
                Image image1 = subject.uploadImage(result);
                subject.sendMessage(image1);
            }
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return msg.getMessage().stream().filter(e -> e instanceof At).anyMatch(e->((At) e).getTarget() == msg.getBot().getId())
                && msg.getMessage().stream().filter(e->e instanceof PlainText).anyMatch(e->((PlainText) e).getContent().trim().equals("hito"));
    }
}
