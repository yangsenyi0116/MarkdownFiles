# [java十进制转换成二进制数](https://www.cnblogs.com/zzlback/p/8657180.html)

牢记这些呀,特别常用!

1.十进制转成二进制

String s = Integer.toBinaryString(n) //将十进制数转成字符串，例如n=5 ，s = "101"

2.将字符串转成整形

int a = Integer.valueof("1002"); //当然s只能是数字类的字符串

或者

int a = Integer.parseInt("1002");

3.将整形转成字符串
String s = String.valueof(1025); 直接转成了

 

4.将整形转成十六进制的数

String s = Integer.toHexString(18);  //输出结果12