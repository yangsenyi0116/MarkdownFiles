### springmvc配置文件名字的问题

默认情况下是用dispatherServlet的名字当做命名空间

[servletName]-servlet.xml(WEB-INF)之下寻找

[servletName]-servlet.xml=namespace.xml

如果非要重新使用另外一个名字	

```xml
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <!--可以重新声明配置文件的名字-->  
    <!--<init-param>-->
          <!--<param-name>namespace</param-name>-->
          <!--<param-value>mvc</param-value>-->
      <!--</init-param>-->
    <!--重新声明配置文件路径-->
      <init-param>
          <param-name>contextConfigLocation</param-name>
          <param-value>classpath:springmvc.xml</param-value>
      </init-param>
  </servlet>
```

普通web项目，

maven项目 配置文件都在resources文件夹下



springmvc.xml配置如下

```xml
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <!--<init-param>-->
          <!--<param-name>namespace</param-name>-->
          <!--<param-value>mvc</param-value>-->
      <!--</init-param>-->
      <init-param>
          <param-name>contextConfigLocation</param-name>
          <param-value>classpath:springmvc.xml</param-value>
      </init-param>
  </servlet>
    
    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!--统一写/-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```





### 视图解析器

springmvc支持多种视图技术

- jsp

- freemaker（模板技术）

  

  

  内部的资源视图解析器

  - 视图前缀
    - /jsp/ 它是我们请求响应的资源的路径的配置， viewName: [..]
  - 后缀
    -  .jsp 此时我们的前缀+视图名称+后缀=/jsp/xxx.jsp

物理视图由逻辑视图转换而来

物理视图是 webapp/jsp/xxx.jsp



逻辑视图

- prefix
- logicViewName
- suffix

p View = prefix + logicViewName + suffix



### 控制器的解释

```java
public interface Controller {
    ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```



```java
//控制器
public class HelloController implements Controller {
    //继承controller类

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("girl","ss");
        mav.setViewName("girl");
       	//初始化视图控制层
        return mav;
    }
}
```



servlet doGet() doPost()返回值类型为void

springmvc设计为ModelAndView

在model、中填充数据，然后再具体的视图进行找事

还需要在配置文件中配置bean，name（Required）是请求的路径

它就处理一个请求



### 注解开发模式

- @Controller
- @RequestMapping



开发步骤

1. 配置基础扫描的包，这样的注解才会声响
2. 在指定的二类上边添加@controller注解
3. 添加@RequestMapping类似于前面controller的那个名字(不同requesthandler处理的HandlerMapping)



协商Controller之后，标记了他为spring的一个控制器的组件，此时我们的handlermapping回去扫描这个controller时候与之匹配，如果匹配就把这里处理的工作交给他。



匹配的规则：

​	具体的匹配就是通过请求的路径进行匹配的

​	@requestMapping(URI)

​	此时就是通过这个URI进行匹配





@RequestMapping

可以写在方法上

类上（推荐用二者结合的方式）

```java
@Controller
@RequestMapping("/bye")
public class ByeController {

    @RequestMapping("/bye")
    //返回值类型为string， string就是逻辑的视图名称
    public String bye(Model model){
        model.addAttribute("model","modeller");
        //这里return的是viewName /jsp/bye.jsp
        return "bye";
    }

    @RequestMapping("/goodBye")
    public String goodBye(Model model){
        model.addAttribute("boy","boy");
        return "bye";
    }
}
```



### 转发和重定向

- 转发到页面
- 重定向到页面: redirect:path
- 转发到另一个控制器: forward:path



### 关于springmvc访问web元素

- request
- session
- application



### 注解详解

@RequestMapping

- value写的是路径，是一个数组的形式，可以匹配多个路径
- path 是value的别名，与value作用一样
- method 限定请求类型 RequestMethod.(...),如果没有限定，则什么请求都可以 比如get post 可以写成一个数组的形式
- params可以去指定参数，限定参数的特征，数组的形式
- headers 能够影响浏览器的行为
- consumers 消费者，媒体类型，可以限定必须为application/json;charset=UTF-8
- produces 产生响应的类型



### 关于请求路径的问题

springmvc支持ant风格

- ? 任意的字符 ·/·除外
- ·* 0到n个 任意个字符都可以
- ** 支持任意层路径

### @GetMapping，@PostMapping

requestmapping 里面的method指定为GET、POST



### 对于非get post请求

对于非get post请求的支持，需要有额外的内容添加 要增加一个过滤器来额外处理

- 过滤器

  ```xml
  <!--注册一个支持所有请求的过滤器-->
     <filter>
          <filter-name>hiddenHttpMethodFilter</filter-name>
          <!--<filter-class>org.springframework.web.filter.reactive.HiddenHttpMethodFilter</filter-class>-->
          <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
      </filter>
      <filter-mapping>
          <filter-name>hiddenHttpMethodFilter</filter-name>
          <url-pattern>/*</url-pattern>
      </filter-mapping>
  ```

  

表单提交里面要加一个影藏的参数

```html
<input type="hidden" name="_method" value="DELETE">
```





