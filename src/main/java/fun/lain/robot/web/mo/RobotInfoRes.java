package fun.lain.robot.web.mo;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/30 21:51
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RobotInfoRes {
    private Long id;
    private List<Long> friendList = new ArrayList<>();
    private List<Long> groupList = new ArrayList<>();
}
