package fun.lain.robot.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Lain <tianshang360@163.com>
 * @Date 2020/10/28 20:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult<T> {
    private int code;
    private T data;
    private String message;
    private String msg;

    public static <T> HttpResult<T> success(T data,String msg){
        HttpResult<T> httpResult = new HttpResult<>();
        httpResult.setCode(0);
        httpResult.setData(data);
        httpResult.setMessage(msg);
        return httpResult;
    }
    public static HttpResult<Object> success(String msg){
        HttpResult<Object> httpResult = new HttpResult<>();
        httpResult.setCode(0);
        httpResult.setMessage(msg);
        return httpResult;
    }

    public static <T> HttpResult<T> success(T data){
        HttpResult<T> httpResult = new HttpResult<>();
        httpResult.setCode(0);
        httpResult.setData(data);
        httpResult.setMessage("success");
        return httpResult;
    }

    public static <T> HttpResult<T> success(){
        HttpResult<T> httpResult = new HttpResult<>();
        httpResult.setCode(0);
        httpResult.setMessage("success");
        return httpResult;
    }

    public static <T> HttpResult<T> error(int code,T data,String msg){
        HttpResult<T> httpResult = new HttpResult<T>();
        httpResult.setCode(code);
        httpResult.setData(data);
        httpResult.setMessage(msg);
        return httpResult;
    }

    public static <T> HttpResult<T> error(T data,String msg){
        HttpResult<T> httpResult = new HttpResult<T>();
        httpResult.setCode(1);
        httpResult.setData(data);
        httpResult.setMessage(msg);
        return httpResult;
    }

    public static <T> HttpResult<T> error(String msg){
        HttpResult<T> httpResult = new HttpResult<T>();
        httpResult.setCode(1);
        httpResult.setMessage(msg);
        return httpResult;
    }

    public static <T> HttpResult<T> error(T data){
        HttpResult<T> httpResult = new HttpResult<>();
        httpResult.setCode(1);
        httpResult.setData(data);
        httpResult.setMessage("error");
        return httpResult;
    }

    public static <T> HttpResult<T> error(){
        HttpResult<T> httpResult = new HttpResult<>();
        httpResult.setCode(1);
        httpResult.setMessage("error");
        return httpResult;
    }
}