###  静态资源处理问题

由于servlet设置了URL匹配方式为/ 所以，它将静态资源也当作一个后台的请求，它尝试去汽配一个static/css/index.css的Controller里面的RequestMapping的组合，因为没有所以404，解决方式很多，最简单的是让springmvc单独处理，将这些交给容器的默认servlet处理，就不让DispathcherServlet来处理了

在springmvc.xml中设置默认的servlet处理者

```xml
<!--默认的servlet处理者-->
    <mvc:default-servlet-handler/>
<!--让原有的controller生效-->
    <mvc:annotation-driven/>
```

MIME类型

用过映射关系描述，一一对应编写规则

```xml
 <!--配置静态资源路径-->
    <mvc:resources mapping="/static/*" location="static/css/"/>
```

自行在web.xml定义映射规则



### @PathVariable

restful风格

```java
@Controller
@RequestMapping("/product")
public class ProductController {

    @PostMapping("/add/{id}/{name}/{price}")
    public String addProduct(@PathVariable("id")Integer id,@PathVariable("name")String name,@PathVariable("price")Double price){

        return "forward";
    }
}
```



### @ResponseBody

返回数据，一般情况下返回JSON数据

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @PutMapping("/put")
    @ResponseBody
    public String put(String name){
        System.out.println(name);
        return "OK";
    }
}
```



### @ModelAttribute

#### 使用方式一

```JAVA
@Controller
@RequestMapping("/user2")
public class UserController2 {

    //就是在controllerl里面的任意一个处理具体的方法之前都会执行
    @ModelAttribute
    public User init(){
        System.out.println("init");
        User u = new User();
        u.setName("issss");
        return u;
    }

    @RequestMapping("/login")
    public String login(Model model){
        System.out.println(model.containsAttribute("u"));
        System.out.println(model.containsAttribute("user"));
        System.out.println(model.containsAttribute("ssssss"));
        return "msg";
    }
}
```

如果某些对象从头到尾每次请求中都要存在，不消失就适合用ModelAttribute

#### 使用方式二

```java
@ModelAttribute
public void init(Model model){
    System.out.println("init");
    User user = new User();
    user.setName("issss");
    model.addAttribute("user",user);
}
```

```java
//该方法会直接去模型里面找
@RequestMapping("/login")
public String login(@ModelAttribute User user){
    System.out.println(user.getName() + user.getPassword());
    return "msg";
}
```

如果没有传递模型过来，那么方法上加了@modelattribute就为你提供 	，如果传了就用传的

### @sessionAttribute

要求当前这次会话的访问里面必须有某个对象

```java
@ResponseBody
public String put(@SessionAttribute("user") User user){
    System.out.println(user.getName() + user.getPassword());
    return "OK";
}
```

### @sessionAttributes

用在类上边，它会自动将模型填充到会话里面去

### @CookieValue

```java
@Controller
@RequestMapping("cookie")
public class CookieController {
    @RequestMapping("c1")
    public String c(@CookieValue("JSESSIONID") String jessionid){
        System.out.println(jessionid);
        return "msg";
    }
}
```

### @RequestParam

### @RequestBody

json数据，不是通过form表单传递

通过ajax({data:

})

### @InitBinder

做数据转换

### @PathVariable

restful风格

通过名字绑定这个路径里面的变量名

```java
@PostMapping("/add/{id}/{name}/{price}")
public String addProduct(@PathVariable("id")Integer id,@PathVariable("name")String name,@PathVariable("price")Double price){

    return "forward";
}
```

### 关于post请求中文乱码问题

添加过滤器即可，springmvc添加了字符过滤器

```xml
<filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <!--指定字符码-->
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    
    <init-param>
        <param-name>forceRequestEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>

<filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```



### 关于form表单提交数据的方式

#### 方式一 通过属性名字绑定

通过属性名称进行绑定，可以完成数据传递.

页面当中表单元素的name要和后台的形参的名字保持一致

如果有多个，多个形参按名字绑定即可



```java
@Controller
@RequestMapping("/user")
public class UserController {
    @PutMapping("/put")
    @ResponseBody
    public String put(String name,String password){
        System.out.println(name + password);
        return "OK";
    }
}
```



#### 方式二 利用 @RequestParam

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @PutMapping("/put")
    @ResponseBody
    public String put(@RequestParam("name") String name, @RequestParam("password") String password){
        System.out.println(name + password);
        return "OK";
    }
}
```



#### 方式三 直接使用pojo方式传递

 

```java
@PutMapping("/put")
@ResponseBody
public String put(User user){
    System.out.println(user.getName() + user.getPassword());
    return "OK";
}
```



### 获取前台传过来的时间

```java
@InitBinder
public void init(WebDataBinder dataBinder){
    //传过来的name要和pojo里面的值名称一致
    //这里指定什么格式，前台就只能传什么格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    sdf.setLenient(false);
    dataBinder.registerCustomEditor(Date.class,new CustomDateEditor(sdf,false));
}
```

在属性上写额外的注解

```java
@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date birth;
```