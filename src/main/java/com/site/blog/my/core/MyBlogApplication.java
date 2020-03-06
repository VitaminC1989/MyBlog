package com.site.blog.my.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.site.blog.my.core.dao")
@SpringBootApplication
public class MyBlogApplication {
    public static void main(String[] args) {
        System.out.println("My-Blog is launching");
    	SpringApplication.run(MyBlogApplication.class, args);
    }
}
