package com.july.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @ProjectName: 01
 * @Package: com.july.springcloud.config
 * @ClassName: RestConfig
 * @Author: July
 * @Description:
 * @url:
 * @Date: 2021/4/29 11:47
 * @Version: 1.0
 */
@Configuration
public class RestConfig {

    //@LoadBalanced 使用Ribbon实现负载均衡的调用
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
