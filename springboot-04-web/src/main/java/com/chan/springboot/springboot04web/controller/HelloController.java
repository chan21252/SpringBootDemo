package com.chan.springboot.springboot04web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * HelloService
 *
 * @author Chan
 * @since 2020/8/12
 */
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello";
    }

//    @RequestMapping(path = {"/", "/index.html"})
//    public String index() {
//        return "index";
//    }
}