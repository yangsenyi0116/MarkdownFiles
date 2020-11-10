# Optional概述

新版本的Java，比如Java 8引入了一个新的Optional类。Optional类的Javadoc描述如下：

> 这是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。

本文会逐个探讨Optional类包含的方法，并通过一两个示例展示如何使用。

# 实例演示

## of

> 为非null的值创建一个Optional。
> of方法通过工厂方法创建Optional类。需要注意的是，创建对象时传入的参数不能为null。如果传入参数为null，则抛出NullPointerException 。

![img](assets/871373-20170813213157835-225507591.png)

## ofNullable

> 为指定的值创建一个Optional，如果指定的值为null，则返回一个空的Optional。

![img](assets/871373-20170813213208038-889552024.png)

## isPresent

> 如果值存在返回true，否则返回false。

![img](assets/871373-20170813213217882-181052492.png)

## get

> 如果Optional有值则将其返回，否则抛出NoSuchElementException。

![img](assets/871373-20170813213225570-1344074077.png)

## ifPresent

> 如果Optional实例有值则为其调用consumer，否则不做处理

![img](assets/871373-20170813213232492-731711515.png)

## orElse

> 如果有值则将其返回，否则返回指定的其它值。

![img](assets/871373-20170813213240476-1813273803.png)

## orElseGet

> 

orElseGet与orElse方法类似，区别在于得到的默认值。orElse方法将传入的字符串作为默认值，orElseGet方法可以接受Supplier接口的实现用来生成默认值

![img](assets/871373-20170813213251101-2108030461.png)

## orElseThrow

> 

如果有值则将其返回，否则抛出supplier接口创建的异常。

在orElseGet方法中，我们传入一个Supplier接口。然而，在orElseThrow中我们可以传入一个lambda表达式或方法，如果值不存在来抛出异常。

![img](assets/871373-20170813213303023-658075236.png)

## map

> 

如果有值，则对其执行调用mapping函数得到返回值。如果返回值不为null，则创建包含mapping返回值的Optional作为map方法返回值，否则返回空Optional。

map方法用来对Optional实例的值执行一系列操作。通过一组实现了Function接口的lambda表达式传入操作。

![img](assets/871373-20170813213324023-1490583616.png)

## flatMap

> 如果有值，为其执行mapping函数返回Optional类型返回值，否则返回空Optional。

flatMap方法与map方法类似，区别在于mapping函数的返回值不同。map方法的mapping函数返回值可以是任何类型T，而flatMap方法的mapping函数必须是Optional。

参照map函数，使用flatMap重写的示例如下：

![img](assets/871373-20170813213358648-2044056169.png)

## filter

> 如果有值并且满足断言条件返回包含该值的Optional，否则返回空Optional。

这里可以传入一个lambda表达式。对于filter函数我们应该传入实现了Predicate接口的lambda表达式。

![img](assets/871373-20170813213405304-1210938238.png)

# 完整示例

```java
/* of */
    //调用工厂方法创建Optional实例
    Optional<String> name = Optional.of("YanWei");
    //传入参数为null，抛出NullPointerException.
    //Optional<String> someNull = Optional.of(null);

/*opNullable*/
    Optional empty = Optional.ofNullable(null);

/*isPresent*/
    if (name.isPresent()) {
        System.out.println(name.get());//输出YanWei
    }

/*get*/
    try {
        System.out.println(empty.get());
    } catch (NoSuchElementException ex) {
        System.err.println(ex.getMessage());
    }

/*ifPresent*/
    name.ifPresent((value) -> {
        System.out.println("The length of the value is: " + value.length());
    });

/*orElse*/
    System.out.println(empty.orElse("There is no value present!"));
    System.out.println(name.orElse("There is some value!"));

/*orElseGet*/
    System.out.println(empty.orElseGet(() -> "Default Value"));
    System.out.println(name.orElseGet(String::new));

/*orElseThrow*/
    try {
        empty.orElseThrow(IllegalArgumentException::new);
    } catch (Throwable ex) {
        System.out.println("error:" + ex.getMessage());
    }

/*map*/
    Optional<String> upperName = name.map((value) -> value.toUpperCase());
    System.out.println(upperName.orElse("No value found"));

/*flatMap*/
    upperName = name.flatMap((value) -> Optional.of(value.toUpperCase()));
    System.out.println(upperName.get());

/*filter*/
    List<String> names = Arrays.asList("YanWei","YanTian");
    for(String s:names)
    {
        Optional<String> nameLenLessThan7 = Optional.of(s).filter((value) -> value.length() < 7);
        System.out.println(nameLenLessThan7.orElse("The name is more than 6 characters"));
    }
```