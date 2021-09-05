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