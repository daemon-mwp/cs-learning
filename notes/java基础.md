# 一、Java基础

## Java8新特性

### 常用特性

1. Lambda表达式和函数式接口

   ```
   Arrays.asList( "a", "b", "d" ).forEach( e -> System.out.println( e ) );
   ```

2. 接口的默认方法和静态方法

3. 方法引用

   第一种方法引用的类型是构造器引用，语法是**Class::new**，或者更一般的形式：**Class<T>::new**。注意：这个构造器没有参数；

   第二种方法引用的类型是静态方法引用，语法是**Class::static_method**。注意：这个方法接受一个Car类型的参数；

   第三种方法引用的类型是某个类的成员方法的引用，语法是**Class::method**，注意，这个方法没有定义入参；

   第四种方法引用的类型是某个实例对象的成员方法的引用，语法是**instance::method**。注意：这个方法接受一个Car类型的参数。

4. 取消永久代

   使用Metaspace（JEP 122）代替持久代（PermGen space）。

5. 流式编程

```
final Collection< Task > tasks = Arrays.asList(
    new Task( Status.OPEN, 5 ),
    new Task( Status.OPEN, 13 ),
    new Task( Status.CLOSED, 8 ) 
);

// Calculate total points of all active tasks using sum()
final long totalPointsOfOpenTasks = tasks
    .stream()
    .filter( task -> task.getStatus() == Status.OPEN )
    .mapToInt( Task::getPoints )
    .sum();
 
System.out.println( "Total points: " + totalPointsOfOpenTasks );
//输出
//Total points: 18


// Calculate total points of all tasks
final double totalPoints = tasks
   .stream()
   .parallel()
   .map( task -> task.getPoints() ) // or map( Task::getPoints ) 
   .reduce( 0, Integer::sum );
 
System.out.println( "Total points (all tasks): " + totalPoints );

//输出
//Total points（all tasks）: 26.0

//list.parallelStream()并行流式处理
```

### 常见问题

#### 流式处理的性能

对于简单操作，比如最简单的遍历，Stream串行API性能明显差于显示迭代，但并行的Stream API能够发挥多核特性。

对于复杂操作，Stream串行API性能可以和手动实现的效果匹敌，在并行执行时Stream API效果远超手动实现。

所以，如果出于性能考虑，

对于简单操作推荐使用外部迭代手动实现

对于复杂操作，推荐使用Stream API，

在多核情况下，推荐使用并行Stream API来发挥多核优势

单核情况下不建议使用并行Stream API

## 多线程

### 1.Synchronized和lock底层实现原理

**synchronized:** 底层使用指令码方式来控制锁的，映射成字节码指令就是增加来两个指令：monitorenter和monitorexit。当线程执行遇到monitorenter指令时会尝试获取内置锁，如果获取锁则锁计数器+1，如果没有获取锁则阻塞；当遇到monitorexit指令时锁计数器-1，如果计数器为0则释放锁。

**Lock:** 底层是CAS乐观锁，依赖AbstractQueuedSynchronizer类，把所有的请求线程构成一个CLH队列。而对该队列的操作均通过Lock-Free（CAS）操作。

### 2.Synchronized和lock的区别

**存在层次上**

**synchronized:** Java的关键字，在jvm层面上

**Lock:** 是一个接口

**锁的释放**

**synchronized:** 1、以获取锁的线程执行完同步代码，释放锁 2、线程执行发生异常，jvm会让线程释放锁

**Lock:** 在finally中必须释放锁，不然容易造成线程死锁

**锁的获取**

**synchronized:** 假设A线程获得锁，B线程等待。如果A线程阻塞，B线程会一直等待

**Lock:** 分情况而定，Lock有多个锁获取的方式，大致就是可以尝试获得锁，线程可以不用一直等待(可以通过tryLock判断有没有锁)

**锁的释放（死锁产生）**

**synchronized:** 在发生异常时候会自动释放占有的锁，因此不会出现死锁

**Lock:** 发生异常时候，不会主动释放占有的锁，必须手动unlock来释放锁，可能引起死锁的发生

