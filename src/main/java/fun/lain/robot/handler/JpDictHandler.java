package fun.lain.robot.handler;

import net.mamoe.mirai.message.MessageEvent;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Lain <tianshang360@163.com>
 * @date 2020/11/4 10:42
 */
@Component
public class JpDictHandler implements MessageHandler{
    private static final String URL = "https://www.weblio.jp/content/%s";
    private static final String SPLIT = "jp ";
    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "jpDict";
    }

    @Override
    public String describe() {
        return "日语词典";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        String content = contact.getMessage().contentToString();
        String jp = content.substring(SPLIT.length());
        Connection connect = Jsoup.connect(String.format(URL, jp));
        Document document = connect.get();
        Elements kijiWrp = document.select(".kijiWrp");
        Optional<Element> first = kijiWrp.stream().findFirst();
        if(first.isPresent()){
            contact.getSubject().sendMessage(first.get().text());
        }else {
            contact.getSubject().sendMessage("没有查到哦");
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return getFirstPlainTextMsg(msg).startsWith("jp ");
    }
}
