package fun.lain.robot.handler;

import com.alibaba.fastjson.JSONObject;
import fun.lain.robot.cache.AuthCache;
import fun.lain.robot.config.properties.XinJieProperties;
import fun.lain.robot.constants.ApiConstants;
import fun.lain.robot.constants.TokenEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/20 0:25
 */
@Component
@Slf4j
@AllArgsConstructor
public class XinJieHandler implements  MessageHandler{
    private static final String PREFIX = "ss ";
    private final AuthCache authCache;
    private final XinJieProperties xinJieProperties;
    private final RestTemplate restTemplate;

    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "XinJie";
    }

    @Override
    public String describe() {
        return "心阶云插件";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        Contact subject = contact.getSubject();
        String command = getFirstPlainTextMsg(contact).substring(PREFIX.length());
        switch (command){
            case "info" : {
                Map<String, String> xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
                Connection connection = Jsoup.connect(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER)
                        .cookies(xinjieCookies).header(HttpHeaders.REFERER,"http://www.xinjiecloud.vip/auth/login")
                        .header(HttpHeaders.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
                Document document = connection.get();
                StringBuilder stringBuilder = new StringBuilder("流量使用情况:\n");
                document.getElementsByClass("traffic-info").forEach(e->{
                    stringBuilder.append(e.text()).append(": ").append(e.nextElementSibling().text()).append("\n");
                });
                Elements timeLimit = document.getElementsContainingText("等级到期时间");
                int size = timeLimit.size();
                if(size > 0){
                    String text = timeLimit.eachText().get(size -1);
                    stringBuilder.append(text);
                    subject.sendMessage(stringBuilder.toString());
                }else{
                    subject.sendMessage("什么都没获取到呢");
                }
                break;
            }
            case "sub":{
                if(subject instanceof Group){
                    subject.sendMessage("这是人家的小秘密，请私聊人家哦~");
                    return;
                }
                if(xinJieProperties.getUserId().contains(contact.getSender().getId())){
                    subject.sendMessage("↓请通过Base64解码获取最新订阅↓");
                    subject.sendMessage("测试111111测试11111111测试1111111测试11111111111测试111111测试111111111111测试111111111测试1111测试1111111测试111111111测试");
                    subject.sendMessage(new PlainText(getSubInfo(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER)));
                    subject.sendMessage("👆Base64在线解密https://base64.us/");
                }
                break;
            }
            case "reset" : {
                if(!xinJieProperties.getMasterId().contains(contact.getSender().getId())){
                    subject.sendMessage("只有主人才可以重置伦家哦QAQ");
                    break;
                }
                subject.sendMessage("↓重置成功啦！请通过Base64解码获取最新订阅↓");
                subject.sendMessage(getSubInfo(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_RESET));
                subject.sendMessage("👆Base64在线解密https://base64.us/");
                break;
            }
        }
    }

    private String getSubInfo(String url) throws IOException {
        Map<String, String> xinjieCookies = null;
        xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
        Document document = Jsoup.connect(url).cookies(xinjieCookies).get();
        Map<String, String> ssrLinks = getSubscribeLink(document, ".quickadd #all_ssr");
        Map<String, String> v2rayLinks = getSubscribeLink(document, ".quickadd #all_v2ray");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("酸酸乳：\n");
        ssrLinks.forEach((k,v)->stringBuilder.append(k).append(" ").append(v).append("\n"));
        stringBuilder.append("威图Ray：\n");
        v2rayLinks.forEach((k,v)->stringBuilder.append(k).append(" ").append(v).append("\n"));
        return Base64Utils.encodeToString(stringBuilder.toString().getBytes());
    }

    private Map<String,String> getSubscribeLink(Document document,String selector){
        Elements sub = document.select(selector);
        Elements input = sub.select("input");
        Map<String,String> returnMap = new HashMap<>();
        input.forEach(e->{
            Element element = e.parent().previousElementSibling();
            element.children().remove();
            returnMap.put(element.text(),e.val());
        });
        return returnMap;
    }


    @Scheduled(cron = "0 0 9 * * ?")
    public void sign(){
        Map<String,String> xinjieCookies = null;
        try {
            xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
            Document post = Jsoup.connect(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_CHECKIN).cookies(xinjieCookies).referrer(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER).post();
            xinJieProperties.getServiceGroup().forEach(e->{
                getBotService().getBot().getGroup(e).sendMessage(post.toString());
            });
        } catch (IOException e) {
            xinJieProperties.getServiceGroup().forEach(e2->{
                getBotService().getBot().getGroup(e2).sendMessage("签到失败喵！");
            });
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return getFirstPlainTextMsg(msg).startsWith(PREFIX);
    }

}
