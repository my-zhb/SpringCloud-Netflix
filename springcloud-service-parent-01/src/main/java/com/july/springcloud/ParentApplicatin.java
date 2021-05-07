package com.july.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ProjectName: springcloud-service-parent-01
 * @Package: com.july.springcloud
 * @ClassName: ParentApplicatin
 * @Author: July
 * @Description:
 * @url:
 * @Date: 2021/4/27 15:46
 * @Version: 1.0
 */
@SpringBootApplication
@Slf4j
public class ParentApplicatin {
    public static void main(String[] args) {
        SpringApplication.run(ParentApplicatin.class,args);
    }

}
