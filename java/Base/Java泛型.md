## CSDN

### **一、泛型类**

**首先定义一个简单的BBox类：**

```java
public class BBox{
    private String date;

    public void setDate(String date){
        this.date=date;
    }

    public String getDate(){
        return date;
    }
}
```

这个盒子类可以装String数据，如何我们又要一个能装Integer数据的盒子呢？以此方式的话需要再写一个新的类，之后又要装Byte,Short,Long,Float,Double,Character,Boolean类型的数据呢？那么我们有要写这么多新的类难道不累吗？为了提高工作效率，这时候泛型类就登场了：

```java
public class BBox<T>{
    private T date;

    public void setDate(T date){
        this.date=date;
    }

    public T getDate(){
        return date;
    }
}
```

**我们需要什么类型的盒子就自己new就可以了：**

```java
 BBox<Integer> intbox=new BBox<Integer>();
 BBox<Double> doubleBBox=new BBox<Double>();
 BBox<Character> charbox= new BBox<Character>(); 
```

### **二、泛型方法**

**根据刚才那个类改一下：**

```java
public class BBox<K,V>{
    private K key;
    private V value;

    public BBox(K key,V value){
        this.key=key;
        this.value=value;
    }

    public static <K,V> boolean compare(BBox<K,V> box1,BBox<K,V> box2){
        return box1.getKey().equals(box2.getKey())&&box1.getValue().equals(box2.getValue());
    }

    public void setKey(K key){
        this.key=key;
    }

    public void setValue(V value){
        this.value=value;
    }

    public K getKey(){
        return key;
    }

    public V getValue(){
        return value;
    }
}

```

**注意这个泛型方法在声明的时候需要用<>加上泛型声明**

**然后可以如下调用方法：**

```java
BBox<Integer,String> box1=new BBox<Integer, String>(1,"sean");
BBox<Integer,String> box2=new BBox<Integer, String>(1,"sea");
System.out.println(BBox.compare(box1,box2));
```

### **三、类型擦除**

**下面来说说泛型的本质作用：泛型的约束于表面，只在编译器检查的时候充当一把枷锁，约束代码的书写规范，在编译后泛型会被擦除，用原始类型取而代之：**

**编译（擦除）前：**

```java
public class BBox<T>{
    private T date;

    public void setDate(T date){
        this.date=date;
    }

    public T getDate(){
        return date;
    }

}
```

**编译（擦除）后：**

```java
public class BBox{
    private Object date;

    public void setDate(Object date){
        this.date=date;
    }

    public Object getDate(){
        return date;
    }

}
```

**通常没有边界符的时候，泛型被擦除后直接被Object类取代，因为根据多态特性它可以指向任何类嘛，泛型为何对它都是无压力的。**

**则编（擦除）译后：**

```java
public class BBox {
    private Number date;

    public BBox() {
    }

    public void setDate(Number date) {
        this.date = date;
    }

    public Number getDate() {
        return this.date;
    }
}
```

### **四、边界符**

**如果我们想修改类型擦除后的类型，可按如下形式：**

```java
public class BBox<T extends Number> {
    private T date;

    public BBox() {
    }

    public void setDate(T date) {
        this.date = date;
    }

    public T getDate() {
        return this.date;
    }
}
```

### 五、PECS原则(Producer Extends Consumer Super)

**1、Producer Extends**

**先看一段代码：**

```java
public class Main {

     static class Animal{}
     static class Cat extends Animal{}
     static class Dog extends Animal{}

     static class Read<T>{
         public T read(List<T> list){
         return list.get(0);
      }
     }


    public static void main(String[] args) {
         List<Animal> listanimal=new ArrayList<Animal>();
         List<Cat>    listcat=new ArrayList<Cat>();
         listanimal.add(new Animal());
         listcat.add(new Cat());

        Read<Animal> readanimal=new Read<Animal>();
        Cat cat=readanimal.read(listcat); //此处会报错
    }
}
```

咋一看好像没有问题，因为根据以往的经验容易认为List< Animal >和List< Cat>有继承关系，所以认为Read< Animal>的read()方法可以读取List,其实不然，虽然Animal和Cat有继承关系但是List< Animal>和List< Cat>什么关系都没有，类型不匹配会出现报错的情况。

**为了解决这个问题，增加代码的灵活性我们可以对Read这么改：**

```java
public class Main {

     static class Animal{}
     static class Cat extends Animal{}
     static class Dog extends Animal{}

     static class Read<T>{
         public T read(List<? extends T> list){
         return list.get(0);
      }
     }


    public static void main(String[] args) {
         List<Animal> listanimal=new ArrayList<Animal>();
         List<Cat>    listcat=new ArrayList<Cat>();
         listanimal.add(new Animal());
         listcat.add(new Cat());

        Read<Animal> readanimal=new Read<Animal>();
        Cat cat= (Cat) readanimal.read(listcat);
    }
}

```

