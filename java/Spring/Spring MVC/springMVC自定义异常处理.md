springmvc.xml

```xml
    <!--指定异常处理器-->
    <bean class="kermi.maven.springmvc.project.exception.CustomExceptionResover">
```



CustomExcetion.java

```java
public class CustomException extends RuntimeException {
    private String message;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}

```



CustomExceptionResover.java

```java
public class CustomExceptionResover implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        CustomException ce = null;
        if(e instanceof CustomException){
            ce=(CustomException)e;
        }else{
            ce = new CustomException("系统异常");
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("msg",ce.getMessage());
        mav.setViewName("error");
        return mav;
    }
}
```