**锁的状态**

**synchronized:** 无法判断

**Lock:** 可以判断

**锁的类型**

**synchronized:** 可重入 不可中断 非公平

**Lock:** 可重入 可判断 可公平（两者皆可）

**性能**

**synchronized:** 少量同步

**Lock:** 大量同步

- Lock可以提高多个线程进行读操作的效率。（可以通过readwritelock实现读写分离）
- 在资源竞争不是很激烈的情况下，Synchronized的性能要优于ReetrantLock，但是在资源竞争很激烈的情况下，Synchronized的性能会下降几十倍，但是ReetrantLock的性能能维持常态；
- ReentrantLock提供了多样化的同步，比如有时间限制的同步，可以被Interrupt的同步（synchronized的同步是不能Interrupt的）等。在资源竞争不激烈的情形下，性能稍微比synchronized差点点。但是当同步非常激烈的时候，synchronized的性能一下子能下降好几十倍。而ReentrantLock确还能维持常态。

**调度**

**synchronized:** 使用Object对象本身的wait 、notify、notifyAll调度机制

**Lock:** 可以使用Condition进行线程之间的调度

**用法**

**synchronized:** 在需要同步的对象中加入此控制，synchronized可以加在方法上，也可以加在特定代码块中，括号中表示需要锁的对象。

**Lock:** 一般使用ReentrantLock类做为锁。在加锁和解锁处需要通过lock()和unlock()显示指出。所以一般会在finally块中写unlock()以防死锁。

**总结**

到了JDK1.6，发生了变化，对synchronize加入了很多优化措施，有自适应自旋，锁消除，锁粗化，轻量级锁，偏向锁等等。导致在JDK1.6上synchronize的性能并不比Lock差。官方也表示，他们也更支持synchronize，在未来的版本中还有优化余地，所以还是提倡在synchronized能实现需求的情况下，优先考虑使用synchronized来进行同步。

### 3.Synchronized不同的作用域

synchronized 关键字主要有以下几种用法：

- 非静态方法的同步；
- 静态方法的同步；
- 代码块。

### 4.锁升级过程

在 `synchronized` 最初的实现方式是 “**阻塞或唤醒一个Java线程需要操作系统切换CPU状态来完成，这种状态切换需要耗费处理器时间，如果同步代码块中内容过于简单，这种切换的时间可能比用户代码执行的时间还长”**，这种方式就是 `synchronized`实现同步最初的方式，这也是当初开发者诟病的地方，这也是在JDK6以前 `synchronized`效率低下的原因，JDK6中为了减少获得锁和释放锁带来的性能消耗，引入了“偏向锁”和“轻量级锁”。

所以目前锁状态一种有四种，从级别由低到高依次是：**无锁、偏向锁，轻量级锁（自旋锁），重量级锁**，锁状态只能升级，不能降级

### 5.CAS和synchronized区别

- 对于资源竞争较少的情况，使用synchronized同步锁进行线程阻塞和唤醒切换以及用户态内核态间的切换操作额外浪费消耗cpu资源；而CAS基于硬件实现，不需要进入内核，不需要切换线程，操作自旋几率较少，因此可以获得更高的性能。
- 对于资源竞争严重的情况，CAS自旋的概率会比较大，从而浪费更多的CPU资源，效率低于synchronized。
- synchronized在jdk1.6之后，已经改进优化。synchronized的底层实现主要依靠Lock-Free的队列，基本思路是自旋后阻塞，竞争切换后继续竞争锁，稍微牺牲了公平性，但获得了高吞吐量。在线程冲突较少的情况下，可以获得和CAS类似的性能；而线程冲突严重的情况下，性能远高于CAS。

### 6.Executors和ThreaPoolExecutor创建线程池的区别

**Executors 各个方法的弊端：**

- newFixedThreadPool 和 newSingleThreadExecutor:
  主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至 OOM。
- newCachedThreadPool 和 newScheduledThreadPool:
  主要问题是线程数最大数是 Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至 OOM。

**ThreaPoolExecutor：**

