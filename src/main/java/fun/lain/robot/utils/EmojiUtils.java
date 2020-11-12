package fun.lain.robot.utils;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.filter.GrayscaleFilter;
import fun.lain.robot.RobotApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * 简易地画表情包
 * @author Lain
 * @date 2020/9/29 9:43
 */
@Slf4j
public class EmojiUtils {
    private static final  Font MS_FONT;
    static {
        Font temp = null;
        try {
//            fonts = Font.createFonts(Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("font/msyh.ttc")));
//            temp = Font.createFont(Font.TRUETYPE_FONT,Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("font/msyh.ttc")));
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resources = resolver.getResource("/font/tamako.ttf");
            log.info("load font:{}",resources.getFilename());
            InputStream inputStream = resources.getInputStream();
            temp = Font.createFont(Font.TRUETYPE_FONT,inputStream);
        } catch (Exception e) {
            log.error("加载字体异常！",e);
        }
        MS_FONT = temp;
    }

    public static BufferedImage buildEmoji(BufferedImage image,String context,Color background,Color fontColor,int fontSize){
        if(StringUtils.isEmpty(context)){
            return image;
        }
        Font font = autoSuitFont(MS_FONT.deriveFont(Font.BOLD, fontSize),image.getWidth(),context);
        Graphics2D graphics = image.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int height = fontMetrics.getMaxAscent();
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
        int newHeight = image.getHeight() + lines.size() * height + 5;
        BufferedImage resizedImage = new BufferedImage(image.getWidth(),newHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D resizedGraphics = resizedImage.createGraphics();
        resizedGraphics.setFont(font);
        resizedGraphics.setColor(background);
        resizedGraphics.fillRect(0,0,resizedImage.getWidth(),newHeight);
        resizedGraphics.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);
        resizedGraphics.setColor(fontColor);
        for (int i = 0; i < lines.size(); i++) {
            if(i == lines.size() -1){
                int width = fontMetrics.stringWidth(temp.toString());
                int i1 = (resizedImage.getWidth() - width) / 2;
                TextLayout textLayout = new TextLayout(temp.toString(),font,resizedGraphics.getFontRenderContext());
                textLayout.draw(resizedGraphics,i1,image.getHeight() +  (i + 1) * height);
//                resizedGraphics.drawString(temp.toString(),i1,image.getHeight() +  (i + 1) * height);
            }else {

                TextLayout textLayout = new TextLayout(lines.get(i),font,resizedGraphics.getFontRenderContext());
                textLayout.draw(resizedGraphics,0,image.getHeight() + (i + 1) * height);
            }
        }
        return resizedImage;
    }

    private static Font autoSuitFont(Font sourceFont,int imageWidth,String context){
        if(StringUtils.isEmpty(context)){
            return sourceFont;
        }
        int size = sourceFont.getSize();
        if(size * context.length() <= imageWidth){
            return sourceFont.deriveFont((float)((imageWidth*3/4) / context.length()));//文字较少的时候，占画面3/4
        }
        return sourceFont;

    }

    public static BufferedImage createEmoji(String context,int fontSize) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("/emo/*.jpg");
        int i2 = new Random().nextInt(resources.length);
        BufferedImage image = ImageIO.read(resources[i2].getInputStream());
        return buildEmoji(image,context,Color.WHITE,Color.BLACK,fontSize);
    }

    public static ImmutableImage avatarImageEmoji(ImmutableImage source,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        BufferedImage image = buildEmoji(source.scaleToWidth(300).toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        return ImmutableImage.fromAwt(image).filter(new GrayscaleFilter());
    }

    public static ImmutableImage montageImages(List<BufferedImage> images){
        Optional<Integer> maxWidth = images.stream().max(Comparator.comparingInt(BufferedImage::getWidth)).map(BufferedImage::getWidth);
        return montageImages(images,maxWidth.orElse(0));
    }

    public static ImmutableImage imageImageEmoji(ImmutableImage source,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        BufferedImage image = buildEmoji(source.toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        return ImmutableImage.fromAwt(image).filter(new GrayscaleFilter());
    }



    private static ImmutableImage montageImages(List<BufferedImage> images,int maxWidth){
        ImmutableImage source = null;
        for (BufferedImage bufferedImage : images) {
            ImmutableImage added = ImmutableImage.fromAwt(bufferedImage).scaleToWidth(maxWidth);
            source = buildImage(source, added);
        }
        return source;
    }

    private static ImmutableImage buildImage(ImmutableImage origin,ImmutableImage added){
        if(origin == null){
            return added;
        }
        return origin.padBottom(added.height,Color.WHITE).overlay(added,0,origin.height);
    }
}
