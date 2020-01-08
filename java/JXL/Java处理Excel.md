转载源地址：<https://www.cnblogs.com/liyafei/p/8146136.html>

1：添加处理excel的依赖jar包

 

```html
        <!-- 引入poi，解析workbook视图 -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>3.16</version>
        </dependency>

        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>3.14</version>
        </dependency>
        <!-- 处理excel和上面功能是一样的-->
        <dependency>
            <groupId>net.sourceforge.jexcelapi</groupId>
            <artifactId>jxl</artifactId>
            <version>2.6.10</version>
        </dependency>
```

 

2：向excel中写入内容的类

　　WriteExcel.java

 

```java
package com.li.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell；
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class WriteExcel {



    private static final String EXCEL_XLS = "xls";



    private static final String EXCEL_XLSX = "xlsx";



    



    public static void main(String[] args) {



        



        Map<String, String> dataMap=new HashMap<String, String>();



        dataMap.put("BankName", "BankName");



        dataMap.put("Addr", "Addr");



        dataMap.put("Phone", "Phone");



        List<Map> list=new ArrayList<Map>();



        list.add(dataMap);



        writeExcel(list, 3, "D:/writeExcel.xlsx");



        



    }



 



    public static void writeExcel(List<Map> dataList, int cloumnCount,String finalXlsxPath){



        OutputStream out = null;



        try {



            // 获取总列数



            int columnNumCount = cloumnCount;



            // 读取Excel文档



            File finalXlsxFile = new File(finalXlsxPath);



            Workbook workBook = getWorkbok(finalXlsxFile);



            // sheet 对应一个工作页



            Sheet sheet = workBook.getSheetAt(0);



            /**



             * 删除原有数据，除了属性列



             */



            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算



            System.out.println("原始数据总行数，除属性列：" + rowNumber);



            for (int i = 1; i <= rowNumber; i++) {



                Row row = sheet.getRow(i);



                sheet.removeRow(row);



            }



            // 创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效



            out =  new FileOutputStream(finalXlsxPath);



            workBook.write(out);



            /**



             * 往Excel中写新数据



             */



            for (int j = 0; j < dataList.size(); j++) {



                // 创建一行：从第二行开始，跳过属性列



                Row row = sheet.createRow(j + 1);



                // 得到要插入的每一条记录



                Map dataMap = dataList.get(j);



                String name = dataMap.get("BankName").toString();



                String address = dataMap.get("Addr").toString();



                String phone = dataMap.get("Phone").toString();



                for (int k = 0; k <= columnNumCount; k++) {



                // 在一行内循环



                Cell first = row.createCell(0);



                first.setCellValue(name);



        



                Cell second = row.createCell(1);



                second.setCellValue(address);



        



                Cell third = row.createCell(2);



                third.setCellValue(phone);



                }



            }



            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效



            out =  new FileOutputStream(finalXlsxPath);



            workBook.write(out);



        } catch (Exception e) {



            e.printStackTrace();



        } finally{



            try {



                if(out != null){



                    out.flush();



                    out.close();



                }



            } catch (IOException e) {



                e.printStackTrace();



            }



        }



        System.out.println("数据导出成功");



    }



 



    /**



     * 判断Excel的版本,获取Workbook



     * @param in



     * @param filename



     * @return



     * @throws IOException



     */



    public static Workbook getWorkbok(File file) throws IOException{



        Workbook wb = null;



        FileInputStream in = new FileInputStream(file);



        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003



            wb = new HSSFWorkbook(in);



        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010



            wb = new XSSFWorkbook(in);



        }



        return wb;



    }



}
 
```

3:读取Excel中的数据，并写入list中

 