创建线程池方式只有一种，就是走它的构造函数，参数自己指定

### 7.线程池参数

- corePoolSize：核心线程数

  线程池维护的最小线程数量，核心线程创建后不会被回收（注意：设置allowCoreThreadTimeout=true后，空闲的核心线程超过存活时间也会被回收）。

- maximumPoolSize：最大线程数

  线程池允许创建的最大线程数量。

  当添加一个任务时，核心线程数已满，线程池还没达到最大线程数，并且没有空闲线程，工作队列已满的情况下，创建一个新线程并执行。

- keepAliveTime：空闲线程存活时间

  当一个可被回收的线程的空闲时间大于keepAliveTime，就会被回收。

- unit：时间单位

  keepAliveTime的时间单位

- workQueue：工作队列

  存放待执行任务的队列：当提交的任务数超过核心线程数大小后，再提交的任务就存放在工作队列，任务调度时再从队列中取出任务。它仅仅用来存放被execute()方法提交的Runnable任务。工作队列实现了BlockingQueue接口。

  JDK默认的工作队列有五种：

  **ArrayBlockingQueue** 数组型阻塞队列：数组结构，初始化时传入大小，有界，FIFO，使用一个重入锁，默认使用非公平锁，入队和出队共用一个锁，互斥。
  **LinkedBlockingQueue** 链表型阻塞队列：链表结构，默认初始化大小为Integer.MAX_VALUE，有界（近似无解），FIFO，使用两个重入锁分别控制元素的入队和出队，用Condition进行线程间的唤醒和等待。
  **SynchronousQueue** 同步队列：容量为0，添加任务必须等待取出任务，这个队列相当于通道，不存储元素。
  **PriorityBlockingQueue** 优先阻塞队列：无界，默认采用元素自然顺序升序排列。
  **DelayQueue** 延时队列：无界，元素有过期时间，过期的元素才能被取出。

- handler：拒绝策略

  AbortPolicy：丢弃任务并抛出RejectedExecutionException异常。

  DiscardPolicy：丢弃任务，但是不抛出异常。可能导致无法发现系统的异常状态。

  DiscardOldestPolicy：丢弃队列最前面的任务，然后重新提交被拒绝的任务。

  CallerRunsPolicy：由调用线程处理该任务。

- threadFactory：线程工厂

  创建线程的工厂，可以设定线程名、线程编号等。

# 二、常见框架

## **spring**

### 1. Spring IOC 循环依赖

Spring 避免循环依赖出现的错误，使用了三层缓存：

- 单例缓存 singletonObjects：存放填充完毕的，实际的 BeanDefinition；
- Bean 定义缓存 earlySingletonObjects：存放未填充的 BeanDeinition (属性值全为 null)，用于解决循环依赖问题；
- 工厂缓存 singletonFactories：存放单例 Bean 的工厂对象，在循环依赖问题中用来辅助解决问题。
  - singletonFactories 的 key 为 beanName，value 为该 bean 对应的 bean 工厂；这样一个 bean 就可以通过 beanName 从对应的 bean 工厂中找到对应的 bean。

```
public Object getSingleton(String beanName){
    //参数true设置标识允许早期依赖
    return getSingleton(beanName,true);
}
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    //检查缓存中是否存在实例
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        //如果为空，则锁定全局变量并进行处理。
        synchronized (this.singletonObjects) {
            //如果此bean正在加载，则不处理
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {  
                //当某些方法需要提前初始化的时候则会调用addSingleFactory 方法将对应的ObjectFactory初始化策略存储在singletonFactories
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    //调用预先设定的getObject方法
                    singletonObject = singletonFactory.getObject();
                    //记录在缓存中，earlysingletonObjects和singletonFactories互斥
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return (singletonObject != NULL_OBJECT ? singletonObject : null);
}
```

以 classA 和 classB 为例，假设两个实例对象存在循环依赖关系，且 classA 对象首先在 Spring 容器中初始化。

