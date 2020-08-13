package com.chan.springboot.springboot04web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class Springboot04WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot04WebApplication.class, args);
    }

    @Bean
    public MyViewResolver myViewResolver() {
        return new MyViewResolver();
    }

    /**
     * 自定义视图解析器
     */
    private static class MyViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return null;
        }
    }
}