< ? extends T>是指由T的所以子孙类包括T在内，这样一来我们使用Read< Animal>也可以读取List< Cat>了包括其他继承Animal类的List，貌似就是又实现了多态。

使用< ? extends T>只能往外取，不能忘里面添加,就像producer生产者一样，故曰Producer Extends,看看这面这段：

```java
    static class Read<T>{
         public T read(List<? extends T> list){
         list.add(new Animal());  //报错
         list.add(new Cat());     //报错
         list.add(new Dog());     //报错
         return list.get(0);

      }
```

**可以看到无论是往list中添加什么都会报错的，因为List< ? extends T>可以指向List< Animal>,List< Cat>,List< Dog>,所以加哪个进来都不合适，就是这个道理。**

**2、Consumer Super**

**既然< ? extends T>修饰的类不能忘里边添加东西，那么如果就需要往里面添加呢？那么需要作如下修改：**

```java
public class Main {

     static class Animal{}
     static class Cat extends Animal{}
     static class Dog extends Animal{}

     static class Read<T>{
         public T read(List<? extends T> list){
         return list.get(0);
      }
     }

     static class Add<T>{
         public <T> void add(List<? super T> list, T t){
             list.add(t);
         }
     }


    public static void main(String[] args) {
         List<Animal> listanimal=new ArrayList<Animal>();
         List<Cat>    listcat=new ArrayList<Cat>();

        Add<Cat> addcat=new Add<Cat>();
        addcat.add(listcat,new Cat());      //即可以把Cat放入listcat中
        addcat.add(listanimal,new Cat());   //也可以把Cat放入listanimal中
    }
}

```

< ? super T>是指T的所有长辈类包括T在内，这样使Cat既可以放入List< Cat>中，又可以放入List< Animal>中，貌似又正确实现了多态。

和< ? extends T>正好相反，被< ? super T>修饰的类的缺点是不能从其中取东西，就像是Consumer顾客一样只能往里要不能往外取，故曰Consumer Super,看看下面的代码说明：

```java
 static class Add<T>{
     public <T> void add(List<? super T> list, T t){
         list.add(t);
         Animal animal=list.get(0);    //报错
         Cat cat=list.get(0);          //报错
         Object object=list.get(0);
     }
 }
```
**这里的道理也很简单，< ? super T>所指的类可能是T类的长辈，所以List< ? super T>中可能就有T类的长辈但是具体是什么也不知道，所以用Animal类，Cat类引用都不对，唯独用Object类引用才可以，因为Object是所有类的长辈。**

**六、泛型数组问题**

**在java中不允许创建泛型数组，例如这样的：**

```java
List<Integer>[] arrayOfLists = new List<Integer>[2]; //报错
```



----



## 简书解释

**Java泛型**

泛型 <T> : T :**即是一个类型占位符  在编译的时候检查**

在泛型之前多态算是一种泛式机制，例如可以将方法的参数类型设置为基类，那么该方法就可以接受从这个基类的任何子类作为参数，这样方法更具有通用性。

```java
class ObjectStack{
	private Object[] elem;//Object类型的
	private int top;
	public ObjectStack(){
		this(10);
	}
	
	public ObjectStack(int size){
		this.elem = new Object[size];
		this.top = 0;
	}
	
	public void push(Object val){
		this.elem[this.top++] = val;
	}
	
	public void pop(){
		--this.top;
	}
	
	public Object getTop(){
		return this.elem[top-1];
	}
	
}
```

接下来对它进行操作

![img](assets/20180531225757241.png)

![img](assets/20180531230001669.png)

1，我们可以看到这块报错，虽然我们栈顶元素为double值，但是编译器并不能知道。所以这个时候**我们需要进行强转**。

2，假设我们是想将Integer型入栈，但是我们并不能阻止其它类型进行入栈。



```java
class GenericStack<T>{
	private T[] elem;
	private int top;
	
	public GenericStack(){
		this(10);
	}
	public GenericStack(int size){
		//this.elem = new T[size];
		this.elem = (T[])new Object[size];
		this.top = 0;
	}
	public void push(T val){
		this.elem[this.top++] = val;
	}
	public void pop(){
		//防止内存泄漏
		this.elem[this.top-1] = null;
		--this.top;
	}
	public T getTop(){
		return this.elem[this.top-1];
	}
}
```

![img](assets/20180531230440145.png)

这个时候如果我们要将其它类型入栈的时候就会发现编译器会报错。

泛型的实现原理