构建 classA 对象的未填充 BeanDefinition 对象，并置入 earlySingletonObjects，同时**将该 bean 从工厂缓存 singletonFactories 中除掉**，为解决循环依赖做准备；
尝试向 classA 对象中填充内容，且填充过程到需要填充 classB 对象；
首先分别尝试从完全实例化完毕的单例缓存 singletonObjects 和不完全实例化的 earlySingletonObjects 中获取 classB 对象，都获取失败；
尝试初始化 classB 对象的 BeanDefinition。在初始化过程中，classB 对象需要引用到 classA 对象实例，此时出现了循环依赖的情况；
classB 对象尝试从 singletonObjects 中获取 classA，但获取失败（因为此时 classA 当前还在初始化过程中，所以没有放入 singletonObjects 中）；然后从 earlySingletonObjects 中获取 classA 的引用。
classB 获取到 classA 的引用后，可以继续完成实例化过程；
classB 实例化完成后，实例对象返回给 classA，然后 classA 完成其实例化过程。

### 2.MVC与三层架构

视图View

模型model

控制器controller

### 3.Spring Bean 的注入过程

- 对于 XML 文件配置的 Bean，读取 bean 的 xml 配置文件，将 bean 元素分别转换成一个 BeanDefinition 对象；
- 对于注解类的 Bean 对象，AnnotationConfigApplicationContext 很关键，它是 spring 加载、管理 bean 的最重要的类。主要包括：
  1. AnnotatedBeanDefinitionReader：用来加载 class 类型的配置信息，在它初始化的时候，会预先注册一些 BeanPostProcessor 和 BeanFactoryPostProcessor，为后续解析 Bean 和 Configuration 注解做准备；
  2. ClasspathBeanDefinitionScanner：将指定包下的类通过一定规则过滤后，将 Class 信息包装成 BeanDefinition 的形式，注册到 IOC 容器中；
  3. 然后通过 BeanDefinitionRegistry 将这些 bean 注册到beanFactory中，保存在它的一个 ConcurrentHashMap 中。

# 三、计算机网络

# 四、系统设计

# 五、数据库

## MySQL

### 1.MyIsam和InnoDb区别

**InnoDB**
是 **MySQL 默认的事务型存储引擎**，只有在需要它不支持的特性时，才考虑使用其它存储引擎。
实现了四个标准的隔离级别，默认级别是**可重复读（REPEATABLE READ）**。在可重复读隔离级别下，通过**多版本并发控制（MVCC）**+ **间隙锁（Next-Key Locking）防止幻影读**。
主索引是**聚簇索引**，在索引中保存了数据，从而避免直接读取磁盘，因此对查询性能有很大的提升。内部做了很多优化，包括从磁盘读取数据时采用的**可预测性读**、能够加快读操作并且自动创建的**自适应哈希索引**、能够加速插入操作的**插入缓冲区**等。

⽽其余的索引都作为辅助索引，**辅助索引的data域存储相应记录主键的值**⽽不是地址，这也是和MyISAM不同的地⽅。在根据主索引搜索时，直接找到key所在的节点即可取出数据；在根据辅助索引查找时，则需要先取出主键的值，再⾛⼀遍主索引。

支持真正的在线**热备份**。其它存储引擎不支持在线热备份，要获取一致性视图需要停止对所有表的写入，而在读写混合场景中，停止写入可能也意味着停止读取。

**MyISAM**

B+Tree叶节点的data域存放的是数据记录的地址。在索引检索的时候，⾸先按照B+Tree搜索算法搜索索引，如果指定的Key存在，则取出其 data 域的值，然后以 data 域的值为地址读取相应的数据记录。这被称为“**⾮聚簇索引**”。

