package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


@Slf4j
//开启zuul
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplicatin {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplicatin.class,args);
        log.info("zuul服务（网关）已启动，端口：80");
    }
}
