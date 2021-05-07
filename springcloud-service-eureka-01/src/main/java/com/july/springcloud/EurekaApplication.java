package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ProjectName: 01
 * @Package: com.july.springcloud
 * @ClassName: EurekaApplication
 * @Author: July
 * @Description: eureka注册中心
 * @url:
 * @Date: 2021/4/29 14:45
 * @Version: 1.0
 */
@Slf4j
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);
        log.info("eureka服务（注册中心）已启动，端口：8761");
    }
}
