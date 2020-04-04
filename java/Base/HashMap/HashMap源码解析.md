## Java1.7 HashMap扩容节点转移分析



JDK1.7 HashMap由数组和链表组成

JDK1.8 加入了红黑树(链表长度>=8链表转红黑树)



HashMap数组初始化长度**16**

```java
static final int DEFAULT_INITIAL_CAPACITY = 1<<4;	//16
loadFactor=0.75;
```



### 为什么初始容量要是2的指数次幂

HashMap的初始容量必须是2的指数次幂

如果传进来的初始容量不是2的指数次幂的话，初始化函数会自动将你数向上转成最靠近2的指数次幂的数



HashMap中put和get方法计算索引index

```java
JDK1.7
	static int indexFor(int h, int length) {
    	return h & (length - 1)
	}
    
	index = hash%index
	hash%index = hash & (length - 1)		//length是2的指数次幂的时候才成立

	hash%index != hash & (length - 1)		//当length不为2的指数次幂的时候
```

```java
JDK1.8
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```



在计算机中

加法>乘法>除法>取模



```
//例如
hashcode: 1101 0010 1010 1010
length  : 0000 0000 0000 1111

// 做与运算的时候后四位总是在0-15区间内,因此不会出现越界的问题，以及减小碰撞
```

### 为什么加载因子默认是0.75

在查询时间与空间之处最好的平衡点

哈希冲突和空间利用

### 为什么链表长度为8的时候转成红黑树

数组长度大于64，链表长度大于8转换为红黑树,小于6的时候又转为链表

```java
    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     */
    static final int TREEIFY_THRESHOLD = 8;

	/**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;


	/**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    static final int MIN_TREEIFY_CAPACITY = 64;
```



遵循泊松分布



### Java7的HashMap扩容死锁演示与环链行程分析





### Java8HashMap扩容优化





### concurrenthashmap线程安全吗？





---

## CSDN

### HashMap的长度为什么要是2的幂次方

HashMap存储数据时要避免位置碰撞且数据分配均匀，于是采用位移运算的算法计算存储链表的位置，假设HashMap的长度不为2的幂次方则有可能产生碰撞

### 为什么加载因子默认是0.75

HashMap源码中的加载因子

```java
static final float DEFAULT_LOAD_FACTOR = 0.75f;
```

当时想到的是应该是“哈希冲突”和“空间利用率”矛盾的一个折衷。
跟数据结构要么查询快要么插入快一个道理，hashmap就是一个插入慢、查询快的数据结构。

**加载因子**是表示Hsah表中元素的填满的程度。
加载因子越大,填满的元素越多,空间利用率越高，但冲突的机会加大了。
反之,加载因子越小,填满的元素越少,冲突的机会减小,但空间浪费多了。



### 为什么链表长度为8的时候转成红黑树

为了配合使用分布良好的hashCode，树节点很少使用。并且在理想状态下，受随机分布的hashCode影响，链表中的节点遵循泊松分布，而且根据统计，链表中节点数是8的概率已经接近千分之一，而且此时链表的性能已经很差了。所以在这种比较罕见和极端的情况下，才会把链表转变为红黑树。因为链表转换为红黑树也是需要消耗性能的，特殊情况特殊处理，为了挽回性能，权衡之下，才使用红黑树，提高性能。也就是大部分情况下，hashmap还是使用的链表，如果是理想的均匀分布，节点数不到8，hashmap就自动扩容了。



为什么链表长度为8的时候转成红黑树