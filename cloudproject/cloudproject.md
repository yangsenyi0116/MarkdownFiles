> 电商秒杀系统

框架技术

- SpringCloud
- SpringBoot
- Spring

数据库相关

- Mysql
- RabbitMQ消息队列，用来秒杀时的削峰和分流
- Redis/ElasticSearch

前后端交互

- SwaggerUI



## 第一版（15天）

普通的(邮箱)登录以及验证

JWT/RedisSession

商品的的CRUD



## 第二版（15天）

用户的授权

购物车的本地存储和服务器存储

购物车内商品的CRUD

商品的购买（伪购买）



## 第三版(20天)

加入秒杀系统



## 第四版（15天）

扫码登录/短信登录的加入

权限控制



## 第五版

引入SpringPay



## 第六版

建立高可用的Eureka和SpringCloudConfig

迁移至SpringCloud Alibaba



## 微服务的划分

EurekaServer 服务注册中心

ConfigCenter配置文件中心

Base基础模块

Common 通用模块（ResponseBody,ErrorBody,IdWorker）



- 登录
- 商品
- 购物车
- 秒杀任务



- 商品评论