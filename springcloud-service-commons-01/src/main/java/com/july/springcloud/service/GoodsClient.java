package com.july.springcloud.service;

import com.july.springcloud.model.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.service
 * @ClassName: GoodsClient
 * @Author: July
 * @Description: openFegin调用
 * @url:
 * @Date: 2021/5/11 16:24
 * @Version: 1.0
 */
/**开启Fegin,FeignClient("服务的名称")**/
@FeignClient(value = "SPRINGCLOUD-SERVICE-GOODS",fallbackFactory = GoodsClientFallBack.class)
public interface GoodsClient {

    @RequestMapping(value = "/service/goods")
    public ResultObject goods();
}