设计简单，数据以紧密格式存储。对于只读数据，或者表比较小、可以容忍修复操作，则依然可以使用它。提供了大量的特性，包括压缩表、**空间数据索引**等。
**不支持事务**。
**不支持行级锁**，只能对整张表加锁，读取时会对需要读到的所有表加共享锁，写入时则对表加排它锁。但在表有读取操作的同时，也可以往表中插入新的记录，这被称为并发插入（CONCURRENT INSERT）。可以手工或者自动执行检查和修复操作，但是和事务恢复以及崩溃恢复不同，可能导致一些数据丢失，而且修复操作是非常慢的，**不支持崩溃后的安全恢复**。
如果指定了 DELAY_KEY_WRITE 选项，在每次修改执行完成时，不会立即将修改的索引数据写入磁盘，而是会写到内存中的键缓冲区，只有在清理键缓冲区或者关闭表的时候才会将对应的索引块写入磁盘。这种方式可以**极大的提升写入性能**，但是在数据库或者主机崩溃时会造成索引损坏，需要执行修复操作。

### 2.事务四大特性(ACID)

1. **原子性**（Atomicity**）：** 事务是最⼩的执⾏单位，不允许分割。事务的原⼦性确保动作要么全部完成，要么完全不起作⽤；

2. **⼀致性（**Consistency**）：** 执⾏事务前后，数据保持⼀致，多个事务对同⼀个数据读取的结果是相同的；

3. **隔离性（**Isolation**）：** 并发访问数据库时，⼀个⽤户的事务不被其他事务所⼲扰，各并发事务之间数据库是独⽴的；

4. **持久性（**Durability**）：** ⼀个事务被提交之后。它对数据库中数据的改变是持久的，即使数据库发⽣故障也不应该对其有任何影响。

### 3.**并发事务带来哪些问题**

**脏读（Dirty read）**: 当⼀个事务正在访问数据并且对数据进⾏了修改，⽽这种修改还没有提交到数据库中，这时另外⼀个事务也访问了这个数据，然后使⽤了这个数据。因为这个数据是还没有提交的数据，那么另外⼀个事务读到的这个数据是“脏数据”，依据“脏数据”所做的操作可能是不正确的。

**丢失修改（Lost to modify**）: 指在⼀个事务读取⼀个数据时，另外⼀个事务也访问了该数据，那么在第⼀个事务中修改了这个数据后，第⼆个事务也修改了这个数据。这样第⼀个事务内的修改结果就被丢失，因此称为丢失修改。 例如：事务1读取某表中的数据A=20，事务2也读取A=20，事务1修改A=A-1，事务2也修改A=A-1，最终结果A=19，事务1的修改被**隔离级别** 

**不可重复读（Unrepeatableread）**: 指在⼀个事务内多次读同⼀数据。在这个事务还没有结束时，另⼀个事务也访问该数据。那么，在第⼀个事务中的两次读数据之间，由于第⼆个事务的修改导致第⼀个事务两次读取的数据可能不太⼀样。这就发⽣了在⼀个事务内两次读到的数据是不⼀样的情况，因此称为不可重复读。

**幻读（Phantom read）**: 幻读与不可重复读类似。它发⽣在⼀个事务（T1）读取了⼏⾏数据，接着另⼀个并发事务（T2）插⼊了⼀些数据时。在随后的查询中，第⼀个事务（T1）就会发现多了⼀些原本不存在的记录，就好像发⽣了幻觉⼀样，所以称为幻读。

### 4.**事务隔离级别有哪些**

- READ-UNCOMMITTED(读取未提交)： 最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读。
- READ-COMMITTED(读取已提交)： 允许读取并发事务已经提交的数据，可以阻⽌脏读，但是幻读或不可重复读仍有可能发⽣。
- REPEATABLE-READ(可重复读)： 对同⼀字段的多次读取结果都是⼀致的，除⾮数据是被本身事务⾃⼰所修改，可以阻⽌脏读和不可重复读，但幻读仍有可能发⽣。
- SERIALIZABLE(可串⾏化)： 最⾼的隔离级别，完全服从ACID的隔离级别。所有的事务依次逐个执⾏，这样事务之间就完全不可能产⽣⼲扰，也就是说，该级别可以防⽌脏读、不可重复读以及幻读。

|  隔离级别  | 脏读 | 不可重复读 | 幻影读 |
| :--------: | :--: | :--------: | :----: |
| 读取未提交 |  ✔   |     ✔      |   ✔    |
| 读取已提交 |      |     ✔      |   ✔    |
|  可重复读  |      |            |   ✔    |
|  可串行化  |      |            |        |

