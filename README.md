# CashierSystem
> 基于SpringBoot+MyBatis-plus的商户系统  
> 购物车、订单流程、商品管理、订单管理、会员管理、权限管理。


## Features
- [x] redis和cookie实现购物车
- [x] 雪花算法的分布式id生成器
- [x] alipay
- [x] 单点登录
- [x] redis缓存
- [x] canal&rocketmq实现缓存和数据库间的数据最终一致性
- [x] 布隆过滤器缓存击穿
- [ ] k8s、istio支持
- [ ] Elasticsearch搜索系统
- [ ] Flink数据处理


## 环境搭建

### 搭建步骤

> 本地环境搭建
- rocketmq
  ```
  sh bin/mqnamesrv
  sh bin/mqbroker -n 127.0.0.1:9876 autoCreateTopicEnable=true -c conf/broker.conf
  ```
- zookeeper
- mysql
- canal  
	./canal.deployer-1.1.4/bin/startup.sh  
 	[conf file](https://github.com/tiyh/CashierSystem/tree/master/conf/canal)
- redis  
	1. [RedisBloom](https://github.com/RedisBloom/RedisBloom)
	```
	make
	chown redis:redis redisbloom.so  
	redis-server --loadmodule /path/to/redisbloom.so  
	//redis-server /etc/redis/redis.conf  
	//loadmodule /path/to/redisbloom.so
	```
