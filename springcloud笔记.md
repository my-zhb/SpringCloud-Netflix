# SpringCloud Netflix

## Spring Cloud Eureka

> Eureka是什么？

1. `Eureka`是Netflix的一个子模块，也是核心模块之一。`Eureka`是一个基于Rest的服务，用于定位服务，以实现云端中间层服务发现和故障转移。

2. `Eureka`采用了C-S的设计架构，`Eureka Server`作为服务注册功能的服务器，他是服务注册中心，而系统其他微服务，使用`Eureka`的客户端连接到`Eureka Server`并维持心跳连接，这样运维人员就可以通过`Eureka Server`来监控每个微服务是否正常运行。


> 什么是服务注册？

服务注册：将服务所在主机、端口、版本号、通信协议等信息登记到注册中心。


> 什么是服务发现？

服务发现：服务消费者向注册中心请求已经登记的服务列表，然后得到某个服务的主机、端口、版本号、通信协议等信息。从而实现对具体服务的调用。

> Eureka在项目中解决了那些问题？

1. 我们不要在去定义接口的地址
2. 可以实现接口的负载均衡
3. 同时实现故障转移，可以知道那些服务宕机等等

> Eureka与zookeeper比较

1. CAP理论指出，一个分布式系统不可能同时满足`C(一致性)、A(可用性)、P(分区容错性->服务本身没有问题而是由于网络原因延迟导致服务无法访问)`

2. 由于分区容错性在分布式中必须要保证的，因此我们只能在A和C之间进行权衡，在此`zookeeper`保证的是`CP`，而`Eureka`保证的是`AP`。

> Zookeeper保证CP

在`zookeeper`中，当`master`节点因为网络故障与其他节点失去联系是，剩余节点会重新进行`leader`选举，但是问题在于，选举`leader`需要一定的时间，且选择期间整个`zookeeper`集群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因网络问题使得`zookeeper`集群失去`master`节点是大概率时间，虽然服务最终能够恢复，但是在选举时间内导致服务注册长期不可用时难以容忍的。

> Eureka保证AP

`Eureka`优先保证可用性，`Eureka`各个节点时平等的，某几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和服务查询。而`Eureka`的客户端在向某个`Eureka`注册时如果发现连接失败，则会自动切换至其他节点，只要有一台`Eureka`还在，就能保证注册服务可用(保证可用性)，只不过查到的信息可能不是最新的(`不保证强一致性`)

> Eureka自我保护机制

![img](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210504163136.png)

在没有`Eureka`自我保护的情况下，如果`Eureka Server`在一定时间内没有接受到某个微服务实例的心跳，`Eureka Server` 将会注销实例，但是当发生网络分区故障时，那么微服务与`Eureka Server`之间将无法正常通讯，以上行为可能变的危险，因为微服务本身其实时没有问题的，此时不应该注销整个服务，如果没有自我保护机制，那么`Eureka Server`将直接注销掉该服务。

`Eureka`通过`自我保护模式`来解决了网络分区故障的问题，当`Eureka Server`节点在短时间内丢失过多的客户端时，那么就会把整个微服务节点进行保护，一旦进入自我保护模式，`Eureka Server`就会保护服务注册表中的信息，不删除服务注册表中的数据（不注销服务）。当网络故障恢复后，`Eureka Server`节点会再自动退出自我保护模式。

`Eureka自我保护模式主要解决了网络分区故障时，不注销服务`

```yaml
#禁用eureka的自我保护模式
eureka:
  server:
    enable-self-preservation: false
```



### 集群

1. 这里就使用多配置文件的方式

 ![image-20210503224651824](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210503224946.png)

2. 然后配置本地`hosts`文件：`C:\Windows\System32\drivers\etc`

	```
	127.0.0.1       eureka8761
	127.0.0.1       eureka8762
	127.0.0.1       eureka8763
	```

