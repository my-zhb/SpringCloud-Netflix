package com.july.springcloud.service;

import com.july.springcloud.constants.Constant;
import com.july.springcloud.model.ResultObject;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.service
 * @ClassName: GoodsRemoteClientFallBack
 * @Author: July
 * @Description:
 * @url:
 * @Date: 2021/5/17 15:30
 * @Version: 1.0
 */
@Component
public class GoodsClientFallBack implements FallbackFactory<GoodsClient> {

    @Override
    public GoodsClient create(Throwable throwable) {
        return  new GoodsClient(){
            @Override
            public ResultObject goods() {
                String message = throwable.getMessage();
                System.out.println("Fegin 远程调用出现错误" + message);
                return new ResultObject(Constant.ONE,"服务异常!",message);
            }
        };
    }
}
