## 事务管理(ACID)

- 原子性（Atomicity）
  原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
- 一致性（Consistency）
  事务前后数据的完整性必须保持一致。
- 隔离性（Isolation）
  事务的隔离性是多个用户并发访问数据库时，数据库为每一个用户开启的事务，不能被其他事务的操作数据所干扰，多个并发事务之间要相互隔离。
- 持久性（Durability）
  持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发生故障也不应该对其有任何影响

## 事务的隔离级别

- 脏读

  指一个事务读取了另外一个事务未提交的数据。

- 不可重复读：

  在一个事务内读取表中的某一行数据，多次读取结果不同。（这个不一定是错误，只是某些场合不对）

- 虚读(幻读)

  是指在一个事务内读取到了别的事务插入的数据，导致前后读取不一致。

## 四种隔离界别设置

### 数据库

```sql
set transaction isolation level 
#设置事务隔离级别
```

```sql
select @@tx_isolation 
##查询当前事务隔离级别
```

| 设置 | 描述 |
| ---- | ---- |
|Serializable|	可避免脏读、不可重复读、虚读情况的发生。（串行化）|
|Repeatable read|	可避免脏读、不可重复读情况的发生。（可重复读）|
|Read committed|	可避免脏读情况发生（读已提交）。|
|Read uncommitted|	最低级别，以上情况均无法保证。(读未提交)|

### Java

适当的 Connection 方法，比如 setAutoCommit 或 setTransactionIsolation

|设置|	描述|
| ----| ----|
|TRANSACTION_SERIALIZABLE|	指示不可以发生脏读、不可重复读和虚读的常量。|
|TRANSACTION_REPEATABLE_READ|	指示不可以发生脏读和不可重复读的常量；虚读可以发生。|
|TRANSACTION_READ_UNCOMMITTED|	指示可以发生脏读 (dirty read)、不可重复读和虚读 (phantom read) 的常量。|
|TRANSACTION_READ_COMMITTED|	指示不可以发生脏读的常量；不可重复读和虚读可以发生。|

### 隔离级别关系

| 隔离级别                   | 脏读(Dirty Read) | 不可重复读(NonRepeatable Read) | 幻读(Phantom Read) |
| -------------------------- | ---------------- | ------------------------------ | ------------------ |
| 未提交读(Read Uncommitted) | ==可能==         | ==可能==                   | ==可能==       |
| 已提交读(Read committed)|不可能|==可能==| ==可能== |
| 可重复读(Repeateable read)|不可能|不可能| ==可能== |
| 可串行化(Serializble)| 不可能|不可能|不可能|