### 5.大表优化

当MySQL单表记录数过大时，数据库的CRUD性能会明显下降，⼀些常见的优化措施如下：
**限定数据的范围**
务必禁止不带任何限制数据范围条件的查询语句。⽐如：我们当⽤户在查询订单历史的时候，我们可以控制在⼀个⽉的范围内；
**读/写分离**
经典的数据库拆分⽅案，主库负责写，从库负责读；
**垂直分区**
根据数据库里面数据表的相关性进⾏拆分。 例如，用户表中既有⽤户的登录信息⼜有⽤户的基本信息，可以将⽤户表拆分成两个单独的表，甚⾄放到单独的库做分库。简单来说垂直拆分是指数据表列的拆分，把⼀张列⽐较多的表拆分为多张表。

- **垂直拆分的优点**： 可以使得列数据变⼩，在查询时减少读取的Block数，减少I/O次数。此外，垂直分区可以简化表的结构，易于维护。
- **垂直拆分的缺点**： 主键会出现冗余，需要管理冗余列，并会引起Join操作，可以通过在应⽤层进⾏Join来解决。此外，垂直分区会让事务变得更加复杂；

**⽔平分区**

- **⽔平拆分优点**：⽔平拆分是指数据表⾏的拆分，表的⾏数超过200万⾏时，就会变慢，保持数据表结构不变，通过某种策略存储数据分⽚。这样每⼀⽚数据分散到不同的表或者库中，达到了分布式的⽬的。 ⽔平拆分可以**⽀撑⾮常⼤的数据量**，**应⽤端改造也少**。
- **⽔平拆分缺点**：分表仅仅是解决了单⼀表数据过⼤的问题，但由于表的数据还是在同⼀台机器上，其实对于提升MySQL并发能⼒没有什么意义，所以⽔平拆分最好分库 。**分⽚事务难以解决** ，**跨节点Join性能差**，**逻辑复杂**。《Java⼯程师修炼之道》的作者推荐尽量不要对数据进⾏分⽚，因为拆分会带来逻辑、部署、运维的各种复杂度 ，⼀般的数据表在优化得当的情况下⽀撑千万以下的数据量是没有太⼤问题的。如果实在要分⽚，尽量选择**客户端分⽚架构**，这样可以减少⼀次和中间件的⽹络I/O。

下面补充⼀下数据库分⽚的两种常见方案：

- **客户端代理**： 分⽚逻辑在应⽤端，封装在jar包中，通过修改或者封装JDBC层来实现。 当当⽹的 Sharding-JDBC 、阿⾥的TDDL是两种⽐较常⽤的实现。
- **中间件代理**： 在应⽤和数据中间加了⼀个代理层。分⽚逻辑统⼀维护在中间件服务中。 我们现在谈的 Mycat 、360的Atlas、⽹易的DDB等等都是这种架构的实现。

**Sharding 存在的问题**

- **事务问题**
  使用分布式事务来解决，比如 XA 接口。

- **连接**
  可以将原来的连接分解成多个单表查询，然后在用户程序中进行连接。

- **ID 唯一性**

  **UUID**：不适合作为主键，因为太⻓了，并且⽆序不可读，查询效率低。比较适合⽤于⽣成唯⼀的名字的标示⽐如⽂件的名字。
  **数据库⾃增 id** : 两台数据库分别设置不同步⻓，⽣成不重复ID的策略来实现⾼可⽤。这种⽅式⽣成的 id 有序，但是需要独⽴部署数据库实例，成本⾼，还会有性能瓶颈。
  **利⽤ redis ⽣成 id** : 性能⽐᫾好，灵活⽅便，不依赖于数据库。但是，引⼊了新的组件造成系统更加复杂，可⽤性降低，编码更加复杂，增加了系统成本。
  **Twitter的snowflake算法** ：Github 地址：https://github.com/twitter-archive/snowflake。
  **美团的Leaf分布式ID⽣成系统** ：Leaf 是美团开源的分布式ID⽣成器，能保证全局唯⼀性、趋势递增、单调递增、信息安全