```java
public static void main(String[] args) {
		GenericStack<Integer> s1 = new GenericStack<Integer>();
		GenericStack<String> s2 = new GenericStack<String>();
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s1.getClass()==s2.getClass());
}
```

**泛型的一些需要注意的地方**

1，不能new泛型类型的数组 

![img](assets/20180531231521656.png)

2，不能new泛型类型的对象 T obj = new T();   

![img](assets/2018053123171134.png)

3，能不能 得到泛型类型的对象数组  GenericStack<T>[] a = new GenericStack<T>[10];  不可以！！！！

![img](assets/20180531231936455.png)

Java在运行时候泛型被擦除，所以泛型数组会转化为Object数组，编译器辨别不出具体类型，Integer和String不分，所以会引起类型不安全，所以编译器禁止你这样做



4，简单类型不能为泛型的参数

![img](assets/20180531232346243.png)

原因：(类型擦除往基类方向擦，简单类型无基类！！！！)



5，在static方法中，不能用泛型类型参数(static 不依赖对象，不能指定其泛型类型)

![img](assets/20180531232431916.png)

6，容易内存泄漏

 如果出栈的时候没有这一句

![img](assets/20180531234356308.png)

我们来执行以下语句（对它进行调试并查看内存）

```java
GenericStack<Animal4> g1 = new GenericStack<Animal4>();
		
		g1.push(new Animal4());
		g1.push(new Animal4());
		g1.pop();
	     
		//jmap 命令检查内存泄漏
		System.gc();
```

![img](assets/20180531234727392.png)//jps获得当前进程号

![img](assets/20180531234836883.png)

![img](assets/20180531234920953.png)

这个时候我们会发现Animal4对象时两个，也就是说并没有将这块内存进行释放回收！所以应该要注意内存泄露的问题





**泛型的上界**

```java
class GnericAlg<T>{
	public T findMaxVal(T[] array){
		T maxVal = array[0];
		for(int i = 1;i < array.length;i++) {
			if(maxVal.compareTo(array[i]) < 0){//报错
				maxVal = array[i];
			}
		}
		return maxVal;
	}
	
}
public class Test12 {
	public static void main(String[] args) {
		Integer[] array = {10,20,30,1,2,0};
		GnericAlg<Integer> g1 = new GnericAlg<Integer>();
		System.out.println(g1.findMaxVal(array));
	}
}
```

这块报错是因为编译器擦除到Object ，但是Object并没有实现Comparable接口，所以就没有compareTo这个方法!!!!!

事实上Integer类实现了这个 接口，所以我们就引入了泛型上界让它擦除到指定的类即可。

<T extends Comparable>

```java
class GnericAlg<T>{
	public static<T extends Comparable>T findMaxVal(T[] array){
		T maxVal = array[0];
		for(int i = 1;i < array.length;i++) {
			if(maxVal.compareTo(array[i]) < 0){
				maxVal = array[i];
			}
		}
		return maxVal;
	}
	
}
public class Test12 {
	public static void main(String[] args) {
		Integer[] array = {10,20,30,1,2,0};
		GnericAlg<Integer> g1 = new GnericAlg<Integer>();
		System.out.println(g1.findMaxVal(array));
	}
}
```

![img](assets/20180601000623932.png)

接下来用我们自定义的类实现一下

```java
class GnericAlg<T extends Comparable<T>>{
	
	public static<T extends Comparable<T>> T findMaxVal(T[] array){//泛型方法！！！
		T maxVal = array[0];
		for(int i = 1;i < array.length;i++) {
			if(maxVal.compareTo(array[i]) < 0){
				maxVal = array[i];
			}
		}
		return maxVal;
	}
}
class Usr implements Comparable<Usr>{
	private String name;
	private String sex;
	private int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Usr(String name, String sex, int i) {
		super();
		this.name = name;
		this.sex = sex;
		this.age = i;
	}
	@Override
	public String toString() {
		return "Usr [name=" + name + ", sex=" + sex + ", age=" + age + "]";
	}
	@Override
	public int compareTo(Usr o) {//重写compareTo方法
		// TODO Auto-generated method stub
		return age > o.age ? 1 :(age == o.age) ? 0 : -1;
	}
	
	
}
public class Test22 {
	public static void main(String[] args) {
		Usr[] usr = new Usr[3];
		usr[0] = new Usr("加盟","woman",18);
		usr[1] = new Usr("加盟1","woman",19);
		usr[2] = new Usr("加盟2","woman",20);
		//System.out.println(GnericAlg.<Usr>findMaxVal(usr));//如果没有指定泛型类型，泛型会通过参数类型自动推演
		System.out.println(GnericAlg.findMaxVal(usr));
	}
```

![img](assets/20180601001239427.png)