3. 配置idea启动项

	```
	--spring.profiles.active=eureka8761
	--spring.profiles.active=eureka8762
	--spring.profiles.active=eureka8763
	```

	![image-20210503225857849](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210503225857.png)
	
	> 8761配置
	
	```yaml
	server:
	  port: 8761
	
	eureka:
	  instance:
	    #设置该服务注册中心的hostname
	    hostname: localhost
	  client:
	    #false 表示不需要把该服务注册到注册中心去，因为这个服务本身就是注册中心
	    register-with-eureka: false
	    # false 表示不去从服务端索取其他服务信息，因为自己就是服务端，服务注册中心本身就是维护服务实例，他不需要去索取其他服务
    	fetch-registry: false
	    service-url:
	      #指定服务注册中心的位置
	      defaultZone: http://eureka8762:8762/eureka,http://eureka8763:8763/eureka
	```
	> 8762配置
	
	```yaml
	server:
	  port: 8762
	
	eureka:
	  instance:
	    #设置该服务注册中心的hostname
	    hostname: localhost
	  client:
	    #false 表示不需要把该服务注册到注册中心去，因为这个服务本身就是注册中心
	    register-with-eureka: false
	    # false 表示不去从服务端索取其他服务信息，因为自己就是服务端，服务注册中心本身就是维护服务实例，他不需要去索取其他服务
   	fetch-registry: false
	    service-url:
	      #指定服务注册中心的位置
	      defaultZone: http://eureka8761:8761/eureka,http://eureka8763:8763/eureka
	```
	> 8763配置
	
	```yaml
	server:
	  port: 8763
	
	eureka:
	  instance:
	    #设置该服务注册中心的hostname
	    hostname: localhost
	  client:
	    #false 表示不需要把该服务注册到注册中心去，因为这个服务本身就是注册中心
	    register-with-eureka: false
    	# false 表示不去从服务端索取其他服务信息，因为自己就是服务端，服务注册中心本身就是维护服务实例，他不需要去索取其他服务
	    fetch-registry: false
	    service-url:
	      #指定服务注册中心的位置
	      defaultZone: http://eureka8761:8761/eureka,http://eureka8762:87632/eureka
	```

> 问题

1. Eureka宕机了接口还能调用嘛？

   只要服务没有挂掉，还能调用。因为服务注册到Eureka之后会被缓存到本地，所以Eureka宕机还是可以继续调用的。

### Eureka安全认证

一般情况下Eureka都会在一个内网环境中，但避免不了在某些项目中需要让其他外网服务注册到Eureka,这个时候就有必要让Eureka增加一套安全认证机制；

```xml
<!--引入security安全认证依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

配置eureka服务端

```yml
# 配置访问的账号密码
spring:
  security:
    user:
      name: myiszhb
      password: 123456
```

然后在eureka服务端编写EurekaSecurityConfig,重写configure方法，把csrf劫持置为不可以，让服务能被接收和注册

```java
@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        super.configure(http);
    }
}
```

eureka客户端配置

`账号:密码@ip:端口`

```yml
eureka:
  client:
    service-url:
      #指定服务注册中心的位置
      defaultZone: http://myiszhb:123456@127.0.0.1:8761/eureka,http://myiszhb:123456@127.0.0.1:8762/eureka,http://myiszhb:123456@127.0.0.1:8763/eureka

```



## Spring Cloud Ribbon

> Ribbon是什么？

`Ribbon`是一个基于HTTP和TCP的客户端负载均衡器，当使用`Ribbon`对服务进行访问的时候，它会扩展`Eureka`客户端的服务发现功能，实现从`Eureka`注册中心中获取服务端列表，并通过`Eureka`客户端来确定是否已经启动。`Ribbon`是在`Eureka`客户端服务发现的基础上，实现了对服务实例的选择策略，从而实现对服务的负载均衡消费。

> Ribbon 负载均衡策略

`IRule`负载均衡实现，如图：

![image-20210511153832961](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210511153842.png)



|          负载均衡实现           |                             策略                             |
| :-----------------------------: | :----------------------------------------------------------: |
|           RandomRule            |                             随机                             |
|         RoundRobinRule          |                             轮询                             |
|    AuailabilityFilteringRule    | 先过滤掉由于多次访问故障的服务，以及并发连接数超过阈值的服务，然后对剩下的服务进行轮询策略访问。 |
|    WeightedResponseTimeRule     | 根据平均响应时间计算所有服务的权重，响应时间越快服务权重越大被选择的概率越高，如服务刚启动时统计信息不足，则使用`RoundRobinRule`策略，待统计信息足够是切换回`WeightedResponseTimeRule`策略 |
|            RetryRule            | 先按照`RoundRobinRule`策略分发，如果分发到的服务不能访问，则在指定时间内进行重试，然后到分发其他可以的服务 |
|        BestAvailableRule        | 先过滤掉由于多次访问故障的服务，以及并发连接数超过阈值的服务，然后选择一个并发量最小的服务 |
| ZoneAvoidanceRule（新版本默认） | 综合判断服务节点所在区域的性能和服务节点的可用性，来决定选择那个服务器 |



它会默认调用`ILoadBalancer`类，然后选择进入`chooseServer`方法，实现是在`ZoneAwareLoadBalancer`里面，默认走的是`ZoneAvoidanceRule`规则。

![image-20210511154247406](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210511154248.png)

### 切换Ribbon的负载均衡

```java
@Configuration
private class RibbonConfig{
    
