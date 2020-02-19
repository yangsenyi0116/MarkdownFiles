> http://hc.apache.org/httpclient-3.x/
>
> Commons HttpClient项目现已结束，不再开发。它已被其HttpClient和HttpCore模块中的Apache HttpComponents项目所取代，它们提供更好的性能和更大的灵活性。
>
> 从2011年开始，org.apache.commons.httpclient就不再开发。这就是说，它已经落伍了。
>
> 方法的对称性上的区别
>
> 一、org.apache.http.client
>
> org.apache.http.client在发起请求前，假如对某个参数a 进行url encode编码。服务端必须进行url decode。
>
> //客户端编码
>
> Stirng a=URLEncoder.encode(cont,"GBK");
>
> //服务端解码
>
> URLDecoder.decode(a,"gbk");
>
> 且服务器端获取到的参数a为可识别的没有任何变动的url encode后原值。
>
>  
>
> 二、org.apache.commons.httpclient
>
> org.apache.commons.httpclient则与之相反。
>
> 服务端获取到的a为不可识别的乱码，且不能用url decode解码。
>
> //服务端解码
>
> new String(cont.getBytes("ISO8859_1"), "GBK")
>
>  
>
> 与时俱进
>
> org.apache.http.client更好的性能和更大的灵活性。
>
> 可以很方便的支持json，xml等数据的传输。且http://mvnrepository.com上在不断的升级。超时、最大连接数等配置灵活方便。

pom.xml

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.11.3</version>
</dependency>

<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpclient</artifactId>
	<version>4.5</version>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpmime</artifactId>
	<version>4.5</version>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpcore</artifactId>
	<version>4.4.1</version>
</dependency>
```

> 最最简单的方法
>
> 利用Jsoup直接获取HTML页面

```java
Document doc = Jsoup.connect("http://www.xbiquge.la/xiaoshuodaquan/").get();
        Elements elements = doc.getElementsContainingOwnText("斗破苍穹");
```

**简单使用方法**

```java
package com.feilong.reptile.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

public class MHttpClient {
    public void get(String url) throws Exception {
        // 创建HttpClient实例
        HttpClient client =  HttpClientBuilder.create().build();
        // 根据URL创建HttpGet实例
        HttpGet get = new HttpGet(url);
        // 执行get请求，得到返回体
        HttpResponse response = client.execute(get);
        // 判断是否正常返回
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 解析数据
            String data = EntityUtils.toString(response.getEntity(),Charsets.UTF_8);
            System.out.println(data);
        }
    }
 
    public void post(String url) throws Exception {
        // 创建HttpClient实例
        HttpClient client = HttpClientBuilder.create().build();
        // 根据URL创建HttpPost实例
        HttpPost post = new HttpPost(url);
        // 构造post参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", "11"));
        // 编码格式转换
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
        // 传入请求体
        post.setEntity(entity);
        // 发送请求，得到响应体
        HttpResponse response = client.execute(post);
        // 判断是否正常返回
        if (response.getStatusLine().getStatusCode() == 200) {
            // 解析数据
            HttpEntity resEntity = response.getEntity();
            String data = EntityUtils.toString(resEntity);
            System.out.println(data);
        }
    }
    
    public static void main(String[] args) throws Exception {
        MHttpClient cl = new MHttpClient();
        String url = "http://www.xbiquge.la/xiaoshuodaquan/";
        cl.get(url);
    }

}
```

**复杂使用方法**

```java
package com.feilong.reptile.util;
import java.io.IOException;  
import java.io.InterruptedIOException;  
import java.net.UnknownHostException;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;  
  
import javax.net.ssl.SSLException;  
  
import org.apache.commons.codec.Charsets;  
import org.apache.http.Header;  
import org.apache.http.HttpEntityEnclosingRequest;  
import org.apache.http.HttpRequest;  
import org.apache.http.HttpResponse;  
import org.apache.http.client.HttpClient;  
import org.apache.http.client.HttpRequestRetryHandler;  
import org.apache.http.client.config.RequestConfig;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.client.protocol.HttpClientContext;  
import org.apache.http.client.utils.URIBuilder;  
import org.apache.http.config.SocketConfig;  
import org.apache.http.conn.ConnectTimeoutException;  
import org.apache.http.impl.client.HttpClients;  
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;  
import org.apache.http.protocol.HttpContext;  
import org.apache.http.util.EntityUtils;  
  
/** 
 * 使用HttpClient发送和接收Http请求 
 *  
 * @author manzhizhen 
 * 
 */  
public class HttpUtils {  
  
    private static HttpClient httpClient;  
    // 最大连接数  
    private static final int MAX_CONNECTION = 100;  
    // 每个route能使用的最大连接数，一般和MAX_CONNECTION取值一样  
    private static final int MAX_CONCURRENT_CONNECTIONS = 100;  
    // 建立连接的超时时间，单位毫秒  
    private static final int CONNECTION_TIME_OUT = 1000;  
    // 请求超时时间，单位毫秒  
    private static final int REQUEST_TIME_OUT = 1000;  
    // 最大失败重试次数  
    private static final int MAX_FAIL_RETRY_COUNT = 3;  
    // 请求配置，可以复用  
    private static RequestConfig requestConfig;  
  
