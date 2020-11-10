配置类

## swagger配置

-----


```java
@ConfigurationProperties(prefix = "swagger")
@Configuration//必须存在
@EnableSwagger2//必须存在
@EnableWebMvc//必须存在
@Data
//必须存在 扫描的API Controller包
@ComponentScan(basePackages = {"com.kermi.springboot.controller"})
public class SwaggerConfig {

    private String title;
    private String description;
    private String version;

    private String name;
    private String url;
    private String email;

    @Bean
    public Docket customDocket(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        Contact contact = new Contact(name,url,email);
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .contact(contact)
                .version(version)
                .build();
    }
}
```

## MVC配置

-----

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

```yaml
swagger:
  title: SpringBootApi
  description: SpringBootApi
  version: 1.0.0
  name: kermi
  url: kermi.xyz
  email: test@test.com
```

<http://localhost:8080/swagger-ui.html#/>访问该页即可

