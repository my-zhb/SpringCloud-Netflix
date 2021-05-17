package com.july.springcloud;

import jdk.internal.instrumentation.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ProjectName: 01
 * @Package: com.july.springcloud
 * @ClassName: ProtalApplicatin
 * @Author: July
 * @Description: 消费者模块
 * @url:
 * @Date: 2021/4/28 11:25
 * @Version: 1.0
 */
@Slf4j
/** 开启对hystrix的服务熔断降级支持**/
@EnableHystrix
/**开启对Eureka的支持**/
@EnableEurekaClient
/**开启对Fegin的支持**/
@EnableFeignClients
@SpringBootApplication
/**
 * @SpringCloudApplication 注解 包含了3个：
 * @SpringBootApplication springboot启动
 * @EnableDiscoveryClient 这个注解等价于@EnableEurekaClient 开启对Eureka
 * @EnableCircuitBreaker 这个注解等价于@EnableHystrix 开放hystrix
 */
public class PortalApplicatin {
    public static void main(String[] args) {
        SpringApplication.run(PortalApplicatin.class,args);
        log.info("portal服务（消费者）已启动，端口：8000");
    }
}
