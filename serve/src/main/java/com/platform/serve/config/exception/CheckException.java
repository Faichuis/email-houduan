package com.platform.serve.config.exception;

/**
 * 校验异自定义异常类
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/4 22:50
 */
public class CheckException extends RuntimeException {
    //注意：spring中，只有RuntimeException才会进行事务回滚，Exception不会进行事务回滚

    private String message;


    public CheckException(String message) {
        super(message);
        this.message = message;
    }


}