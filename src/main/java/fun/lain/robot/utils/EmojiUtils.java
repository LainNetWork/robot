package fun.lain.robot.utils;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.filter.GrayscaleFilter;
import com.sksamuel.scrimage.nio.ImmutableImageLoader;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * 简易地画表情包
 * @author Lain
 * @date 2020/9/29 9:43
 */
public class EmojiUtils {
    private static final  Font MS_FONT;
    static {
        Font[] fonts = new Font[0];
        try {
            fonts = Font.createFonts(Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("font/msyh.ttc")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MS_FONT = fonts[0];
    }

    public static BufferedImage buildEmoji(BufferedImage image,String context,Color background,Color fontColor,int fontSize){
        Font font = MS_FONT.deriveFont(Font.BOLD, fontSize);
        Graphics2D graphics = image.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int lineSpace = 2;//字符高度预留行间距
        int height = fontMetrics.getHeight() + lineSpace;
        List<String> lines = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        for (char c : context.toCharArray()) {
            String a = String.valueOf(c);
            String append = temp.toString() + a;
            if(fontMetrics.stringWidth(append) > image.getWidth()){
                lines.add(temp.toString());
                temp = new StringBuilder(a);
            }else{
                temp.append(a);
            }
        }
        lines.add(temp.toString());
        //扩容图片长度
        int newHeight = image.getHeight() + lines.size() * height + lineSpace*3;
        BufferedImage resizedImage = new BufferedImage(image.getWidth(),newHeight,BufferedImage.TYPE_INT_RGB);
        Graphics resizedGraphics = resizedImage.getGraphics();
        resizedGraphics.setFont(font);
        resizedGraphics.setColor(background);
        resizedGraphics.fillRect(0,0,resizedImage.getWidth(),newHeight);
        resizedGraphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);
        resizedGraphics.setColor(fontColor);
        for (int i = 0; i < lines.size(); i++) {
            if(i == lines.size() -1){
                int width = fontMetrics.stringWidth(temp.toString());
                int i1 = (resizedImage.getWidth() - width) / 2;
                resizedGraphics.drawString(temp.toString(),i1,image.getHeight() +  (i + 1) * height - lineSpace);
            }else {
                resizedGraphics.drawString(lines.get(i),0,image.getHeight() + (i + 1) * height - lineSpace);
            }
        }
        return resizedImage;
    }

    public static BufferedImage createEmoji(String context,int fontSize) throws IOException {
        int i2 = new Random().nextInt(4);
        BufferedImage image = ImageIO.read(Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("emo/emo" + i2 +".jpg")));
        return buildEmoji(image,context,Color.WHITE,Color.BLACK,fontSize);
    }

    public static BufferedImage avatarImageEmoji(BufferedImage avatar,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        ImmutableImage source = ImmutableImage.fromAwt(avatar);
        BufferedImage image = buildEmoji(source.scaleToWidth(300).toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        ImmutableImage immutableImage = ImmutableImage.fromAwt(image);
        ImmutableImage filter = immutableImage.filter(new GrayscaleFilter());
        return filter.toNewBufferedImage(BufferedImage.TYPE_INT_RGB);
    }

    public static BufferedImage imageImageEmoji(BufferedImage avatar,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        ImmutableImage source = ImmutableImage.fromAwt(avatar);
        BufferedImage image = buildEmoji(source.toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        ImmutableImage immutableImage = ImmutableImage.fromAwt(image);
        ImmutableImage filter = immutableImage.filter(new GrayscaleFilter());
        return filter.toNewBufferedImage(BufferedImage.TYPE_INT_RGB);
    }
}
