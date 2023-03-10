package com.rookie.bigdata;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * rookie-security.v.1.0.0
 * @Classname RookieSecurityApplication
 * @Description http://localhost:18900/quickstart/hello/hello  输入用户名和密码：admin/admin
 * 也可以用postman的basic
 * @Author rookie
 * @Date 2021/8/6 16:20
 * @Version 1.0
 */

@MapperScan(basePackages = "com.rookie.bigdata.mapper")
@SpringBootApplication
public class RookieSecurityApplication {

    public static void main(String[] args) {

        //UserDetailsServiceAutoConfiguration
        SpringApplication.run(RookieSecurityApplication.class);
    }
}