    @Bean
    public IRule iRule(){
        //采用轮询方式负载均衡
        return new RoundRobinRule();
    }
}
```

### 自定义负载均衡算法

需要继承`AbstractLoadBalancerRule`实现 其中的`choose`方法即可

```java
public class MyIRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        //具体实现
        return null;
    }
}
```



## Spring Cloud Feign

> Feign是什么？

`Fegin`是Netflix公司开发的一个声明式的REST调用客户端（`调用远程的restful风格的http接口组件`）。`Spring Cloud Feign`对`Ribbon`负载均衡进行了简化，在其基础上进行了进一步的封装，在配置上进行了简化，它是一种声明式的调用方式，它的使用方法是定义一个接口，然后在接口上添加注解，使其支持了`Spring MVC`标准注解和`HttpMessageConverters`,`Fegin`可以与`Eureka`和`Ribbon`组合使用以支持负载均衡。

> 怎么使用Fegin

所需架包

```xml
    <dependencies>
        <!-- 引入Fegin依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
```

代码如下

```java
/**开启Fegin,FeignClient("服务的名称")**/
@FeignClient("SPRINGCLOUD-SERVICE-GOODS-01")
public interface GoodsClient {

    @RequestMapping(value = "/service/goods")
    public ResultObject goods();
}
```

在消费模块启动类加上`@EnableFeignClients`开启对`Fegin`的支持

在`controller`引入`GoodsClient`,即可完成。

```java
@Resource
private GoodsClient goodsClient;

@RequestMapping(value = "/service/goods", method = RequestMethod.GET)
public ResultObject goodsFegin() {
   System.out.println("/service/goodsFegin -->8080 被执行..........");
   ResultObject goods = goodsClient.goods();
   return new ResultObject(Constant.ZERO, "查询成功", goods);
}
```



## Spring Cloud Hystrix

> Hystrix时什么？

`Hystrix`被称为熔断器，它是一个用于处理分布式系统的`延迟`和`容错`的开源库，在分布式系统里，许多服务之间通过远程调用实现信息交互，调用时不可避免会出现调用失败，比如超速、异常等原因导致调用失败，`Hystrix`能够保证在一个服务出现故障的情况下，不会导致整体服务失败，避免级联故障（服务雪崩），以提高分布式系统的弹性。

所以当某个服务单元发生故障之后，通过熔断器的故障监控，向调用方返回一个符合预期的、可处理的备选响应（`FallBack`也叫服务降级），而不是长时间的占用，从而避免了故障在分布式系统中的蔓延，甚至雪崩。

### Hystrix 基本使用

引入依赖

```xml
<!-- Hystrix依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

在springboot启动类加上`@EnableHystrix`或者`@SpringBootApplication`注解

```java
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
```

在对应的方法上加上`@HystrixCommand(fallbackMethod = "fallback")`注解，其中`fallback`是自己定义的其他方法，表示如果服务出现错误或者超时，就进去其方法。

```java
 /**
     * Hystrix 查询所有商品
     *
     * @return
     */
    @HystrixCommand(fallbackMethod = "fallback")
    @RequestMapping(value = "/service/goodsHystrix", method = RequestMethod.GET)
    public ResultObject goodsHystrix() {
        System.out.println("/service/goodsHystrix -->8080 被执行..........");
        ResultObject goods = goodsClient.goods();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ResultObject(Constant.ZERO, "查询成功", goods);
    }

    /**
     * 服务降级了
     * @return
     */
    public ResultObject fallback(){
        return new ResultObject(Constant.ONE,"服务降级了...");
    }
```

