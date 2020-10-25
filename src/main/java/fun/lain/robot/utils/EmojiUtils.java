package fun.lain.robot.utils;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.filter.GrayscaleFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
        Font font = autoSuitFont(MS_FONT.deriveFont(Font.BOLD, fontSize),image.getWidth(),context);
        Graphics2D graphics = image.createGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        TextLayout textLayout = new TextLayout(context,font,graphics.getFontRenderContext());
        int lineSpace = 5;
        int height = (int) textLayout.getBounds().getHeight() + lineSpace;
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
        int newHeight = image.getHeight() + lines.size() * height;
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

    private static Font autoSuitFont(Font sourceFont,int imageWidth,String context){
        int size = sourceFont.getSize();
        if(size * context.length() <= imageWidth){
            return sourceFont.deriveFont((float)((imageWidth*3/4) / context.length()));//文字较少的时候，占画面3/4
        }
        return sourceFont;

    }

    public static BufferedImage createEmoji(String context,int fontSize) throws IOException {
        int i2 = new Random().nextInt(4);
        BufferedImage image = ImageIO.read(Objects.requireNonNull(EmojiUtils.class.getClassLoader().getResourceAsStream("emo/emo" + i2 +".jpg")));
        return buildEmoji(image,context,Color.WHITE,Color.BLACK,fontSize);
    }

    public static ImmutableImage avatarImageEmoji(ImmutableImage source,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        BufferedImage image = buildEmoji(source.scaleToWidth(300).toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        ImmutableImage immutableImage = ImmutableImage.fromAwt(image).filter(new GrayscaleFilter());
        return immutableImage;
    }

    public static ImmutableImage montageImages(List<BufferedImage> images){
        Optional<Integer> maxWidth = images.stream().max(Comparator.comparingInt(BufferedImage::getWidth)).map(BufferedImage::getWidth);
        ImmutableImage source = montageImages(images,maxWidth.orElse(0));
        return source;
    }

    public static ImmutableImage imageImageEmoji(ImmutableImage source,String context,int fontSize) throws IOException {
        if(context == null){
            context = "";
        }
        BufferedImage image = buildEmoji(source.toNewBufferedImage(BufferedImage.TYPE_INT_RGB), context,Color.BLACK,Color.WHITE,fontSize);
        ImmutableImage immutableImage = ImmutableImage.fromAwt(image).filter(new GrayscaleFilter());
        return immutableImage;
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
