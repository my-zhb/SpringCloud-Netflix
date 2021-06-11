package com.july.springcloud.fallback;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ProjectName: SpringCloud-Netflix
 * @Package: com.july.springcloud.fallback
 * @ClassName: ProviderFallback
 * @Author: July
 * @Description: zuul服务熔断
 * @url:
 * @Date: 2021/6/11 15:10
 * @Version: 1.0
 */
@Component
public class ZuulFallback implements FallbackProvider {

    /**
     * 配置对那些服务进行使用
     * @return
     */
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {

            //设置headers
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type","text/html; charset=UTF-8");
                return headers;
            }

            //设置状态码
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST;
            }

            //设置响应体
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("服务正在维护,请稍后再试".getBytes());
            }

            //设置状态码的值，如200，400等
            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST.value();
            }

            //设置状态的文本
            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.BAD_REQUEST.getReasonPhrase();
            }

            @Override
            public void close() {

            }
        };
    }
}