`Hystrix`和`ribbon`的超时时间配置

这里`Hystrix`和`ribbon`的超时时间都必须配置，不然他是以最小的时间算，`Hystrix`和`ribbon`的超时时间都是`1秒`

```yml
# 配置Hystrix的超时时间
hystrix:
  command:
    default: #也可以针对多个服务
      execution:
        timeout:
          enabled: true
        isolation:
          thread:
            timeoutInMilliseconds: 4000

# 配置ribbon的超时时间
ribbon:
  ReadTimeout: 6000
  ConnectTimeout: 3000
```

### Hystrix 异常处理

指定方法加上`Throwable`参数

```java
public ResultObject fallback(Throwable throwable){
     throwable.printStackTrace();
     System.out.println(throwable.getMessage());
     return new ResultObject(Constant.ONE,"服务降级了...");
}
```

### Hystrix 忽略异常

在`@HystrixCommand`,加入`ignoreExceptions = Throwable.class`

```
@HystrixCommand(fallbackMethod = "fallback",ignoreExceptions = Throwable.class)
```

### Hystrix 限流

限流就是限制某个微服务的使用量（可用线程数、信号量）

`threadPoolKey`是线程池唯一表示，`Hystrix`会使用表示来计数，看线程占用是否超过了，超过了就会直接降级该次调用。

```java
@HystrixCommand(fallbackMethod = "fallback",
        //线程池唯一标识
		threadPoolKey = "goods",
		threadPoolProperties = {
        	//可用线程数量（这里设置的是2个）
         	@HystrixProperty(name = "coreSize", value = "2"),
         	//队列 （这里设置的是1 所以可以方一个），如果第4个就限流了
          	@HystrixProperty(name = "maxQueueSize", value = "1")
   		}
)
```



### Hystrix 整合Feign

> 没有处理异常

在`@FeignClient`中加入`fallback = GoodsClientFallBack.class`,其中`GoodsClientFallBack`自定义的

```java
/**开启Fegin,FeignClient("服务的名称")**/
@FeignClient(value = "SPRINGCLOUD-SERVICE-GOODS-01",fallback = GoodsClientFallBack.class)
public interface GoodsClient {
    @RequestMapping(value = "/service/goods")
    public ResultObject goods();
}
```

`GoodsClientFallBack`实现`GoodsClient`,并实现其方法

```java
@Component
public class GoodsClientFallBack implements GoodsClient{

    //goods的备用方法，如果goods出现超时或者错误，进入此方法
    @Override
    public ResultObject goods() {
        return new ResultObject(Constant.ONE,"fegin 服务降级");
    }
}
```

最后在配置文件开启开启fegin整合hystrix

```yml
# 开启fegin整合hystrix
feign:
  hystrix:
    enabled: true
```

> 获取异常信息

在`@FeignClient`中加入`fallbackFactory = GoodsClientFallBack.class`,其中`GoodsClientFallBack`自定义的

```java
@FeignClient(value = "SPRINGCLOUD-SERVICE-GOODS-01",fallbackFactory = GoodsClientFallBack.class)
public interface GoodsClient {
    @RequestMapping(value = "/service/goods")
    public ResultObject goods();
}
```

`GoodsClientFallBack`实现`FallbackFactory<GoodsClient>`,并实现其方法

```java
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
```



### Hystrix Dashboard

Hystrix仪表盘，就像汽车的仪表盘一样，实时显示汽车的各项数据，Hystrix仪表盘主要用来监控Hystrix的实时运行状态，通过它我们可以看到Hystrix的各项信息，从而快速发现系统中存在的问题进而解决问题。

> 简单使用

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

在启动类加上`@EnableHystrixDashboard

`消费者配置`

```xml
<!-- Hystrix依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>

<!-- springboot 提供的监控 actuator-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

暴露端点

```yml
# 暴露端点
management:
  endpoints:
    web:
      exposure:
        include: hystrix.stream
```

