package com.july.springcloud.service;

import com.july.springcloud.constants.Constant;
import com.july.springcloud.model.ResultObject;
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
public class GoodsClientFallBack implements GoodsClient{

    @Override
    public ResultObject goods() {
        return new ResultObject(Constant.ONE,"fegin 服务降级");
    }
}
