package fun.lain.robot.utils;

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
        MS_FONT = fonts[0].deriveFont(Font.PLAIN,28);
    }


    public static BufferedImage createEmoji(String context) throws IOException {
        int i2 = new Random().nextInt(4);
        BufferedImage image = ImageIO.read(Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("emo/emo" + i2 +".jpg")));
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(MS_FONT);
        graphics.setColor(Color.BLACK);
        FontMetrics fontMetrics = graphics.getFontMetrics(MS_FONT);
        int height = fontMetrics.getHeight();
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
        if(CollectionUtils.isEmpty(lines)){
            int i = fontMetrics.stringWidth(temp.toString());
            int i1 = (image.getWidth() - i) / 2;
            graphics.drawString(temp.toString(),i1,310);
        }else {
            if(!StringUtils.isEmpty(temp.toString())){
                lines.add(temp.toString());
            }
            for (int i = 0; i < lines.size(); i++) {
                if(i == lines.size() -1){
                    int width = fontMetrics.stringWidth(temp.toString());
                    int i1 = (image.getWidth() - width) / 2;
                    graphics.drawString(temp.toString(),i1,310 +  i * height);
                }else {
                    graphics.drawString(lines.get(i),10,310 + i * height);
                }
            }
        }


//        if(totalWidth > image.getWidth()){
//            int rest = totalWidth - image.getWidth();
//            int i = (int) Math.floor(context.length() * rest / totalWidth);
//            if ( i > 0 ){
//                String firstLine = context.substring(0 , context.length() - i);
//                String substring = context.substring(context.length() - i);
//                graphics.drawString(firstLine,10,310);
//                graphics.drawString(substring,10,350);
//            }
//        }else {
//            graphics.drawString(context,10,310);
//        }

        return image;
    }
}