```java
package com.li.controller;



import java.io.File;



import java.io.FileInputStream;



import java.io.FileNotFoundException;



import java.io.IOException;



import java.io.InputStream;



import java.util.ArrayList;



import java.util.List;



 



import jxl.Sheet;



import jxl.Workbook;



import jxl.read.biff.BiffException;



public class ReadExcel {



    public static void main(String[] args) {



        ReadExcel obj = new ReadExcel();



        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下



        File file = new File("D:/readExcel.xls");



        List excelList = obj.readExcel(file);



        System.out.println("list中的数据打印出来");



        for (int i = 0; i < excelList.size(); i++) {



            List list = (List) excelList.get(i);



            for (int j = 0; j < list.size(); j++) {



                System.out.print(list.get(j));



            }



            System.out.println();



        }



 



    }



    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象



    public List readExcel(File file) {



        try {



            // 创建输入流，读取Excel



            InputStream is = new FileInputStream(file.getAbsolutePath());



            // jxl提供的Workbook类



            Workbook wb = Workbook.getWorkbook(is);



            // Excel的页签数量



            int sheet_size = wb.getNumberOfSheets();



            for (int index = 0; index < sheet_size; index++) {



                List<List> outerList=new ArrayList<List>();



                // 每个页签创建一个Sheet对象



                Sheet sheet = wb.getSheet(index);



                // sheet.getRows()返回该页的总行数



                for (int i = 0; i < sheet.getRows(); i++) {



                    List innerList=new ArrayList();



                    // sheet.getColumns()返回该页的总列数



                    for (int j = 0; j < sheet.getColumns(); j++) {



                        String cellinfo = sheet.getCell(j, i).getContents();



                        if(cellinfo.isEmpty()){



                            continue;



                        }



                        innerList.add(cellinfo);



                        System.out.print(cellinfo);



                    }



                    outerList.add(i, innerList);



                    System.out.println();



                }



                return outerList;



            }



        } catch (FileNotFoundException e) {



            e.printStackTrace();



        } catch (BiffException e) {



            e.printStackTrace();



        } catch (IOException e) {



            e.printStackTrace();



        }



        return null;



    }



}
```

 

4：在D盘下面创建readExcel.xls（有内容） 和writeExcel.xlsx即可

 （二）转载地址：<https://www.cnblogs.com/wangyang108/p/6030420.html>

 

Excel是我们平时工作中比较常用的用于存储二维表数据的，JAVA也可以直接对Excel进行操作，在这篇博客中将为大家介绍两种操作Excel的方式，分别为：jxl和poi。

对于两者的区别网上有测试如下：

测试结果 

类型 　　数据量(行)　　 执行时间(ms) 　　执行时间(ms)　　 执行时间(ms) 　　平均时间(ms) 
POI 　　1000 　　　　　　579 　　　　　　562 　　　　　　532 　　　　　　558 
JXL 　　1000　　　　　　 500　　　　　　 469　　　　　　 484　　　　　　 484 
POI 　　5000 　　　　　　984 　　　　　　984 　　　　　　969　　　　　　 979 
JXL 　　5000 　　　　　　922 　　　　　　860 　　　　　　890 　　　　　　891 
POI 　　10000 　　　　　1609 　　　　　1594 　　　　　1641 　　　　　　1615 
JXL 　　10000 　　   　　1437 　　　　　1453 　　　　　1406 　　　　　　1432 
POI 　　30000 　　　　　3782 　　　　　3765 　　　　　3828 　　　　　　3792 
JXL 　　30000 　　　　　3922 　　　　　3906 　　　　　3922 　　　　　　3917 
POI 　　50000 　　　　　5953 　　　　　6484 　　　　　5859 　　　　　　6099 
JXL 　　50000　　　　　 6765 　　　　　7421 　　　　　6984 　　　　　　7057 

 

 **在小数据量时jxl快于poi，在大数据量时poi要快于jxl。但差距都不明显。**

# （一）jxl

## 写Excel

 

```java
import java.io.File;



import java.io.IOException;



 



import jxl.Workbook;



import jxl.write.Label;



import jxl.write.WritableSheet;



import jxl.write.WritableWorkbook;



import jxl.write.WriteException;



 



/**



 * jxl写Excel



 * 



 * @author jianggujin



 * 



 */



public class JxlWriteDemo



{



   public static void main(String[] args) throws IOException, WriteException



   {



      File xlsFile = new File("jxl.xls");



      // 创建一个工作簿



      WritableWorkbook workbook = Workbook.createWorkbook(xlsFile);



      // 创建一个工作表



      WritableSheet sheet = workbook.createSheet("sheet1", 0);



      for (int row = 0; row < 10; row++)



      {



         for (int col = 0; col < 10; col++)



         {



            // 向工作表中添加数据



            sheet.addCell(new Label(col, row, "data" + row + col));



         }



      }



      workbook.write();



      workbook.close();



   }



}
```

 

