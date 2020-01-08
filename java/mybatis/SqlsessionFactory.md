### 加载mybatis配置文件

```java
public class SqlSessionUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static{
        String resource="[配置文件名称].xml";
        try(InputStream in=Resources.getResourceAsStream(resource)){
            sqlSessionFactory=new SqlSessionFactoryBuilder().build(in);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SqlSession openSession(){
        return sqlSessionFactory.openSession();
    }

}
```

