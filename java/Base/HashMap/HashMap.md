## 一、HashMap集合简介

**特点：**

- HashMap是Map接口的一个重要实现类，基于哈希表，以key-value的形式存储数据，线程不安全；
- null可以作为键，这样的键只能有一个，可以有一个或多个键对应的值为null；
- 存取元素无序。

**底层数据结构：**

- JDK1.8之前，由数组+链表构成，数组是存储数据的主体，链表是为了解决哈希冲突而存在的；
- JDK1.8以后，由数组+链表+红黑树构成，当链表长度大于阈值（默认为8），并且数组长度大于64时，链表会转化为红黑树去解决哈希冲突。

**注意：** 链表转化为红黑树之前会进行判断，若果阈值大于8，但是数组长度小于64，这时链表不会转化为红黑树去存储数据，而是会对数组进行扩容。

**这样做的原因：** 如果数组比较小，应尽量避免红黑树结构。因为红黑树结构较为复杂，红黑树又称为平衡二叉树，需要进行左旋、右旋、变色这些操作才能保证平衡。在数组容量较小的情况下，操作数组要比操作红黑树更节省时间。综上所述：为了提高性能以及减少搜索时间，在阈值大于8并且数组长度大于64的情况下链表才会转化为红黑树而存在。具体参考treeifyBin方法。



**HashMap存储数据结构图：**

![img](assets/v2-fe4e0b474a5bb0052f804e1e9ea29f9b_720w.jpg)



## 二、HashMap底层存储数据的过程

### 1.以下面代码所示进行分析：

```java
package hashmap_demo;
import java.util.HashMap;
public class HashMapTest {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("柳岩", 18);
        map.put("杨幂", 28);
        map.put("刘德华", 40);
        map.put("柳岩", 20);
        System.out.println(map);
    }
}
//输出结果：{杨幂=28, 柳岩=20, 刘德华=40}
```

### 2.HashMap存储过程图：

![img](assets/v2-ba159ae712005509e13ef7f67ff85dda_720w.jpg)

### 3.存储过程分析：

1.当执行`HashMap<String, Integer> map = new HashMap<>();`这行代码创建HashMap实例对象时；在JDK1.8之前，会在构造方法中创建一个长度为16 的Entry[] table数组用来存储键值对；JDK1.8之后，创建数组的时机发生了变化，不是在构造方法中创建数组了，而是在第一次调用put()方法时（即第一次向HashMap中添加元素）创建Node[] table数组。

**注意：** 创建HashMap实例对象在JDK1.8前后发生了变化，主要有两点：创建的时机发生了变化；数组类型发生了变化，由原来的Entry[]类型变为Node[]类型。

2.向哈希表中存储柳岩-18，会根据柳岩调用String类中重写后的hashCode()方法计算出柳岩对应的哈希值，然后结合数组长度采用某种算法计算出柳岩在Node[]数组中的索引值。如果该索引位置上无数据，则直接将柳岩-18插入到该索引位置。比如计算出柳岩对应的索引为3，如上图所示。

面试题：哈希表底层采用那种算法计算出索引值？还有哪些算法计算索引值？

答：采用key的hashCode()方法计算出哈希值，然后结合数组长度进行无符号右移（>>>）、按位异或（^）、按位与（&）计算出索引值；还可以采用平方取中法、取余数、伪随机数法。

**取余数：10%8=2 11%8=3；位运算效率最高，其他方式效率较低。**

3.向哈希表中存储杨幂-28，计算出该索引位置无数据，直接插入。

4.向哈希表中存储刘德华-40，假设刘德华计算出的索引也是3，那么此时该索引位置不为null，这时底层会比较柳岩和刘德华的哈希值是否一致，如果不一致，则在此索引位置上划出一个节点来存储刘德华-40，这种方式称为拉链法。

补充：索引计算源码p = tab[i = (n - 1) & hash]，即索引=哈希值&（数组长度-1），按位与运算等价于取余运算，因为11%8=3，19%8=3，所以会出现索引相同，数组长度相同，但哈希值不同的情况。

5.最后向哈希表中存储柳岩-20，柳岩对应的索引值为3。因为该索引位置已有数据，所以此时会比较柳岩与该索引位置上的其他数据的哈希值是否相等，如果相等，则发生哈希碰撞。此时底层会调用柳岩所属String字符串类中的equals()方法比较两个对象的内容是否相同：

