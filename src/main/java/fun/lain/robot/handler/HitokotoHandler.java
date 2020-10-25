package fun.lain.robot.handler;

import com.sksamuel.scrimage.ImmutableImage;
import fun.lain.robot.service.TranslateService;
import fun.lain.robot.utils.EmojiUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.message.data.Image;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lain <tianshang360@163.com>
 * @date 2020/10/23 12:12
 */
@Component
@Slf4j
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
                ImmutableImage cao = EmojiUtils.avatarImageEmoji(ImmutableImage.fromAwt(read), jp,24);
                ImmutableImage result = EmojiUtils.avatarImageEmoji(cao, hitokoto,15);
                Image image1 = subject.uploadImage(result.padBottom(15,Color.BLACK).toNewBufferedImage(BufferedImage.TYPE_INT_RGB));
                subject.sendMessage(image1);
            }
        }else {
            List<Image> collect = contact.getMessage().stream().filter(e -> e instanceof Image).map(e -> (Image) e).collect(Collectors.toList());
            Optional<SingleMessage> content = contact.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
            List<BufferedImage> result = new ArrayList<>();
            String contentToString = content.map(SingleMessage::contentToString).orElse("");
            String jp = translateService.baiduTranslate(contentToString, "auto", "jp");
            for (Image image : collect) {
                String imageUrl = contact.getBot().queryImageUrl(image);
                ResponseEntity<byte[]> imageData = restTemplate.getForEntity(imageUrl, byte[].class);
                if(imageData.getBody() == null){
                    subject.sendMessage("获取图片失败惹，一定是服务器被网线所蒙蔽了！");
                    return;
                }
                BufferedImage read = ImageIO.read(new ByteArrayInputStream(imageData.getBody()));
                result.add(read);
            }
            ImmutableImage source = EmojiUtils.montageImages(result);
            ImmutableImage cao = EmojiUtils.imageImageEmoji(source, jp,24);
            ImmutableImage resultImage = EmojiUtils.imageImageEmoji(cao, contentToString,15);
            Image image1 = subject.uploadImage(resultImage.padBottom(15, Color.BLACK).toNewBufferedImage(BufferedImage.TYPE_INT_RGB));
            subject.sendMessage(image1);
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return msg.getMessage().stream().filter(e -> e instanceof At).anyMatch(e->((At) e).getTarget() == msg.getBot().getId())
                && msg.getMessage().stream().filter(e->e instanceof PlainText).anyMatch(e->((PlainText) e).getContent().trim().equals("hito"));
    }
}
