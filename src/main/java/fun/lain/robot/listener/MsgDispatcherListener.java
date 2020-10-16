package fun.lain.robot.listener;

import fun.lain.robot.service.HandlerService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.MessageEvent;
import org.springframework.stereotype.Component;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 23:05
 */

@Setter
public class MsgDispatcherListener extends SimpleListenerHost {
    private HandlerService handlerService;

    @EventHandler
    public ListeningStatus onEvent(MessageEvent messageEvent){
        handlerService.dispatchMsg(messageEvent);
        return ListeningStatus.LISTENING;
    }


}