### 6.解释⼀下什么是池化设计思想。什么是数据库连接池?为什么需要数据库连接池?

池化设计会初始预设资源，解决的问题就是抵消每次获取资源的消耗，如创建线程的开销，获取远程连接的开销等。

### 7.MySQL 索引

索引是在存储引擎层实现的，而不是在服务器层实现的，所以不同存储引擎具有不同的索引类型和实现。

#### B+Tree

B+Tree 索引是**大多数 MySQL 存储引擎的默认索引类型**。因为不再需要进行全表扫描，只需要对树进行搜索即可，所以查找速度快很多。因为 B+ Tree 的有序性，所以除了用于查找，还可以用于排序和分组。可以指定多个列作为索引列，多个索引列共同组成键。适用于全键值、键值范围和键前缀查找，其中键前缀查找只适用于**最左前缀查找**。如果不是按照索引列的顺序进行查找，则无法使用索引。

#### **全文索引**

MyISAM 存储引擎支持全文索引，用于查找文本中的关键词，而不是直接比较是否相等。查找条件使用 MATCH AGAINST，而不是普通的 WHERE。

#### **哈希索引**

哈希索引能以 O(1) 时间进行查找，但是失去了有序性：
无法用于排序与分组；
只支持精确查找，无法用于部分查找和范围查找。
InnoDB 存储引擎有一个特殊的功能叫“**自适应哈希索引**”，当某个索引值被使用的非常频繁时，会在 B+Tree 索引之上再创建一个哈希索引，这样就让 B+Tree 索引具有哈希索引的一些优点，比如快速的哈希查找。

#### **空间数据索引**

MyISAM 存储引擎支持空间数据索引（R-Tree），可以用于地理数据存储。空间数据索引会从所有维度来索引数据，可以有效地使用任意维度来进行组合查询。



### 8.B+Tree和B-Tree

B+树是B-树的变体，也是一种多路搜索树, 它与 B- 树的不同之处在于:

- 所有关键字存储在叶子节点出现,内部节点(非叶子节点并不存储真正的 data)

  B+树内节点不存储数据，所有 data 存储在叶节点导致查询时间复杂度固定为 log n。而B-树查询时间复杂度不固定，与 key 在树中的位置有关，最好为O(1)。

- 为所有叶子结点增加了一个链指针

  B+树叶节点两两相连可大大增加区间访问性，可使用在范围查询等，而B-树每个节点 key 和 data 在一起，则无法区间查找。

  根据空间局部性原理：如果一个存储器的某个位置被访问，那么将它附近的位置也会被访问。B+树可以很好的利用局部性原理，若我们访问节点 key为 50，则 key 为 55、60、62 的节点将来也可能被访问，我们可以利用磁盘预读原理提前将这些数据读入内存，减少了磁盘 IO 的次数。
  当然B+树也能够很好的完成范围查询。

### 9.B+Tree和红黑树

**（一）更少的查找次数**
平衡树查找操作的时间复杂度和树高 h 相关，O(h)=O(logdN)，其中 d 为每个节点的出度。
红黑树的出度为 2，而 B+ Tree 的出度一般都非常大，所以红黑树的树高 h 很明显比 B+ Tree 大非常多，查找的次数
也就更多。
**（二）利用磁盘预读特性**
为了减少磁盘 I/O 操作，磁盘往往不是严格按需读取，而是每次都会预读。预读过程中，磁盘进行顺序读取，顺序读
取不需要进行磁盘寻道，并且只需要很短的磁盘旋转时间，速度会非常快。
操作系统一般将内存和磁盘分割成固定大小的块，每一块称为一页，内存与磁盘以页为单位交换数据。数据库系统将
索引的一个节点的大小设置为页的大小，使得一次 I/O 就能完全载入一个节点。并且可以利用预读特性，相邻的节点
也能够被预先载入。

### 10.索引优化

