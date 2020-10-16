package fun.lain.robot.service;

import fun.lain.robot.handler.MessageHandler;
import net.mamoe.mirai.message.MessageEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/17 0:58
 */
public class HandlerService {
    private final ConcurrentHashMap<String,MessageHandler> HANDLERS = new ConcurrentHashMap<>();

    public void add(MessageHandler messageHandler){
        this.HANDLERS.put(messageHandler.name(),messageHandler);
    }

    public void remove(MessageHandler messageHandler){
        this.HANDLERS.put(messageHandler.name(),messageHandler);
    }

    public void dispatchMsg(MessageEvent messageEvent){
        HANDLERS.values().stream().filter(e->e.isMatch(messageEvent)).sorted((e1,e2)->e2.order()-e1.order()).forEach(e->{
            try {
                e.handleMsg(messageEvent);
            } catch (Exception ex) {
                ex.printStackTrace();
                messageEvent.getSubject().sendMessage("处理失败了喵...");
            }
        });
    }

}
