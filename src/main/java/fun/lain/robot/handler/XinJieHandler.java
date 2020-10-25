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
import net.mamoe.mirai.message.MessageEvent;
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
                List<String> xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
                Connection connection = Jsoup.connect(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER).header(HttpHeaders.COOKIE, String.join(";", xinjieCookies));
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
                    subject.sendMessage(getSubInfo(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER));
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

    private String getSubInfo(String url){
        List<String> xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
        HttpHeaders httpHeaders = new HttpHeaders();
        xinjieCookies.forEach(e->httpHeaders.add(HttpHeaders.COOKIE,e));
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> result = restTemplate.exchange(url,HttpMethod.GET,httpEntity, String.class);
        String body = result.getBody();
        Document document = Jsoup.parse(body);
        Map<String, String> ssrLinks = getSubscribeLink(document, ".quickadd #all_ssr");
        Map<String, String> v2rayLinks = getSubscribeLink(document, ".quickadd #all_v2ray");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("酸酸乳订阅，每日新鲜送到家！：\n");
        ssrLinks.forEach((k,v)->stringBuilder.append(k).append(" ").append(v).append("\n"));
        stringBuilder.append("威图Ray，专属定制手机，尊享品质人生：");
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
        List<String> xinjieCookies = authCache.getXinjieCookies(TokenEnum.XIN_JIE_CLOUD.name());
        HttpHeaders httpHeaders = new HttpHeaders();
        xinjieCookies.forEach(e->httpHeaders.add(HttpHeaders.COOKIE,e));
        httpHeaders.add(HttpHeaders.REFERER,xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_USER);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(xinJieProperties.getBaseURL() + ApiConstants.XIN_JIE_CHECKIN, HttpMethod.POST, httpEntity, String.class);
        String resp = exchange.getBody();
        JSONObject jsonObject = JSONObject.parseObject(resp);
        xinJieProperties.getServiceGroup().forEach(e->{
            getBotService().getBot().getGroup(e).sendMessage(jsonObject.toJSONString());
        });
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        return getFirstPlainTextMsg(msg).startsWith(PREFIX);
    }

}