相同：则后添加数据的value值会覆盖之前的value值，即柳岩-20覆盖掉柳岩-18。

不相同：继续和该索引位置的其他对象进行比较，如果都不相同，则向下划出一个节点存储（拉链法）。

**注意点：如果一个索引位置向下拉链，即链表长度大于阈值8且数组长度大于64，则会将此链表转化为红黑树。因为链表的时间复杂度为O（N），红黑树的时间复杂度为O（logN），链表长度多大时O（N）>O（logN）。**



## 三、HashMap的扩容机制

### 1.HashMap什么时候进行扩容？

首先看添加元素的put()方法流程：

![img](assets/v2-b8237fd3494a293ac482bd8e1ef598af_720w.jpg)

**说明：**

- 上图中的size表示HashMap中K-V的实时数量，不等于数组的长度；
- threshold（临界值）=capacity（数组容量）*loadFactory（加载因子），临界值表示当前已占用数组的最大值。size如果超过这个临界值进调用resize()方法进行扩容，扩容后的容量是原来的两倍；
- 默认情况下，16*0.75=12，即HashMap中存储的元素超过12就会进行扩容。

### 2.HashMap扩容后的大小是多少？

```text
是原来容量的2倍，即HashMap是以2n进行扩容的。
```

### 3.HashMap的默认初始容量是多少？

HashMap的无参构造，默认初始值为16，源码如下：

```text
/**
 * Constructs an empty <tt>HashMap</tt> with the default initial capacity
 * (16) and the default load factor (0.75).
 */
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
}
```

默认初始值源码：

```text
/**
 * The default initial capacity - MUST be a power of two.
 * 默认初始容量必须是2的幂
 */
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 
```

由源码可以看到，HashMap的默认初始容量为1左移4位，即1*2的4次方为16。如果使用HashMap的无参构造进行初始化，第一次put元素时，会触发resize()方法（扩容方法），扩容后的容量为16。这一点和ArrayList初始化过程很相似（使用ArrayList的无参构造初始化时，创建的是一个空数组，当第一次向空数组添加元素时会触发grow()扩容方法，扩容后的容量为10）。

### 4.指定初始容量为什么必须是2的幂？

HashMap的有参构造，即可以指定初始化容量大小，源码如下：

```text
/**
 * Constructs an empty <tt>HashMap</tt> with the specified initial
 * capacity and the default load factor (0.75).
 *
 * @param  initialCapacity the initial capacity.
 * @throws IllegalArgumentException if the initial capacity is negative.
 */
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
```

即构造一个指定容量和默认加载因子（0.75）的空HashMap。

由上面的内容我们知道，当向HashMap中添加元素时，首先会根据key的哈希值结合数组长度计算出索引位置。HashMap为了存取高效需要减少哈希碰撞，使数据分配均匀，采用按位与**hash&（length-1）**计算索引值。

HashMap采用取余的算法计算索引，即hash%length，但是取余运算不如位运算效率高，所以底层采用按位与**hash&（length-1）**进行运算。两种算法等价的前提就是length是2的n次幂。

### 5.为什么这样就能均匀分布？

我们需要知道两个结论：

- 2的n次方就是1后面n个0；如2的4次方为16，二进制表示为10000；
- 2的n次方-1就是n个1；比如2的4次方-1位15，二进制表示为1111。

举例说明为什么数组长度是2的n次幂可以均匀分布：

```text
按位与运算：相同二进制位上都是1，结果为1，否则为0。
假设数组长度为2的3次幂8，哈希值为3，即3&（8-1）=3，索引为3；
假设数组长度为2的3次幂8，哈希值为2，即2&（8-1）=2，索引为2；
运算过程如下：
3&（8-1）
0000 0011 -->3
0000 0111 -->7
----------------
0000 0011 -->3

2&（8-1）
0000 0010 -->2
0000 0111 -->7
----------------
0000 0010 -->2

结论：索引值不同，不同索引位置都有数据分布，分布均匀。
```

假设数组长度不是2的n次幂，比如长度为9，运算过程如下：

