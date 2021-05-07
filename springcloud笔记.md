# SpringCloud Netflix

## Eureka

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
	
	



## Ribbon

> Ribbon是什么？

`Ribbon`是一个基于HTTP和TCP的客户端负载均衡器，当使用`Ribbon`对服务进行访问的时候，它会扩展`Eureka`客户端的服务发现功能，实现从`Eureka`注册中心中获取服务端列表，并通过`Eureka`客户端来确定是否已经启动。`Ribbon`是在`Eureka`客户端服务发现的基础上，实现了对服务实例的选择策略，从而实现对服务的负载均衡消费。