```java
public class CMDS {
    public static void runCMD(String cmds) throws IOException {
        Process p = Runtime.getRuntime().exec(cmds);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String readLine = br.readLine();
        while (readLine!=null){
            readLine = br.readLine();
            System.out.println(readLine);
        }
        if(br!=null){
            br.close();
        }
        p.destroy();
        p=null;
    }
}
```

在java中调用cmd

