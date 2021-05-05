package com.platform.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lids
 * @version 1.0
 * @date 2020/12/20 21:26
 */
@RestController
@RequestMapping(path = "/ceshi")
public class EmailController {


    @SuppressWarnings(value = {"unchecked", "deprecation"})//告诉编译器同时忽略unchecked和deprecation的警告信息
    @GetMapping(value = "/get")
    public String sendEmail() {
        return "阿斯达山东撒粉速度！！！";
    }

}
