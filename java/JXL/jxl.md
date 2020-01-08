```
try {
    String fileName = "yourPath/datas.xls"; 
    File file = new File(fileName); 
    // 设置读文件编码
    WorkbookSettings setEncode = new WorkbookSettings();
    setEncode.setEncoding("GB2312");
    // 从文件流中获取Excel工作区对象（WorkBook）
    Workbook wb = Workbook.getWorkbook(file,setEncode); 
    // 从工作区中取得页（Sheet）,默认单独一页，第一页
    Sheet sheet = wb.getSheet(0); 
    // 测试：循环打印Excel表中的内容
    for (int i = 0; i < 50; i++) { 
        for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j, i);
        System.out.print(cell.getContents());
                }
                System.out.println();
            }
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
```

这样便可以挨个取出xls文件中每个单元格内的具体内容。

## =====xls转 CSV=======

但是xls文件并不如csv的逗号文件应用方便和广泛，因此本文顺便示例如何将xls文件转化为csv文件。 
其实只需要挨个将xls文件的单元格数据读取出来以后，加上英文逗号，追加到StringBuffer中即可，待完成所有数据的读取，最后将buffer写入到文件中。 
但是有一个小小的陷阱需要注意：那就是在xls文件中，某个单元格内的内容有可能存在换行问题，因此需要置换每行的数据（除了每行的结束）以外所有换行符”\n”,不然生成的csv是无法使用的，简单示例代码如下：

```java
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class dealExcels {

    public static void main(String[] args) {
         String buffer = "";
         try {  
                String fileName = "yourPath/datas.xls"; 
                File file = new File(fileName);
                // 设置读文件编码
    WorkbookSettings setEncode = new WorkbookSettings();
    setEncode.setEncoding("GB2312");
    // 从文件流中获取Excel工作区对象（WorkBook）
    Workbook wb = Workbook.getWorkbook(file,setEncode); 
                Sheet sheet = wb.getSheet(0); 

          for (int i = 0; i < sheet.getRows(); i++) {  
              for (int j = 0; j < 11; j++) {  
                 Cell cell = sheet.getCell(j, i);   
                 buffer += cell.getContents().replaceAll("\n", " ")+",";
               }  
            buffer = buffer.substring(0, buffer.lastIndexOf(",")).toString();
            buffer += "\n";
         }  
    } catch (BiffException e) {  
           e.printStackTrace();  
    } catch (IOException e) {  
           e.printStackTrace();  
            }   
    //write the string into the file
    String savePath = "yourPath/datas.csv";
    File saveCSV = new File(savePath);
    try {   
        if(!saveCSV.exists())
            saveCSV.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(saveCSV));
        writer.write(buffer);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }        
  }
}
```