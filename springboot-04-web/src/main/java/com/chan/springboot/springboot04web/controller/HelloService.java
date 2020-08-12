package com.chan.springboot.springboot04web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * HelloService
 *
 * @author Chan
 * @since 2020/8/12
 */
@Controller
public class HelloService {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @RequestMapping("/success")
    public Map<String, String> success() {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "你好");
        return map;
    }
}