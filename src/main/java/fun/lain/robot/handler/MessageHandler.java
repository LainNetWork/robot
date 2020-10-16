package fun.lain.robot.handler;

import fun.lain.robot.service.RobotService;
import fun.lain.robot.utils.BeanUtils;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.Optional;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/16 22:30
 */
public interface MessageHandler {
   int order();

   String name();

   String describe();

   void handleMsg(MessageEvent contact) throws Exception;

   boolean isMatch(MessageEvent msg);

   default String getFirstPlainTextMsg(MessageEvent messageEvent){
      Optional<SingleMessage> firstText = messageEvent.getMessage().stream().filter(e -> e instanceof PlainText).findFirst();
      if(firstText.isEmpty()){
         return "";
      }
      SingleMessage singleMessage = firstText.get();
      return singleMessage.contentToString();
   }

   default RobotService getBotService(){
      return BeanUtils.getBean(RobotService.class);
   }

}