```text
假设数组长度为9，哈希值为3，即3&（9-1）=3，索引为0；
假设数组长度为9，哈希值为2，即2&（9-1）=2，索引为2；
运算过程如下：
3&（9-1）
0000 0011 -->3
0000 1000 -->8
----------------
0000 0000 -->0

2&（9-1）
0000 0010 -->2
0000 1000 -->8
----------------
0000 0000 -->0

结论：索引值都为0，导致同一索引位置上有很多数据，而其他索引位置没有数据，致使链表或红黑树过长，效率降低。
```

**注意：** hash%length等价于hash&（length-1）的前提条件是数组长度为2的n次幂。由于底层采用按位与运算计算索引值，所以需要保证数组长度必须为2的n次幂。

### 6.如果指定的初始容量不是2的n次幂会怎样？

```text
这时HashMap会通过位运算和或运算得到一个2的幂次方数，并且这个数是离指定容量最小的2的幂次数。比如初始容量为10，经过运算最后会得到16。
```

该过程涉及到的源码如下：

```text
//创建HashMap集合对象，并指定容量为10，不是2的幂
HashMap<String, Integer> map = new HashMap<>(10);
//调用有参构造
public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
//this关键字继续调用
public HashMap(int initialCapacity, float loadFactor) {//initialCapacity=10
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);//initialCapacity=10
}
//调用tableSizeFor()方法
/**
* Returns a power of two size for the given target capacity.
* 返回指定目标容量的2的幂。
*/
static final int tableSizeFor(int cap) {
     int n = cap - 1;
     n |= n >>> 1;
     n |= n >>> 2;
     n |= n >>> 4;
     n |= n >>> 8;
     n |= n >>> 16;
     return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

下面分析tableSizeFor()方法：

- int n = cap - 1;为什么要减1操作呢？

```text
这是为了防止`cpa`已经是2的幂了。如果`cpa`已经是2的幂，又没有执行减1的操作，则执行完下面的无符号右移后，返回的将为`cap`的2倍。
```

- n等与0时，返回1，这里不讨论你等于0的情况。
- |表示按位或运算：运算规则为相同二进制位上都是0，结果为0，否则为1。

第1次运算：

```text
int n = cap - 1;//cap=10，n=9
n |= n >>> 1;//无符号右移1位，然后再与n进行或运算
00000000 00000000 00000000 00001001  //n=9
00000000 00000000 00000000 00000100  //9无符号右移1位变为4
-----------------------------------------------
00000000 00000000 00000000 00001101  //按位或运算结果为13，即此时n=13
```

第2次运算：

```text
int n = 13
n |= n >>> 2;
00000000 00000000 00000000 00001101  //n=13
00000000 00000000 00000000 00000011  //13无符号右移2位变为3
------------------------------------------------
00000000 00000000 00000000 00001111  //按位或运算结果为15，即此时n=15
```

第3次运算：

```text
int n = 15
n |= n >>> 4;
00000000 00000000 00000000 00001111  //n=15
00000000 00000000 00000000 00000000  //15无符号右移4位变为0
------------------------------------------------
00000000 00000000 00000000 00001111  //按位或运算结果为15，即此时n=15
```

接下来的运算结果都是n=15，由于最后有一个n + 1操作，最后结果为16。

**总结：** 由以上运算过程可以看出，如果指定的初始容量不是2的n次幂，经过运算后会得到离初始容量最小的2幂。

## 四、HashMap源码分析

### 1.成员变量

```text
private static final long serialVersionUID = 362498820763181265L; //序列化版本号
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; //初始化容量，必须是2的n次幂
static final int MAXIMUM_CAPACITY = 1 << 30; //集合最大容量：2的30次幂
static final float DEFAULT_LOAD_FACTOR = 0.75f; //默认的加载因子
/**1.加载因子是用来衡量HashMap的疏密程度，计算HashMap的实时加载因子的方法为：size/capacity；
  *2.加载因子太大导致查找元素效率低，太小导致数组的利用率低，默认值为0.75f是官方给出的一个较好的临界值；
  *3.当HashMap里面容纳的元素已经达到HashMap数组长度的75%时，表示HashMap太挤了，需要扩容，而扩容这个过程涉及到rehash、复制数据等操作，非常消耗性能，所以开发中尽量减少扩容的次数，可以通过创建HashMap集合对象时指定初始容量来尽量避免扩容；
  *4.同时在HashMap的构造方法中可以指定加载因子大小。
  */
