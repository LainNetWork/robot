package fun.lain.robot.handler;

import com.sksamuel.scrimage.ImmutableImage;
import fun.lain.robot.cache.ImageCache;
import fun.lain.robot.utils.BeanUtils;
import fun.lain.robot.utils.EmojiUtils;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:33
 */
@Component
@AllArgsConstructor
public class EmojiHandler implements MessageHandler {
    private final ImageCache imageCache;
    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "Emoji";
    }

    @Override
    public String describe() {
        return "表情包";
    }

    @Override
    public void handleMsg(MessageEvent messageEvent) throws Exception {

        String msg = getFirstPlainTextMsg(messageEvent);
        Contact contact = messageEvent.getSubject();
        int index = msg.indexOf(".jpg");
        String content = msg.substring(0, index);
        ImmutableImage immutableImage = ImmutableImage.fromAwt(EmojiUtils.createEmoji(content, 24));
        Image image = contact.uploadImage(immutableImage.padBottom(15, Color.WHITE).toNewBufferedImage(BufferedImage.TYPE_INT_RGB));

        MessageReceipt<Contact> contactMessageReceipt = contact.sendMessage(image);
        int id = contactMessageReceipt.getSource().getId();
        imageCache.put(messageEvent.getSubject().getId() + "_" + id, List.of(image.getImageId()));
    }

    @Override
    public boolean isMatch(MessageEvent messageEvent) {
        String msg = getFirstPlainTextMsg(messageEvent);
        return !StringUtils.isEmpty(msg) && msg.endsWith(".jpg");
    }
}
