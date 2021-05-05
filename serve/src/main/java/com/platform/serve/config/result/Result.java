package com.platform.serve.config.result;

import lombok.Data;
import lombok.ToString;

/**
 * http请求返回的最外层对象
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/4 22:25
 */
@Data
public class Result<T> {

    private static final String SUCCESS_MSG = "SUCCESS";
    private static final Integer SUCCESS_CODE = 1;
    private static final String ERROR_MSG = "ERROR";
    private static final Integer CHECK_ERROR_CODE = -1;
    private static final Integer ERROR_CODE = -2;


    /** 错误码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 具体的内容 */
    private T data;


    public static Result success(String msg, Object object) {
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        result.setData(object);
        return result;
    }


    public static Result success(String msg) {
        return success(msg, null);
    }


    private static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }


    public static Result error(String msg) {
        return error(ERROR_CODE, msg) ;
    }


    public static Result checkError(String msg) {
        return error(CHECK_ERROR_CODE, msg) ;
    }


}