HashMap(int initialCapacity, float loadFactor) //构造一个带指定初始容量和加载因子的空HashMap
static final int TREEIFY_THRESHOLD = 8; //链表转红黑树的第一个条件，链表长度大于阈值8
static final int UNTREEIFY_THRESHOLD = 6; //删除红黑树节点时，当红黑树节点小于6，转化为链表
static final int MIN_TREEIFY_CAPACITY = 64; //链表转红黑树的第二个条件，数组长度大于64
```

## 五、常见面试题

### 1.发生哈希碰撞的条件是什么？

```text
两个对象的索引相同，并且hashCode（即哈希值）相等时，会发生哈希碰撞。
```

### 2.如何解决哈希冲突？

```text
JDK1.8之前，采用链表解决；JDK1.8之后，采用链表+红黑树解决。
```

### 3.如果两个key的hashCode相同，如何存储？

```text
使用equals比较内容是否相同：

相同：后添加的value值会覆盖之前的value值；

不相同：划出一个节点存储（拉链法）。
```

### 4.HashMap的底层数据结构？

JDK1.8：数组+链表+红黑树。其中数组是主体，链表和红黑树是为解决哈希冲突而存在的，具体如下图所示：

![img](assets/v2-07c14fc84557e38a9796867f1a11fa61_720w.jpg)

### 5.JDK1.8为什么引入了红黑树？红黑树结构不是更复杂吗？

JDK1.8以前HashMap的底层数据是数组+链表，我们知道，即使哈希函数做得再好，哈希表中的元素也很难达到百分之百均匀分布。当HashMap中有大量的元素都存在同一个桶（同一个索引位置），这个桶下就会产生一个很长的链表，这时HashMap就相当于是一个单链表的结构了，假如单链表上有n个元素，则遍历的时间复杂度就是O（n），遍历效率很低。针对这种情况，JDK1.8引入了红黑树，遍历红黑树的时间复杂度为O（logn)，由于O（n）>O（logn)；所以这一问题得到了优化。

### 6.为什么链表长度大于8才转化为红黑树？

我们知道8是从链表转成红黑树的阈值，在源码中有这样一段注释内容：

```text
/** Because TreeNodes are about twice the size of regular nodes, we use them only when     * bins contain enough nodes to warrant use (see TREEIFY_THRESHOLD). And when they         * become too small (due to removal or resizing) they are converted back to plain bins.   * In usages with well-distributed user hashCodes, tree bins are rarely used.  Ideally,   * under random hashCodes, the frequency of nodes in bins follows a Poisson distribution
  * (http://en.wikipedia.org/wiki/Poisson_distribution) with a parameter of about 0.5 on   * average for the default resizing threshold of 0.75, although with a large variance     * because of resizing granularity. Ignoring variance, the expected occurrences of list   * size k are (exp(-0.5) * pow(0.5, k) / factorial(k)). The first values are:
  *
  * 0:    0.60653066
  * 1:    0.30326533
  * 2:    0.07581633
  * 3:    0.01263606
  * 4:    0.00157952
  * 5:    0.00015795
  * 6:    0.00001316
  * 7:    0.00000094
  * 8:    0.00000006
  * more: less than 1 in ten million
  */
```

翻译过来的的值意思就是说：

**红黑树节点所占空间是普通链表节点的两倍，并且链表中存储数据的频率符合泊松分布，我们可以看到，在链表为8的节点上存储数据的概率是0.00000006，这也就表明超过8以后的节点存储数据的概率就非常小了。**

由上述分析可以得出：

- 如果小于阈值8就是用红黑树，会使得结构一开始就很复杂；
- 如果大于阈值8还使用链表，会导致链表节点不能被充分利用；
- 所以，阈值8是科学合理的一个值，是空间和时间的权衡值。

### 7.为什么加载因子设置为0.75？边界值是12？

- 如果加载因子是0.4，那么16*0.4=6，致使数组中满6个空间就扩容，造成数组利用率太低了；
- 如果加载因子是0.9，那么16*0.9=14，这样就会使数组太满，很大几率造成某一个索引节点下的链表过长，进而导致查找元素效率低；
- 所以兼顾数组利用率又考虑链表不要太长，经过大量测试0.75是最佳值。