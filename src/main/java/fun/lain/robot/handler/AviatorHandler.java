package fun.lain.robot.handler;

import com.googlecode.aviator.AviatorEvaluator;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 0:53
 */
@Component
public class AviatorHandler implements  MessageHandler{
    private static final String MARK = "run ";

    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "ExpressionExecutor";
    }

    @Override
    public String describe() {
        return "脚本执行";
    }

    @Override
    public void handleMsg(MessageEvent messageEvent) throws Exception {
        Optional<SingleMessage> firstText = messageEvent.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
        if(firstText.isEmpty()){
            return;
        }
        SingleMessage singleMessage = firstText.get();
        String msg = singleMessage.contentToString();
        Contact contact= messageEvent.getSubject();
        String exp = msg.substring(MARK.length());
        try {
            AviatorEvaluator.validate(exp);
        } catch (Exception e) {
            contact.sendMessage("表达式不正确，ba——ka!");
        }
        Object execute = null;
        try {
            execute = AviatorEvaluator.execute(exp);
        } catch (Exception e) {
            contact.sendMessage("执行出错了哦" + e.getMessage());
        }
        contact.sendMessage(String.valueOf(execute));
    }

    @Override
    public boolean isMatch(MessageEvent messageEvent) {
        Optional<SingleMessage> firstText = messageEvent.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
        if(firstText.isEmpty()){
            return false;
        }
        SingleMessage singleMessage = firstText.get();
        String msg = singleMessage.contentToString();
        return !StringUtils.isEmpty(msg) && msg.startsWith(MARK);
    }
}
