pom

```xml
<dependency>
  <groupId>com.github.penggle</groupId>
  <artifactId>kaptcha</artifactId>
  <version>2.3.2</version>
</dependency>
```

配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--<bean id="captchaProducer" class="com.google.code.kaptcha.impl.DefaultKaptcha">-->
        <!--<property name="config">-->
            <!--<bean class="com.google.code.kaptcha.util.Config">-->
                <!--<constructor-arg>-->
                    <!--<props>-->
                        <!--<prop key="kaptcha.border">yes</prop>-->
                        <!--<prop key="kaptcha.border.color">105,179,90</prop>-->
                        <!--<prop key="kaptcha.textproducer.font.color">black</prop>-->
                        <!--<prop key="kaptcha.image.width">125</prop>-->
                        <!--<prop key="kaptcha.image.height">45</prop>-->
                        <!--<prop key="kaptcha.textproducer.font.size">45</prop>-->
                        <!--<prop key="kaptcha.session.key">code</prop>-->
                        <!--<prop key="kaptcha.textproducer.char.length">4</prop>-->
                        <!--<prop key="kaptcha.textproducer.font.names">宋体,楷体,微软雅黑</prop>-->
                    <!--</props>-->
                <!--</constructor-arg>-->
            <!--</bean>-->
        <!--</property>-->
    <!--</bean>-->

    <bean id="kaptchaProducer" class="com.google.code.kaptcha.impl.DefaultKaptcha">
        <property name="config">
            <bean class="com.google.code.kaptcha.util.Config">
                <constructor-arg>
                    <props>
                        <!-- 验证码宽度 -->
                        <prop key="kaptcha.image.width">138</prop>
                        <!-- 验证码高度 -->
                        <prop key="kaptcha.image.height">28</prop>
                        <!-- 生成验证码内容范围 -->
                        <prop key="kaptcha.textproducer.char.string">0123456789AKWUEHPMRX</prop>
                        <!-- 验证码个数 -->
                        <prop key="kaptcha.textproducer.char.length">5</prop>
                        <!-- 是否有边框 -->
                        <prop key="kaptcha.border">no</prop>
                        <!-- 边框颜色 -->
                        <prop key="kaptcha.border.color">105,179,90</prop>
                        <!-- 边框厚度 -->
                        <prop key="kaptcha.border.thickness">1</prop>
                        <!-- 验证码字体颜色 -->
                        <prop key="kaptcha.textproducer.font.color">black</prop>
                        <!-- 验证码字体大小 -->
                        <prop key="kaptcha.textproducer.font.size">25</prop>
                        <!-- 验证码所属字体样式 -->
                        <prop key="kaptcha.textproducer.font.names">楷体</prop>
                        <!-- 干扰线颜色 -->
                        <prop key="kaptcha.noise.color">black</prop>
                        <!-- 验证码文本字符间距 -->
                        <prop key="kaptcha.textproducer.char.space">8</prop>
                        <!-- 图片样式 :阴影-->
                        <prop key="kaptcha.obscurificator.impl">com.google.code.kaptcha.impl.ShadowGimpy</prop>
                    </props>
                </constructor-arg>
            </bean>
        </property>
    </bean>

</beans>
```



`captcha.xml`

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
    <comment>Temporary Properties</comment>
    <!-- 图片边框，合法值yes，no，默认值yes -->
    <entry key="kaptcha.border">no</entry>
    <!-- 边框颜色，合法值rgb(and optional alpha)或者 white,black,blue，默认值black -->
    <entry key="kaptcha.border.color">blue</entry>
    <!-- 边框厚度，合法值>0,默认值为1 -->
    <entry key="kaptcha.border.thickness">2</entry>
    <!-- 图片宽度，默认值200 -->
    <entry key="kaptcha.image.width">200</entry>
    <!-- 图片高度，默认值50 -->
    <entry key="kaptcha.image.height">50</entry>
    <!-- 图片实现类，默认值priv.kerlomz.kaptcha.impl.DefaultKaptcha -->
    <entry key="kaptcha.producer.impl">priv.kerlomz.kaptcha.impl.DefaultKaptcha</entry>
    <!-- 文本实现类,默认值priv.kerlomz.kaptcha.impl.DefaultTextCreator -->
    <entry key="kaptcha.textproducer.impl">priv.kerlomz.kaptcha.text.impl.DefaultTextCreator</entry>
    <!-- 文本集合，验证码值从此集合中获取,默认值abcde2345678gfynmnpwx -->
    <entry key="kaptcha.textproducer.char.string">abcde2345678gfynmnpwx</entry>
    <!-- 验证码长度,默认值为5 -->
    <entry key="kaptcha.textproducer.char.length">5</entry>
    <!-- 字体,默认值Arial, Courier(如果使用中文验证码，则必须使用中文的字体，否则出现乱码) -->
    <entry key="kaptcha.textproducer.font.names">Arial</entry>
    <!-- 字体大小，默认值为40px -->
    <entry key="kaptcha.textproducer.font.size">40</entry>
    <!-- 字体颜色，合法值： r,g,b 或者 white,black,blue，默认值black -->
    <entry key="kaptcha.textproducer.font.color">black</entry>
    <!-- 文字间隔，默认值为2 -->
    <entry key="kaptcha.textproducer.char.space">2</entry>
    <!-- 干扰实现类，默认值priv.kerlomz.kaptcha.impl.DefaultNoise -->
    <entry key="kaptcha.noise.impl">priv.kerlomz.kaptcha.impl.DefaultNoise</entry>
    <!-- 干扰 颜色，合法值： r,g,b 或者 white,black,blue，默认值black -->
    <entry key="kaptcha.noise.color">black</entry>
    <!-- 图片样式： 
         水纹 priv.kerlomz.kaptcha.impl.WaterRipple 
         鱼眼 priv.kerlomz.kaptcha.impl.FishEyeGimpy
         阴影 priv.kerlomz.kaptcha.impl.ShadowGimpy, 默认值水纹    
    -->
    <entry key="kaptcha.obscurificator.impl">priv.kerlomz.kaptcha.impl.WaterRipple</entry>
    <!-- 背景实现类，默认值priv.kerlomz.kaptcha.impl.DefaultBackground -->
    <entry key="kaptcha.background.impl">priv.kerlomz.kaptcha.impl.DefaultBackground</entry>
    <!-- 背景颜色渐变，开始颜色，默认值lightGray/192,193,193 -->
    <entry key="kaptcha.background.clear.from">255,255,255</entry>
    <!-- 背景颜色渐变， 结束颜色，默认值white -->
    <entry key="kaptcha.background.clear.to">white</entry>
    <!-- 文字渲染器，默认值priv.kerlomz.kaptcha.text.impl.DefaultWordRenderer -->
    <entry key="kaptcha.word.impl">priv.kerlomz.kaptcha.text.impl.DefaultWordRenderer</entry>
</properties>
```

controller

```java
@Controller
@RequestMapping("/kaptcha")
public class KaptchaController{

    @Autowired
    private Producer captchaProducer;

    @RequestMapping("/img")
    public ModelAndView verification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");
        // create the text for the image
        String capText = captchaProducer.createText();
        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

}
```

```java
@Controller
public class CaptcharAction {
    private Producer kaptchaProducer=null;

    @Autowired
    public void setCaptchaProducer(Producer kaptchaProducer) {
        this.kaptchaProducer = kaptchaProducer;
    }

    @RequestMapping("/code")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String capText = kaptchaProducer.createText();
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = kaptchaProducer.createImage(capText);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

```

