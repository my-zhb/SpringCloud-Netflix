package com.july.springcloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.filter
 * @ClassName: LogFilter
 * @Author: July
 * @Description: 日志过滤器
 * @url:
 * @Date: 2021/6/7 16:36
 * @Version: 1.0
 */
@Component
public class LogFilter extends ZuulFilter {

    /**
     * 设置过滤器状态 -> 在路由时执行
     * @return
     */
    @Override
    public String filterType() {
        /**
         *  出现错误
         *  public static final String ERROR_TYPE = "error";
         *  路由之后执行
         *  public static final String POST_TYPE = "post";
         *  路由之前执行
         *  public static final String PRE_TYPE = "pre";
         *  路由
         *  public static final String ROUTE_TYPE = "route";
         */
        return FilterConstants.ROUTE_TYPE;
    }

    /**
     * 设置路由顺序 （顺序小的先执行）
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER;
    }

    /**
     *  设置是否启用过滤器
     *  true 启用  反之
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 在路由时执行run方法
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String serverName = request.getServerName();
        System.out.println("访问地址：" + request.getRequestURI());
        return null;
    }
}
