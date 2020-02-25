导入swaggerUI依赖

```xml
<!--swagger依赖-->
        <!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>

<!--SwaggerUI-layui-->
        <!-- https://mvnrepository.com/artifact/com.github.caspar-chen/swagger-ui-layer -->
        <!--<dependency>
            <groupId>com.github.caspar-chen</groupId>
            <artifactId>swagger-ui-layer</artifactId>
            <version>1.1.3</version>
        </dependency>-->
```

注册配置类

```java
package com.kermi.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     *
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                // 为扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.kermi.datacheck.controller")).paths(
                        PathSelectors.any())
                .build();
    }

    /**
     * api文档的详细信息函数,注意这里的注解引用的是哪个
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // //大标题
                .title("RESTful API")
                // 版本号
                .version("1.0")
//                .termsOfServiceUrl("NO terms of service")
                // 描述
                .description("API 描述")
                //作者
                .contact(new Contact("Kermi", "http://localhost:8090", "test"))
//                .license("The Apache License, Version 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }
}

```

改造控制类

```java
@Api(value = "UserController", description = "用户登入登出接口")
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string")
    })
    @RequestMapping("/login")
    @ResponseBody
    public ResResult login(@RequestParam("email") String email,@RequestParam("pwd") String pwd) {
        //TODO 用户登录
        return null;
    }
}
```

pojo改造

```java
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
 
/**
 * @author Cash Zhang
 * @version v1.0
 * @since 2019/05/22 09:33
 */
@Data
public class User {
  /**
   * ApiModelProperty()用于方法，字段 表示对model属性的说明或者数据操作更改
   * value–字段说明
   * name–重写属性名字
   * dataType–重写属性类型
   * required–是否必填
   * example–举例说明
   * hidden–隐藏
   */
  @ApiModelProperty(value="ID",name="id",example="1")
  private Long id;
 
  @ApiModelProperty(value="用户名",name="username",example="zhangxin")
  private String username;
 
  @ApiModelProperty(value="密码",name="password",example="123456")
  private String password;

```