访问：`http://localhost:8000/actuator/hystrix.stream`,如果一直出现`ping:`，就访问一下hystrix管理的接口。

![image-20210518120115126](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210518120124.png)

如果出现`Unable to connect to Command Metric Stream.`错误 可以尝试配置dashboard

```yml
hystrix:
  dashboard:
    proxy-stream-allow-list: "localhost"
```

最终结果

![image-20210518120256958](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210518120258.png)

### Hystrix Turbine

`Turbine`可以对多个服务进行监控，然后`Hystrix Dashboard`在对其进行监控。

> 简单使用

```xml
<dependencies>
	<!-- eureka依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Hystrix依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
    
    <!-- springboot 提供的监控 actuator-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- turbine 依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
    </dependency>
</dependencies>
```

配置内容

```yml
server:
  port: 3722
eureka:
  client:
    #不把服务注册到注册中心
    register-with-eureka: false
    service-url:
      #指定服务注册中心的位置
      defaultZone: http://eureka8761:8761/eureka,http://eureka8762:8762/eureka,http://eureka8763:8763/eureka

#配置turbine
turbine:
  #对那几个hystrix服务聚合汇总，多个服务逗号分隔
  app-config: springcloud-service-protal
  # 配置成：default 可能会出现报错 
  cluster-name-expression: new String('default')
```

把`Dashboard`的地址改成`http://localhost:3722/turbine.stream`

![2](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210520120547.png)



## Spring Cloud Zuul

在微服务中，一个独立的系统被拆分成很多独立的服务，为了确保安全，权限管理也是一个不可避免的问题，如果在每一个服务上都添加上相同的权限验证代码来确保系统不被非法访问，工作量就太大了，二期维护也非常不方便.Spring Cloud Zuul即可实现一套API网关服务。Zuul包含了对请求的路由和过滤。

### Zuul 路由使用

```xml
<dependencies>
    <!-- eureka客户端依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- zuul 依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
</dependencies>
```

配置文件

```yml
server:
  port: 80
spring:
  application:
    name: springcloud-server-zuul
eureka:
  instance:
    #每间隔2s，向服务端发送一次心跳，证明自己存活
    lease-renewal-interval-in-seconds: 2
    #告诉服务端，如果我10s没有像你发送心跳，就代表我故障了，将我踢掉
    lease-expiration-duration-in-seconds: 10
    #告诉服务端，服务实例以ip作为链接，而不是机器名
    prefer-ip-address: true
    #告诉服务端，服务实例的名称
    instance-id: springcloud-service-zuul
  client:
    service-url:
      #指定服务注册中心的位置
      defaultZone: http://eureka8761:8761/eureka,http://eureka8762:8762/eureka,http://eureka8763:8763/eureka
```

启动项目，然后我们就可以通过`http://127.0.0.1/springcloud-service-protal/service/goodsFeginHystrix`去访问接口了

`http://127.0.0.1`（zuul服务本身）

`/springcloud-service-protal`(调用的服务名称)

`/service/goodsFeginHystrix`（接口地址）

### Zuul 路由规则

```yml
zuul:
  # zuul的超时时间
  host:
    connect-timeout-millis: 5000
  # zuul 路由规则
  routes:
    portal:
      # 服务名称
      service-id: springcloud-service-protal
      path: /portal/**
  # zuul 忽略某个服务名，禁止通过服务名称调用
  ignored-services: springcloud-service-protal 
  # *号表示禁止所有服务名调用
  #ignored-services: '*'
  #给所有经过zuul网关接口加一个访问权限，比如 http://127.0.0.1/api/portal/service/goodsFeginHystrix
  #prefix: /api
  # 禁止访问的路由
  ignored-patterns: /**/feign/**
```

然后就可以通过`http://127.0.0.1/portal/service/goodsFeginHystrix`去访问，因为配置了忽略服务名，上面那种方式就服务在访问了

### Zuul 过滤器

过滤器可以做限流、权限验证、记录日志等，过滤器（filter）是zuul的核心组件，zuul中定义了4中标准过滤器类型，这些过滤器对应于请求的典型生命周期。

`PRE`:这种过滤器在请求被路由之前调用，可以利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等；

`ROUTING`:这种过滤器将请求路由到微服务，这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Ribbon请求微服务；

