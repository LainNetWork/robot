package fun.lain.robot.web.auth;

import com.alibaba.fastjson.JSONObject;
import fun.lain.robot.web.HttpResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/29 22:26
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取注解，判断接口是否需要鉴权
        if(handler instanceof HandlerMethod){
            HandlerMethod methodHandle = (HandlerMethod) handler;
            if (methodHandle.hasMethodAnnotation(NeedAuth.class)) {
                //todo auth
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.println(JSONObject.toJSONString(HttpResult.error(403,"not Auth")));
                outputStream.close();
                return false;
            }
        }
        return true;
    }
}
