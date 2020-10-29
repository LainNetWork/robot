package fun.lain.robot.web;

import fun.lain.robot.service.RobotService;
import fun.lain.robot.web.auth.NeedAuth;
import fun.lain.robot.web.mo.QQMessageRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.MessageReceipt;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/28 20:30
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/push")
public class MessagePushController {
    private final RobotService robotService;

    @PostMapping("/group/{groupId}")
    @NeedAuth
    public HttpResult<Object> pushMessageToGroup(@PathVariable Long groupId, @RequestBody QQMessageRes qqMessageRep){
        Bot bot = robotService.getBot();
        Group group;
        try {
            group = bot.getGroup(groupId);
        } catch (NoSuchElementException e) {
            return HttpResult.error("组不存在！");
        }
        group.sendMessage(qqMessageRep.getMsg());
        return HttpResult.success();
    }

    @PostMapping("/friends/{friendId}")
    public HttpResult<Object> pushMessageToFriends(@PathVariable Long friendId,@RequestBody QQMessageRes qqMessageRep){
        Bot bot = robotService.getBot();
        Friend friend;
        try {
            friend = bot.getFriend(friendId);
        } catch (NoSuchElementException e) {
            return HttpResult.error("好友不存在！");
        }
        friend.sendMessage(qqMessageRep.getMsg());
        return HttpResult.success();
    }
}