`POST`：这种过滤器在路由到微服务之后执行，这种过滤器可用来为响应添加标准的Http Header、收集统计信息和指标、将响应从微服务发送给客户端登；

`ERROR`：在其他阶段发生错误时执行该过滤器

### 自定义过滤器

```java
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
     * 在路由时执行run方法 具体逻辑实现
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
```

如果有多个过滤器我们可以通过配置文件的方式来更改它的状态

```yml
zuul:
  #过滤器名称
  LogFilter:
    route:
      disable: true
```

### Zuul 异常处理

首先先禁用掉Zuul自带的`SendErrorFilter`过滤器

```yml
zuul:
  #过滤器名称
  SendErrorFilter:
    route:
      disable: true
```

### Zuul 熔断

在zuul服务里面创建熔断,并实现`FallbackProvider`

```java
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

```



## Spring Cloud Config

>  什么是配置中心？

1.传统配置方式：配置信息分散到系统各个角落，配置文件和代码中。

2.集中式配置中心：将应用系统中对配置信息的管理作为一个新的应用模块，进行集中管理，并且提供额外功能。

3.分布式配置中心：在分布式、微服务架构中，独立的配置中心服务。

> 为什么需要分布式配置中心？

在分布式微服务体系中，服务的数量以及配置信息日益增多，比如个在服务器参数配置、各种数据库访问参数配置、各种环境下配置信息的不同、配置信息修改之后实时生效等，传统的配置文件方式或者将配置信息存放于数据库中的方式就无法满足开发人员对配置管理的要求 例如：

安全性：配置跟随源代码在代码库中，容易造成配置泄露；

时效性：修改配置，需要重启服务才能生效；

局限性：无法支持多态调整，比如日志开关、功能开关；

> 常用的分布式配置中心框架

`Apollo`：携程框架部门研发的分布式配置中心，能够集中化管理应用不同环境、不同集群的配置，配置修改后能够实时推送到应用端，并且具备规范的权限、流程治理等特性，适用于微服务配置管理场景；

`diamond`:淘宝开源的持久配置中心，支持各种持久信息（比如各种规则，数据库配置等）的发布和订阅；（更新稍微落后）

`XDiamond`:全局配置中心，存储应用的配置项目，解决配置混乱分散的问题；

`Qconf`：奇虎360内部分布式配置管理工具，用来代替传统的配置文件，使得配置信息和程序代码分离，同时配置变化能够实时同步到客户端，而且保证用户高效读取配置；

`Disconf`：百度的分布式配置管理平台；

`Spring Cloud Config`：Spring Cloud微服务开发的配置中心，提供服务端和客户端支持；

`Spring Cloud alibaba nacos`：Spring Cloud Alibaba下的分布式配置中心;

> 什么是Spring Cloud Config?

Spring Cloud Config是一个解决分布式系统的配置管理方案，它包含Client和Server两部分，Server提供配置文件的存储、以接口的形式将配置文件的内容提供出去，Client通过接口获取数据、并依据此数据初始化自己的应用。Spring Cloud使用git或者svn、也可以是本地存放配置文件，默认情况下使用git；

> Spring Cloud Config工作原理



![image-20210615135728500](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210615135737.png)

1、首先需要一个远程Git仓库，平时测试可以使用GitHub，在实际生成环境中，需要自己搭建一个Git服务器，远程Git仓库的主要作用是用来保存我们的配置文件；

2、除了远程Git仓库之外，我们还需要一个本地Git仓库，每当`Config Server`访问远程Git仓库时，都会克隆一份到本地，这样当远程仓库无法连接时，就直接使用本地存储的配置信息；

3、微服务A、微服务B则是我们的具体应用，这些应用在启动的时候会从`Config Server`中获取相应的配置信息；

4、当微服务A、微服务B尝试从Config Server中加载配置信息的时候，`Config Server`会先通过`git clone`命令克隆一份配置文件到本地保存；

5、由于配置文件是存储在Git仓库中，所以配置文件天然具有版本管理功能；

### 构建服务端

