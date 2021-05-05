package com.platform.serve.config.result;

import com.platform.serve.config.exception.CheckException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 捕获异常的类，返回信息给浏览器，可以自定义返回的code,msg等信息
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/4 22:47
 */
@ControllerAdvice
public class ExceptionHandle {

    //private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {

        //判断异常是否是我们定义的异常
        if (e instanceof CheckException) {
            CheckException checkException = (CheckException) e;
            return Result.checkError(checkException.getMessage());
        } else {
            return Result.error(e.getMessage());
        }
    }


}