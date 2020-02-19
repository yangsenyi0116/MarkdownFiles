### HttpURLConnection

使用JDK原生提供的net，无需其他jar包；

HttpURLConnection是URLConnection的子类，提供更多的方法，使用更方便。

```java
package httpURLConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionHelper {

    public static String sendRequest(String urlParam,String requestType) {

        HttpURLConnection con = null;  

        BufferedReader buffer = null; 
        StringBuffer resultBuffer = null;  

        try {
            URL url = new URL(urlParam); 
            //得到连接对象
            con = (HttpURLConnection) url.openConnection(); 
            //设置请求类型
            con.setRequestMethod(requestType);  
            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=GBK");  
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            //得到响应码
            int responseCode = con.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                //得到响应流
                InputStream inputStream = con.getInputStream();
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                return resultBuffer.toString();
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void main(String[] args) {

        String url ="http://int.dpool.sina.com.cn/iplookup/iplookup.php?ip=120.79.75.96";
        System.out.println(sendRequest(url,"POST"));
    }
}
```

### URLConnection

使用JDK原生提供的net，无需其他jar包；

建议使用HttpURLConnection

```java
package uRLConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionHelper {

    public static String sendRequest(String urlParam) {

        URLConnection con = null;  

        BufferedReader buffer = null; 
        StringBuffer resultBuffer = null;  

        try {
             URL url = new URL(urlParam); 
             con = url.openConnection();  

            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=GBK");  
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            //得到响应流
            InputStream inputStream = con.getInputStream();
            //将响应流转换成字符串
            resultBuffer = new StringBuffer();
            String line;
            buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            while ((line = buffer.readLine()) != null) {
                resultBuffer.append(line);
            }
            return resultBuffer.toString();

        }catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static void main(String[] args) {
        String url ="http://int.dpool.sina.com.cn/iplookup/iplookup.php?ip=120.79.75.96";
        System.out.println(sendRequest(url));
    }
}
```

### HttpClient

使用方便，我个人偏爱这种方式，但依赖于第三方jar包，相关maven依赖如下：

```xml
<!-- https://mvnrepository.com/artifact/commons-httpclient/commons-httpclient -->
<dependency>
    <groupId>commons-httpclient</groupId>
    <artifactId>commons-httpclient</artifactId>
    <version>3.1</version>
</dependency>
```

```java
package httpClient;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientHelper {
    public static String sendPost(String urlParam) throws HttpException, IOException {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(urlParam);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");

        httpClient.executeMethod(postMethod);

        String result = postMethod.getResponseBodyAsString();
        postMethod.releaseConnection();
        return result;
    }
    public static String sendGet(String urlParam) throws HttpException, IOException {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建GET请求方法实例对象
        GetMethod getMethod = new GetMethod(urlParam);
        // 设置post请求超时时间
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        getMethod.addRequestHeader("Content-Type", "application/json");

        httpClient.executeMethod(getMethod);

        String result = getMethod.getResponseBodyAsString();
        getMethod.releaseConnection();
        return result;
    }
    public static void main(String[] args) throws HttpException, IOException {
        String url ="http://int.dpool.sina.com.cn/iplookup/iplookup.php?ip=120.79.75.96";
        System.out.println(sendPost(url));
        System.out.println(sendGet(url));
    }
}
```

### Socket

```java
package socket;
import java.io.BufferedInputStream;  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.net.Socket;  
import java.net.URLEncoder;  

import javax.net.ssl.SSLSocket;  
import javax.net.ssl.SSLSocketFactory;  

public class SocketForHttpTest {  

    private int port;  
    private String host;  
    private Socket socket;  
    private BufferedReader bufferedReader;  
    private BufferedWriter bufferedWriter;  

    public SocketForHttpTest(String host,int port) throws Exception{  

        this.host = host;  
        this.port = port;  

        /**  
         * http协议  
         */  
        // socket = new Socket(this.host, this.port);  

        /**  
         * https协议  
         */  
        socket = (SSLSocket)((SSLSocketFactory)SSLSocketFactory.getDefault()).createSocket(this.host, this.port);  


    }  

    public void sendGet() throws IOException{  
        //String requestUrlPath = "/z69183787/article/details/17580325";  
        String requestUrlPath = "/";          

        OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());    
        bufferedWriter = new BufferedWriter(streamWriter);              
        bufferedWriter.write("GET " + requestUrlPath + " HTTP/1.1\r\n");    
        bufferedWriter.write("Host: " + this.host + "\r\n");    
        bufferedWriter.write("\r\n");    
        bufferedWriter.flush();    

        BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());    
        bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));    
        String line = null;    
        while((line = bufferedReader.readLine())!= null){    
            System.out.println(line);    
        }    
        bufferedReader.close();    
        bufferedWriter.close();    
        socket.close();  

    }  


    public void sendPost() throws IOException{    
            String path = "/";    
            String data = URLEncoder.encode("name", "utf-8") + "=" + URLEncoder.encode("张三", "utf-8") + "&" +    
                        URLEncoder.encode("age", "utf-8") + "=" + URLEncoder.encode("32", "utf-8");    
            // String data = "name=zhigang_jia";    
            System.out.println(">>>>>>>>>>>>>>>>>>>>>"+data);              
            OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream(), "utf-8");    
            bufferedWriter = new BufferedWriter(streamWriter);                  
            bufferedWriter.write("POST " + path + " HTTP/1.1\r\n");    
            bufferedWriter.write("Host: " + this.host + "\r\n");    
            bufferedWriter.write("Content-Length: " + data.length() + "\r\n");    
            bufferedWriter.write("Content-Type: application/x-www-form-urlencoded\r\n");    
            bufferedWriter.write("\r\n");    
            bufferedWriter.write(data);    

            bufferedWriter.write("\r\n");    
            bufferedWriter.flush();    

            BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());    
            bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));    
            String line = null;    
            while((line = bufferedReader.readLine())!= null)    
            {    
                System.out.println(line);    
            }    
            bufferedReader.close();    
            bufferedWriter.close();    
            socket.close();    
    }    

    public static void main(String[] args) throws Exception {  
        /**  
         * http协议测试  
         */  
        //SocketForHttpTest forHttpTest = new SocketForHttpTest("www.baidu.com", 80);  
        /**  
         * https协议测试  
         */  
        SocketForHttpTest forHttpTest = new SocketForHttpTest("www.baidu.com", 443);  
        try {  
            forHttpTest.sendGet();  
        //  forHttpTest.sendPost();  
        } catch (IOException e) {  

            e.printStackTrace();  
        }  
    }  

} 
```