1、创建一个远程仓库[GitHub](https://github.com/)或者[码云](https://gitee.com/),我们吧配置文件上传上去

![](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210616160730.png)

2、搭建SpringCloud Config服务

引入依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

`@EnableConfigServer`开始config

```java
@Slf4j
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
        log.info("Spring Cloud Config服务 已启动，端口：8888");
    }
}
```

配置文件

```yml
server:
  port: 8888
spring:
  application:
    name: springcloud-service-config
  cloud:
    config:
      server:
        git:
          # 仓库地址
          uri: https://github.com/my-zhb/SpringCloud-RemotelyConfig.git
          # 仓库目录
          search-paths: config-server
          # 仓库账号
          username: myiszhb@gmail.com
          # 仓库密码
          password: 123456
```



### Config映射规则

{applcation} 表示配置文件的名字，对应的配置文件即applicaton；

{profile} 表示环境，有dev、test、online及默认；

{label} 表示分支，默认放在master分支；

```tex
第一种：
/{application}/{profile}[/{label}]/
http://localhost:8888/application/dev/master

第二种：
/{application}-{profile}.properties
http://localhost:8888/application-dev.yml

第三种：
/{label}/{application}-{profile}.properties
http://localhost:8888/master/application-dev.yml
```



### 构建客户端

引入包

```xml
<!-- 引入spring cloud config 客户端-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

在`resources`目录下创建`bootstrap.(properties或者yml)`,文件名称必须是`bootstrap`

```yml
spring:
  cloud:
    config:
      profile: dev
      #分支
      label: master
      #地址
      uri: http://localhonst:8888/
```

`bootstrap.yml`文件，SpringCloud有一个“引导上下文”的概念，这是主应用程序的父上下文。引导上下文负责从配置服务器加载配置属性，以及解密外部配置文件中的属性和主应用程序加载application中的属性不同，引导上下文加载（bootstrap）中的属性，配置bootstrap.*中的属性有更高的优先级，因此默认情况下它们不能本本地配置覆盖；







### Config信息加密







### Config动态刷新

#### 局部刷新

Spring boot的actuator提供了一个刷新端点/refresh

```xml
<!-- springboot 提供的监控 actuator-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

在远程文件配置暴露端点

```yml
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

在resources创建bootstrap.yml配置config

```yml
spring:
  cloud:
    config:
      profile: dev
      label: master
      uri: http://127.0.0.1:8888/
```

在有引用配置的类上加上注解`@RefreshScope`

```java
@RefreshScope
@RestController
public class GoodsController {

    @Value("${info.address}")
    private String address;
    
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public Object config() {
        return address;
    }
}
```

进行测试，如果修改了远程文件需要执行`ip:端口/actuator/refresh`，新的配置才会生效，这方法不建议使用，因为很麻烦；

#### 全局刷新

Spring Cloud Bus就可以实现配置的自动刷新，Spring Cloud Bus使用轻量级的消息代理/总线（例如RaboitMQ,Kafka等）广播状态的更改（例如配置的更新）或者其他的管理指令，可以将Spring Cloud Bus想象成一个分布式的Spring Boot Actuator;

![image-20210621144531140](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210621144539.png)

> 配置springcloud bus

引入依赖

```xml
   <!-- 引入springcloud bus-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
<!-- 引入spring-boot-starter-actuator-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

bootstrap.yml配置

```yml
spring:
  application:
    name: springcloud-service-config
  cloud:
    config:
      server:
        git:
          # 仓库地址
          uri: https://github.com/my-zhb/SpringCloud-RemotelyConfig.git
          # 扫描仓库目录
          search-paths: config-server/goods,config-server/portal
          # 仓库账号
          username: myiszhb@gmail.com
          # 仓库密码
          password: 123456
    # 开启spring cloud bus，默认开启
    bus:
      enabled: true
  # 配置rabbitmq
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: myiszhb
    password: 123456
# 打开所有的web访问端点
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

配置客户端

```xml
<!-- 引入spring cloud config 客户端-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<!-- 引入springcloud bus-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
<!-- springboot 提供的监控 actuator-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

bootstrap.yml配置

```yml
server:
  port: 8001
spring:
  cloud:
    config:
      profile: devportal
      label: master
      uri: http://127.0.0.1:8888/
```

git远程服务配置

```yml
spring:
  application:
    name: springcloud-service-protal
  # 配置rabbitmq
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: myiszhb
    password: 123456

# 暴露端点
management:
  endpoints:
    web:
      exposure:
        include: '*'

# 测试局部动态刷新
info:
  address: myiszhb333
```

我们修改了配置文件通过调用`http://127.0.0.1:8888/actuator/bus-refresh`链接进行刷新,端口是你config服务器的端口。

### Config高可用

有了配置中心之后，其他的微服务都是从配置中心上读取配置信息，此时配中心就至关重要了，在真实的项目环境中，Spring Cloud Config配置中心难免会出现各种问题，此时就需要考虑Spring Cloud Config的高可用机制；

Spring Cloud Config的高可用机制解决方式，把Spring Cloud Config注册到Eureka就可以了，此时用户访问的时候不是直接从配置中心获取配置信息，而是先通过eureka中获取配置中心的地址，然后在从配置中心获取具体服务的配置信息；

> 简单使用

config 服务端搭建

配置N份application-config8887.yml，application-config8888.yml，application-config8889.yml 端口以及`instance-id`对应更改。

```yml
server:
  port: 8887
spring:
  application:
    name: springcloud-service-config
  cloud:
    config:
      server:
        git:
          # 仓库地址
          uri: https://gitee.com/myiszhb/spring-cloud-remotely-config.git
          # 仓库目录
          search-paths: config-server/goods,config-server/portal
          # 仓库账号
          username: myiszhb@gmail.com
          # 仓库密码
          password: 123456
    # 开启spring cloud bus，默认开启
    bus:
      enabled: true
  # 配置rabbitmq
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: myiszhb
    password: 123456
# 打开所有的web访问端点
management:
  endpoints:
    web:
      exposure:
        include: '*'
eureka:
  instance:
    #每间隔2s，向服务端发送一次心跳，证明自己存活
    lease-renewal-interval-in-seconds: 2
    #告诉服务端，如果我10s没有像你发送心跳，就代表我故障了，将我踢掉
    lease-expiration-duration-in-seconds: 10
    #告诉服务端，服务实例以ip作为链接，而不是机器名
    prefer-ip-address: true
    #告诉服务端，服务实例的名称 唯一
    instance-id: springcloud-service-config-8887
  client:
    service-url:
      #指定服务注册中心的位置
      defaultZone: http://127.0.0.1:8761/eureka,http://127.0.0.1:8762/eureka,http://127.0.0.1:8763/eureka
```

config 客户端配置

这里是bootstrap的配置

```yml
server:
  port: 8001
spring:
  cloud:
    config:
      profile: devportal
      # 分支
      label: master
      #      uri: http://127.0.0.1:8888/
      # 通过注册中心去拿
      discovery:
        enabled: true
        # 服务的名称
        service-id: springcloud-service-config
eureka:
  instance:
    #告诉服务端，服务实例的名称
    instance-id: springcloud-service-protal-01
  client:
    service-url:
      #指定服务注册中心的位置
      defaultZone: http://127.0.0.1:8761/eureka,http://127.0.0.1:8762/eureka,http://127.0.0.1:8763/eureka
```

搭建完毕

![image-20210621155246732](https://cdn.jsdelivr.net/gh/my-zhb/CDN/img/20210621155248.png)

`还可以使用nginx进行高可用配置`

### Config安全认证

在config服务端添加依赖

```yml
<!--引入security安全认证依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

config服务端配置文件

```yml
spring:
  # 配置安全认证
  security:
    user:
      name: myiszhb
      password: 123456
```

然后配置客户文件

```yml
spring:
  cloud:
    config:
      # 配置访问config的账号密码
      username: myiszhb
      password: 123456
```

配置

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 高版本的丢弃了
     *
     * security:
     *   basic:
     *    enabled: true
     *
     * 配置，应该使用以下方式开启
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configure HttpSecurity as needed (e.g. enable. http basic).
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.csrf().disable();
        //注意：为了可以使用 http://${user}:${password}@${host}:${port}/eureka/ 这种方式登录,所以必须是httpBasic,
        // 如果是form方式,不能使用url格式登录
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
```

这样客户端去配置中心取文件就需要验证,刷新配置为`http://username:password@ip:port/actuator/bus-refresh`



## Spring Cloud Sleuth