    static {  
        SocketConfig socketConfig = SocketConfig.custom()  
                .setSoTimeout(REQUEST_TIME_OUT).setSoKeepAlive(true)  
                .setTcpNoDelay(true).build();  
  
        requestConfig = RequestConfig.custom()  
                .setSocketTimeout(REQUEST_TIME_OUT)  
                .setConnectTimeout(CONNECTION_TIME_OUT).build();  
        /** 
         * 每个默认的 ClientConnectionPoolManager 实现将给每个route创建不超过2个并发连接，最多20个连接总数。 
         */  
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();  
        connManager.setMaxTotal(MAX_CONNECTION);  
        connManager.setDefaultMaxPerRoute(MAX_CONCURRENT_CONNECTIONS);  
        connManager.setDefaultSocketConfig(socketConfig);  
  
        httpClient = HttpClients.custom().setConnectionManager(connManager)  
        // 添加重试处理器  
                .setRetryHandler(new MyHttpRequestRetryHandler()).build();  
    }  
  
    public static void main(String[] args) {  
        testGet();  
    }  
  
    /** 
     * 测试get方法 
     */  
    private static void testGet() {  
        String url = "http://restapi.amap.com/v3/place/text";  
        Map<String, String> paramMap = new HashMap<String, String>();  
        paramMap.put("key", "95708f902ac2428ea119ec99fb70e6a3");  
        paramMap.put("keywords", "互联网金融大厦");  
        paramMap.put("city", "330100");  
        paramMap.put("extensions", "all");  
  
        try {  
            System.out.println(get(url, paramMap));  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * post请求 
     *  
     * @param url 
     * @param paramMap 
     * @param headers 
     * @return 
     * @throws Exception 
     */  
    public static String post(String url, Map<String, String> paramMap,  
            List<Header> headers) throws Exception {  
        URIBuilder uriBuilder = new URIBuilder(url);  
        if (paramMap != null) {  
            // 添加请求参数  
            for (Entry<String, String> entry : paramMap.entrySet()) {  
                uriBuilder.addParameter(entry.getKey(), entry.getValue());  
            }  
        }  
  
        HttpPost httpPost = new HttpPost(uriBuilder.build());  
        if (headers != null) {  
            // 添加请求首部  
            for (Header header : headers) {  
                httpPost.addHeader(header);  
            }  
        }  
  
        httpPost.setConfig(requestConfig);  
  
        // 执行请求  
        HttpResponse response = httpClient.execute(httpPost);  
  
        return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);  
    }  
  
    /** 
     * post请求，不带请求首部 
     *  
     * @param url 
     * @param paramMap 
     * @return 
     * @throws Exception 
     */  
    public static String post(String url, Map<String, String> paramMap)  
            throws Exception {  
  
        return post(url, paramMap, null);  
    }  
  
    /** 
     * get请求 
     *  
     * @param url 
     * @param paramMap 
     * @param headers 
     * @return 
     * @throws Exception 
     */  
    public static String get(String url, Map<String, String> paramMap,  
            List<Header> headers) throws Exception {  
        URIBuilder uriBuilder = new URIBuilder(url);  
        if (paramMap != null) {  
            // 添加请求参数  
            for (Entry<String, String> entry : paramMap.entrySet()) {  
                uriBuilder.addParameter(entry.getKey(), entry.getValue());  
            }  
        }  
  
        HttpGet httpGet = new HttpGet(uriBuilder.build());  
        if (headers != null) {  
            // 添加请求首部  
            for (Header header : headers) {  
                httpGet.addHeader(header);  
            }  
        }  
  
        httpGet.setConfig(requestConfig);  
  
        // 执行请求  
        HttpResponse response = httpClient.execute(httpGet);  
  
        return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);  
    }  
  
    /** 
     * get请求，不带请求首部 
     *  
     * @param url 
     * @param paramMap 
     * @return 
     * @throws Exception 
     */  
    public static String get(String url, Map<String, String> paramMap)  
            throws Exception {  
  
        return get(url, paramMap, null);  
    }  
  
    /** 
     * 请求重试处理器 
     * @author manzhizhen 
     * 
     */  
    private static class MyHttpRequestRetryHandler implements  
            HttpRequestRetryHandler {  
  
        @Override  
        public boolean retryRequest(IOException exception, int executionCount,  
                HttpContext context) {  
            if (executionCount >= MAX_FAIL_RETRY_COUNT) {  
                return false;  
            }  
  
            if (exception instanceof InterruptedIOException) {  
                // 超时  
                return false;  
            }  
            if (exception instanceof UnknownHostException) {  
                // 未知主机  
                return false;  
            }  
            if (exception instanceof ConnectTimeoutException) {  
                // 连接被拒绝  
                return false;  
            }  
            if (exception instanceof SSLException) {  
                // SSL handshake exception  
                return false;  
            }  
  
            HttpClientContext clientContext = HttpClientContext.adapt(context);  
            HttpRequest request = clientContext.getRequest();  
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);  
            if (idempotent) {  
                // 如果请求被认为是幂等的，则重试  
                return true;  
            }  
  
            return false;  
        }  
    }  
}
```

**完整pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.6.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.feilong</groupId>
    <artifactId>reptile</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>reptile</name>
    <description>reptile for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
             <version>1.2.58</version>
        </dependency>
       
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
<!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.11.3</version>
</dependency>

<dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
            <version>4.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.4.1</version>
        </dependency>

 
        
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

