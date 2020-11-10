#### maven依赖包：

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.47</version>
</dependency>

<dependency>
     <groupId>org.projectlombok</groupId>
     <artifactId>lombok</artifactId>
</dependency>
```

Java对象转Json字符串

```java
private static void java2String() {
        Student student = new Student();
        student.setId(123L);
        student.setName("jack");
        student.setBirthday(LocalDate.now());
        student.setAge(10);
        Clazz clazz = new Clazz();
        clazz.setId(123L);
        clazz.setName("三年二班");
        clazz.setStudents(Lists.newArrayList(student));

        String jsonString = JSONObject.toJSONString(clazz);
        System.out.println(jsonString);
    	//{"id":123,"name":"三年二班","students":[{"age":10,"birthday":"2019-08-28","id":123,"name":"jack"}]}
    }

```

Json字符串转Json对象

```java
private static void string2Json() {
        String jsonString = "{\"id\":123,\"name\":\"三年二班\",\"students\":[{\"age\":10,\"birthday\":\"2019-08-28\",\"id\":123,\"name\":\"jack\"}]}\n";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String clazzName = jsonObject.getString("name");
        System.out.println("clazzName:" + clazzName);
        //clazzName:三年二班
    }

```

json字符串转java对象

```java
private static void string2Java() {
        String jsonString = "{\"id\":123,\"name\":\"三年二班\",\"students\":[{\"age\":10,\"birthday\":\"2019-08-28\",\"id\":123,\"name\":\"jack\"}]}\n";
        Clazz clazz = JSONObject.parseObject(jsonString, Clazz.class);
        System.out.println(clazz.toString());
        //{"id":123,"name":"三年二班","students":[{"age":10,"birthday":"2019-08-28","id":123,"name":"jack"}]}
    }

```

json对象转java对象

```java
private static void json2Java() {
        String jsonString = "{\"id\":123,\"name\":\"三年二班\",\"students\":[{\"age\":10,\"birthday\":\"2019-08-28\",\"id\":123,\"name\":\"jack\"}]}\n";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        System.out.println(jsonObject.getString("name"));
        //三年二班
        //json转数组
        JSONArray jsonArray = jsonObject.getJSONArray("students");
        List<Student> students = jsonArray.toJavaList(Student.class);
        System.out.println(students.toString());
        //[{"age":10,"birthday":"2019-08-28","id":123,"name":"jack"}]
    }

```

