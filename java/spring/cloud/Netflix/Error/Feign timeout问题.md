# 解决springcloud Feign项目中遇到的timeout请求超时的问题

解决：

- 方案一：

```yaml
 hystrix:
  command:
    default:
      execution:
        timeout:
          enable: fales       #解决超时问题（方案一：关闭请求时间限制）
```

- 方案二：

```yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000  #解决超时问题（方案二：延长请求时间）
```

- 方案三

```yaml
feign:
  hystrix:
    enabled: false             #解决超时问题（方案三：关闭Feign的hystrix）
```

