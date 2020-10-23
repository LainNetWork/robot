package fun.lain.robot.constants;

/**
 * @author Lain <tianshang360@163.com>
 * @date 2020/10/20 15:25
 */
public interface ApiConstants {
    String XIN_JIE_LOGIN = "/auth/login?email={email}&passwd={passwd}&code=";
    String XIN_JIE_CHECKIN = "/user/checkin";
    String XIN_JIE_USER = "/user";
    String XIN_JIE_RESET = "/user/url_reset";

    String BAIDU_TRANSLATE = "https://api.fanyi.baidu.com/api/trans/vip/translate?q={q}&from={from}&to={to}&appid={appid}&salt={salt}&sign={sign}";
}
