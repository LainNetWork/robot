package fun.lain.robot.handler;

import net.mamoe.mirai.message.MessageEvent;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/12/15 21:02
 */
@Component
public class KVHandler implements MessageHandler{

    private static final String  CMD_GET = "mem r " , CMD_SET = "mem w ";

    private final StringRedisTemplate redisTemplate;

    public KVHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public int order() {
        return 0;
    }

    @Override
    public String name() {
        return "K-V";
    }

    @Override
    public String describe() {
        return "简单的KV记录器，作备忘录用";
    }

    @Override
    public void handleMsg(MessageEvent contact) throws Exception {
        String content = contact.getMessage().contentToString();
        if(content.startsWith(CMD_SET)){
            String subContent = content.substring(CMD_SET.length());//命令之外的内容
            int index = subContent.indexOf(" ");//key值
            if(index > 0){ //存在
                String key = subContent.substring(0,index);
                String value = subContent.substring(index);
                redisTemplate.boundValueOps(key).set(value);
                contact.getSubject().sendMessage("保存成功⭐~");
            }
        }else if(content.startsWith(CMD_GET)){
            String key = content.substring(CMD_GET.length());//命令之外的内容
            String value = redisTemplate.boundValueOps(key).get();
            if(StringUtils.isNotBlank(value)){
                contact.getSubject().sendMessage(value);
            }
        }
    }

    @Override
    public boolean isMatch(MessageEvent msg) {
        String firstPlainTextMsg = getFirstPlainTextMsg(msg);
        return firstPlainTextMsg.startsWith(CMD_GET) || firstPlainTextMsg.startsWith(CMD_SET);
    }


}
