## json数据交互

额外依赖

```xml
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.9.3</version>
    </dependency>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.9.3</version>
    </dependency>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.9.3</version>
    </dependency>

    <dependency>
      <groupId>net.sf.json-lib</groupId>
      <artifactId>json-lib</artifactId>
      <version>2.4</version>
      <classifier>jdk15</classifier>
    </dependency>


    <!--添加处理json为javabean-->
    <dependency>
      <groupId>org.codehaus.jackson</groupId>
      <artifactId>jackson-core-asl</artifactId>
      <version>1.9.2</version>
    </dependency>

    <dependency>
      <groupId>org.codehaus.jackson</groupId>
      <artifactId>jackson-mapper-asl</artifactId>
      <version>1.9.2</version>
    </dependency>
  </dependencies>
```

​	另外记得添加

```xml
<!--激活springmvc消息转换功能-->
<mvc:annotation-driven />
```





## JSON数据返回前台以及如何解析
### JSON后台解析

用@RestController可以不写下面的@ResponseBody

1. 返回pojo

   ```java
   @RequestMapping("/m1")
       @ResponseBody   //这个注解将知道当前返回的不是视图，会将数据转换成json格式
       public User m1(){
           User u = new User();
           u.setName("sss");
           u.setPwd("asdasdsadsa");
           return u;
       }
   ```

   

2. 返回Map

   ```java
   @RequestMapping("/m2")
       @ResponseBody
       public Map<String,Object> m2(){
           Map<String,Object> map = new HashMap<>();
           map.put("name","ssss");
           map.put("age",28);
           return map;
       }
   ```

   

3. 返回数组

   ```java
       @RequestMapping("/m3")
   //    @ResponseBody
       public User[] m3(){
           User u1 = new User();
           u1.setName("a");
           u1.setPwd("asdsads");
           User u2 = new User();
           u2.setName("b");
           u2.setPwd("sssssss");
           return new User[]{u1,u2};
       }
   ```

   

4. 返回list

### JSON前台解析

```html
<script>     
$(function () {
            $("#b1").click(function () {
                $.ajax({
                    url: '${ctx}/json/m1',
                    type: 'post',
                    success:function (data) {
                        alert(data.name);
                    }
                })
            })
        })
</script>
```



## JSON数据如何使用ajax提交到后台，后台如何解析

前台写法

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="${ctx}/static/plugs/jquery/jquery-3.3.1.js"></script>
</head>
<body>

    <button id="b1">发送一个User对象到后台，并且以Ajax方式发送到后台</button>

    <script>
        $(function () {
            $("#b1").click(function () {
                var obj = {
                    'name':'ssss',
                    'pwd':'aaaa'
                };
                $.ajax({
                    url:'${ctx}/json2/add',
                    type:'post',
                    contentType:'application/json',
                    data:JSON.stringify(obj),
                    success:function (data) {

                    }
                })
            })
        })
    </script>
</body>
</html>
```

后台写法

```java
@Controller
@RequestMapping("/json2")
public class JsonController2 {

    @RequestMapping("/add")
    public String add(@RequestBody User user){
        System.out.println(user.getName() + user.getPwd());
        return "msg";
    }
}
```

一定要加@RequestBody

### 关于Form提交数据与Ajax兹定于JSON数据提交的区别

form提交方式请求的是一个form表单

通过ajax祖卓的json数据发送分析

![1553949456713](C:\Users\vip87\AppData\Roaming\Typora\typora-user-images\1553949456713.png)

所以二者发送组装数据的区域存在不同，所以处理方式也不同

对于form表单提交数据他的contentType是属于Content-type: application/x-www-form-urlencoded

对于ajax发送json数据测试application/json.

## XML数据交互

对于很多第三方开发，很多会采用XML作为数据交互，比如微信

1. 添加依赖

   ```xml
   <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml -->
   <!--XML数据处理-->
   <dependency>
         <groupId>com.fasterxml.jackson.dataformat</groupId>
         <artifactId>jackson-dataformat-xml</artifactId>
         <version>2.9.8</version>
   </dependency>
   ```

   

2. 方法返回数据类型定义

   ```java
   @RequestMapping(value = "/m1",produces = {MediaType.APPLICATION_ATOM_XML_VALUE})
       @ResponseBody
       public User m1(){
           User u = new User();
           u.setName("ssss");
           u.setPwd("aaa");
           return u;
       }
   ```

   