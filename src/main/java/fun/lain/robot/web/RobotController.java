package fun.lain.robot.web;

import fun.lain.robot.service.RobotService;
import fun.lain.robot.web.mo.RobotInfoRes;
import lombok.AllArgsConstructor;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/30 21:53
 */
@RestController
@RequestMapping("/robot")
@AllArgsConstructor
public class RobotController {

    private final RobotService robotService;

    @GetMapping("/info")
    public HttpResult<RobotInfoRes> getRobotInfo(){
        List<Long> friends = robotService.getBot().getFriends().stream().map(Friend::getId).collect(Collectors.toList());
        List<Long> groups = robotService.getBot().getGroups().stream().map(Group::getId).collect(Collectors.toList());
        Long id = robotService.getBot().getId();
        return HttpResult.success(RobotInfoRes.builder()
                .friendList(friends)
                .groupList(groups)
                .id(id)
                .build());
    }

}