## 读Excel

 

 

```java
import java.io.File;



import java.io.IOException;



 



import jxl.Sheet;



import jxl.Workbook;



import jxl.read.biff.BiffException;



 



/**



 * jxl读excel



 * 



 * @author jianggujin



 * 



 */



public class JxlReadDemo



{



   public static void main(String[] args) throws BiffException, IOException



   {



      File xlsFile = new File("jxl.xls");



      // 获得工作簿对象



      Workbook workbook = Workbook.getWorkbook(xlsFile);



      // 获得所有工作表



      Sheet[] sheets = workbook.getSheets();



      // 遍历工作表



      if (sheets != null)



      {



         for (Sheet sheet : sheets)



         {



            // 获得行数



            int rows = sheet.getRows();



            // 获得列数



            int cols = sheet.getColumns();



            // 读取数据



            for (int row = 0; row < rows; row++)



            {



               for (int col = 0; col < cols; col++)



               {



                  System.out.printf("%10s", sheet.getCell(col, row)



                        .getContents());



               }



               System.out.println();



            }



         }



      }



      workbook.close();



   }



}
 
```

# （二）poi

## 写Excel

 

```java
import java.io.File;



import java.io.FileOutputStream;



import java.io.IOException;



 



import org.apache.poi.hssf.usermodel.HSSFRow;



import org.apache.poi.hssf.usermodel.HSSFSheet;



import org.apache.poi.hssf.usermodel.HSSFWorkbook;



 



/**



 * Poi写Excel



 * 



 * @author jianggujin



 * 



 */



public class PoiWriteDemo



{



   public static void main(String[] args) throws IOException



   {



      // 创建工作薄



      HSSFWorkbook workbook = new HSSFWorkbook();



      // 创建工作表



      HSSFSheet sheet = workbook.createSheet("sheet1");



 



      for (int row = 0; row < 10; row++)



      {



         HSSFRow rows = sheet.createRow(row);



         for (int col = 0; col < 10; col++)



         {



            // 向工作表中添加数据



            rows.createCell(col).setCellValue("data" + row + col);



         }



      }



 



      File xlsFile = new File("poi.xls");



      FileOutputStream xlsStream = new FileOutputStream(xlsFile);



      workbook.write(xlsStream);



   }



}
```

 

## 读Excel

 

```java
import java.io.File;



import java.io.IOException;



 



import org.apache.poi.openxml4j.exceptions.InvalidFormatException;



import org.apache.poi.ss.usermodel.Row;



import org.apache.poi.ss.usermodel.Sheet;



import org.apache.poi.ss.usermodel.Workbook;



import org.apache.poi.ss.usermodel.WorkbookFactory;



 



/**



 * Poi写Excel



 * 



 * @author jianggujin



 * 



 */



public class PoiReadDemo



{



   public static void main(String[] args) throws IOException,



         InvalidFormatException



   {



      File xlsFile = new File("poi.xls");



      // 获得工作簿



      Workbook workbook = WorkbookFactory.create(xlsFile);



      // 获得工作表个数



      int sheetCount = workbook.getNumberOfSheets();



      // 遍历工作表



      for (int i = 0; i < sheetCount; i++)



      {



         Sheet sheet = workbook.getSheetAt(i);



         // 获得行数



         int rows = sheet.getLastRowNum() + 1;



         // 获得列数，先获得一行，在得到改行列数



         Row tmp = sheet.getRow(0);



         if (tmp == null)



         {



            continue;



         }



         int cols = tmp.getPhysicalNumberOfCells();



         // 读取数据



         for (int row = 0; row < rows; row++)



         {



            Row r = sheet.getRow(row);



            for (int col = 0; col < cols; col++)



            {



               System.out.printf("%10s", r.getCell(col).getStringCellValue());



            }



            System.out.println();



         }



      }



   }
```