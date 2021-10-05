# 一、Java基础

## Java集合

### List

存储的元素是有序的、可重复的。

#### 三种实现方式

1. ArrayList：基于动态数组实现，支持随机访问。
2. Vector：和 ArrayList 类似，但它是线程安全的。
3. LinkedList：基于双向链表实现，只能顺序访问，但是可以快速地在链表中间插入和删除元素。不仅如此，LinkedList 还可以用作栈、队列和双向队列。

#### 推荐的线程安全的List

1. SynchronizedList是Collections类的静态内部类，它能把所有 List 接口的实现类转换成线程安全的List，比 Vector 有更好的扩展性和兼容性。

2. CopyOnWriteArrayList

   **读写分离**

- 写操作在一个复制的数组上进行，读操作还是在原始数组中进行，读写分离，互不影响。

- 写操作需要加锁，防止并发写入时导致写入数据丢失。

- 写操作结束之后需要把原始数组指向新的复制数组。

  **缺点**

- 内存占用：在写操作时需要复制一个新的数组，使得内存占用为原来的两倍左右；

- 数据不一致：读操作不能读取实时性的数据，因为部分写操作的数据还未同步到读数组中。

  **适用场景**

- CopyOnWriteArrayList 在写操作的同时允许读操作，大大提高了读操作的性能，因此很适合读多写少的应用场景。

- CopyOnWriteArrayList 不适合内存敏感以及对实时性要求很高的场景。

#### ArrayList

因为 ArrayList 是基于数组实现的，所以支持快速随机访问。RandomAccess 接口标识着该类支持快速随机访问。

数组的默认大小为 10。

**删除元素** 

需要调用 System.arraycopy() 将 index+1 后面的元素都复制到 index 位置上，该操作的时间复杂度为 O(N)，可以看出 ArrayList 删除元素的代价是非常高的。

**扩容机制**

添加元素时使用 ensureCapacityInternal() 方法来保证容量足够，如果不够时，需要使用 grow() 方法进行扩容，新容量的大小为oldCapacity + (oldCapacity >> 1) ，也就是旧容量的 1.5 倍。

扩容操作需要调用 Arrays.copyOf() 把原数组整个复制到新数组中，这个操作代价很高，因此最好在创建ArrayList 对象时就指定大概的容量大小，减少扩容操作的次数。

### Set

存储的元素是⽆序的、不可重复的。

#### 三种实现方式

1. TreeSet：基于**红黑树**实现，支持有序性操作，例如根据一个范围查找元素的操作。但是查找效率不如HashSet，HashSet 查找的时间复杂度为 O(1)，TreeSet 则为 O(logN)。
2. HashSet：基于**哈希表**实现，支持快速查找，但不支持有序性操作。并且失去了元素的插入顺序信息，也就是说使用 Iterator 遍历 HashSet 得到的结果是不确定的。
3. LinkedHashSet：具有 HashSet 的查找效率，且内部使用双向链表维护元素的插入顺序。

#### 线程安全的Set 

- Collections.synchronizedSet
- CopyOnWriteArraySet

### Map

使⽤键值对（kye-value）存储。

#### 四种实现方式

1. TreeMap：基于红黑树实现。
2. HashMap：基于哈希表实现。
3. HashTable：和 HashMap 类似，但它是线程安全的，这意味着同一时刻多个线程可以同时写入 HashTable 并且不会导致数据不一致。它是遗留类，不应该去使用它。现在可以使用 ConcurrentHashMap 来支持线程安全，并且 ConcurrentHashMap 的效率会更高，因为 ConcurrentHashMap 引入了分段锁。
4. LinkedHashMap：使用双向链表来维护元素的顺序，顺序为插入顺序或者最近最少使用（LRU）顺序。

#### 线程安全的Map

- Hashtable

  HashTable的get/put方法都被synchronized关键字修饰，说明它们是方法级别阻塞的，它们占用共享资源锁，所以导致同时只能一个线程操作get或者put，而且get/put操作不能同时执行，所以这种同步的集合效率非常低，一般不建议使用这个集合。

- ConcurrentHashMap

  这个也是最推荐使用的线程安全的Map，也是实现方式最复杂的一个集合，每个版本的实现方式也不一样，在jdk8之前是使用分段加锁的一个方式，分成16个桶，每次只加锁其中一个桶，而在jdk8又加入了红黑树和CAS算法来实现。

- Collections.synchronizedMap

#### HashMap

以JDK1.7为例

##### 存储结构

内部包含了一个 Entry 类型的数组 table。

Entry 存储着键值对。它包含了四个字段，从 next 字段我们可以看出 Entry 是一个链表。即数组中的每个位置被当成一个桶，一个桶存放一个链表。HashMap 使用拉链法来解决冲突，同一个链表中存放哈希值和散列桶取模运算结果相同的 Entry。 

##### 拉链法的工作原理

**插入**新的键值对时，先对key计算哈希值，再适用除留余数法得到桶的下标，以头插法的方式插入到链表中。

**查询**时分为两步：1.计算键值对所在的桶；2.在链表上顺序查找，时间复杂度显然和链表的长度成正比。

**注：**HashMap使用0号桶存放键值为null的数据

##### 扩容机制

设 HashMap 的 table 长度为 M，需要存储的键值对数量为 N，如果哈希函数满足均匀性的要求，那么每条链表的长度大约为 N/M，因此平均查找次数的复杂度为 O(N/M)。

为了让查找的成本降低，应该尽可能使得 N/M 尽可能小，因此需要保证 M 尽可能大，也就是说 table 要尽可能大。HashMap 采用动态扩容来根据当前的 N 值来调整 M 值，使得空间效率和时间效率都能得到保证。

和扩容相关的参数主要有：capacity、size、threshold 和 load_factor。

|    参数     | 含义                                                         |
| :---------: | ------------------------------------------------------------ |
|  capacity   | table 的容量大小，默认为 16。需要注意的是 capacity 必须保证为 2 的 n 次方。 |
|    size     | 键值对数量。                                                 |
|  threshold  | size 的临界值，当 size 大于等于 threshold 就必须进行扩容操作。 |
| load_factor | 装载因子，table 能够使用的比例，threshold = capacity * loadFactor。 |

```
static final int DEFAULT_INITIAL_CAPACITY = 16; 
static final int MAXIMUM_CAPACITY = 1 << 30; 
static final float DEFAULT_LOAD_FACTOR = 0.75f;
```

HashMap当需要扩容时，令 capacity 为原来的两倍。

扩容使用 resize() 实现，需要注意的是，扩容操作同样需要把 oldTable 的所有键值对重新插入 newTable 中，因此这一步是很费时的。

##### 初始化

HashMap 构造函数允许用户传入的容量不是 2 的 n 次方，因为它可以自动地将传入的容量转换为 2 的 n 次方。

**为什么这么做的原因？**

hash%length==hash&(length-1)的前提是 length 是2的 n 次方，相对于%能够提⾼运算效率，这就解释了 HashMap 的⻓度为什么是2的幂次⽅。

##### JDK1.8之后

头插法改成尾插法。

一个桶存储的链表长度大于 8 时会将链表转换为红黑树，但是当HashMap容量小于64时会优先扩容。

#### ConcurrentHashMap

以JDK1.7为例

##### 存储结构

ConcurrentHashMap 和 HashMap 实现上类似，最主要的差别是 ConcurrentHashMap 采用了分段锁（Segment），每个分段锁维护着几个桶（HashEntry），多个线程可以同时访问不同分段锁上的桶，从而使其并发度更高（并发度就是 Segment 的个数）。

##### JDK1.8之后

JDK 1.7 使用分段锁机制来实现并发更新操作，核心类为 Segment，它继承自重入锁 ReentrantLock，并发度与Segment 数量相等。

JDK 1.8的 ConcurrentHashMap 不在是 Segment 数组 + HashEntry 数组 + 链表，⽽是 Node 数 组 + 链表 / 红⿊树。

JDK 1.8 使用了 CAS 操作来支持更高的并发度，在 CAS 操作失败时使用内置锁 synchronized。

并且 JDK 1.8 的实现也在链表过长时会转换为红黑树。

## 原子类（Atomic类）

原子类是具有原子性的类，原子性的意思是对于一组操作，要么全部执行成功，要么全部执行失败，不能只有其中某几个执行成功。

作用和锁有类似之处，是为了保证并发情况下的线程安全。

### 相对于锁的优势

- 粒度更细
  原子变量可以把竞争范围缩小到变量级别,通常情况下锁的粒度也大于原子变量的粒度
- 效率更高
  除了在高并发之外，使用原子类的效率往往比使用同步互斥锁的效率更高，因为原子类底层利用了**CAS**，不会阻塞线程。（所以原子类也不适用于高并发的场景）

### AtomicInteger常用方法

上面列举了J.U.C中提供的一些原子操作类，接下来从简单的AtomicInteger开始分析，来看看它的常用方法，其他的两种AtomicLong、AtomicBoolean和它相似。

| 类型                               | 具体类型                                                     |
| ---------------------------------- | ------------------------------------------------------------ |
| Atomic* 基本类型原子类             | AtomicInteger、AtomicLong、AtomicBoolean                     |
| Atomic*Array 数组类型原子类        | AtomicIntegerArray、AtomicLongArray、AtomicReferenceArray    |
| Atomic*Reference 引用类型原子类    | AtomicReference、AtomicStampedReference、AtomicMarkableReference |
| Atomic*FieldUpdater 升级类型原子类 | AtomicIntegerfieldupdater、AtomicLongFieldUpdater、AtomicReferenceFieldUpdater |
| Adder 累加器                       | LongAdder、DoubleAdder                                       |
| Accumulator 积累器                 | LongAccumulator、DoubleAccumulator                           |

### Array 数组类型原子类

AtomicArray 数组类型原子类，数组里的元素，都可以保证其原子性，比如 AtomicIntegerArray 相当于把 AtomicInteger 聚合起来，组合成一个数组。我们如果想用一个每一个元素都具备原子性的数组的话， 就可以使用 AtomicArray。
| 方法                                          | 作用                                                         |
| --------------------------------------------- | ------------------------------------------------------------ |
| public final int get()                        | 获取当前的值                                                 |
| public final int getAndSet(int newValue)      | 获取当前的值，并设置新的值                                   |
| public final int getAndIncrement()            | 获取当前的值，并自增+1                                       |
| public final int getAndDecrement()            | 获取当前的值，并自减-1                                       |
| public final int getAndAdd(int delta)         | 获取当前的值，并加上预期的值。getAndIncrement和getAndDecrement不满足，可使用当前方法 |
| boolean compareAndSet(int expect, int update) | 如果输入的数值等于预期值，则以原子方式将该值更新为输入值（update） |

### Atomic*Reference 引用类型原子类

AtomicReference引用类型原子类，作用和AtomicInteger没有本质区别，AtomicReference是让一个对象保持原子性，而不局限一个变量。

该种类所有类型：

| 类名                    | 作用                                                         |
| ----------------------- | ------------------------------------------------------------ |
| AtomicReference         | 保证对象的原子性                                             |
| AtomicStampedReference  | 它是对 AtomicReference 的升级，在此基础上还加了时间戳，用于解决 CAS 的 ABA 问题。 |
| AtomicMarkableReference | 和 AtomicReference 类似，多了一个绑定的布尔值，可以用于表示该对象已删除等场景。 |

### Atomic*FieldUpdater原子更新器

原子类更新器主要用于对已经声明的非原子变量，为它增加原子性，让该变量拥有CAS操作的能力。
该种类所有类型：

| 类名                        | 作用                   |
| --------------------------- | ---------------------- |
| AtomicIntegerFieldUpdater   | 原子更新整形的更新器   |
| AtomicLongFieldUpdater      | 原子更新长整形的更新器 |
| AtomicReferenceFieldUpdater | 原子更新引用的更新器   |

```
//声明原子更新类，泛型为Score类，更新的是"score"字段
public static AtomicIntegerFieldUpdater<Score> scoreUpdater = AtomicIntegerFieldUpdater
        .newUpdater(Score.class, "score");
```

### 如何改进AtomicLong 在高并发下性能

java8引入的，高并发下LongAdder比AtomicLong（其他基本原子类一样）效率高，不过**本质是空间换时间**

LongAdder引入了分段累加的概念，内部有一个`base`变量和一个`cell[]`共同参与累加计数：

- base变量：竞争不激烈，直接累加到该变量上
- cell[]：竞争激烈的情况下，各个线程分散累加到自己的槽cell[i]中

因此每个线程之间不需要每次累加都进行同步，只是在每个线程累加完成之后将数据同步到对应的cell[i]中，然后再调用sum()进行统计。

LongAdder在两个方面提高了性能：每次操作无需同步、分段机制降低冲突

**总结：**竞争激烈的时候，LongAdder把不同线程对应到不同的Cell上进行修改，降低了冲突的概率，这是多段锁的概念，提高了并发性。

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

## 并发

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

### 8.线程状态

![image-20211005164742106](C:\Users\mwp\AppData\Roaming\Typora\typora-user-images\image-20211005164742106.png)

**新建（New）**

创建后尚未启动。

**可运行（Runnable）**

可能正在运行，也可能正在等待 CPU 时间片。
包含了操作系统线程状态中的 Running 和 Ready。

**阻塞（Blocked）**

等待获取一个排它锁，如果其线程释放了锁就会结束此状态。

**无限期等待（Waiting）**

等待其它线程显式地唤醒，否则不会被分配 CPU 时间片。

**限期等待（Timed Waiting）**

无需等待其它线程显式地唤醒，在一定时间之后会被系统自动唤醒。

阻塞和等待的区别在于，阻塞是被动的，它是在等待获取一个排它锁。而等待是主动的，通过调用 Thread.sleep() 和Object.wait() 等方法进入。

**死亡（Terminated）**

可以是线程结束任务之后自己结束，或者产生了异常而结束。

### 9.进程和线程

**进程**是程序的⼀次执⾏过程，是系统运⾏程序的基本单位，因此进程是动态的。系统运⾏⼀个程序即是⼀个进程从创建，运⾏到消亡的过程。

**线程**与进程相似，但线程是⼀个⽐进程更⼩的执⾏单位。⼀个进程在其执⾏的过程中可以产⽣多个线程。与进程不同的是同类的多个线程共享进程的堆和⽅法区资源，但每个线程有⾃⼰的程序计数器、 虚拟机栈和本地⽅法栈，所以系统在产⽣⼀个线程，或是在各个线程之间作切换⼯作时，负担要⽐进程⼩得多，也正因为如此，线程也被称为轻量级进程

线程 是 进程 划分成的更⼩的运⾏单位。线程和进程最⼤的不同在于基本上各进程是独⽴的，⽽各线程则不⼀定，因为同⼀进程中的线程极有可能会相互影响。线程执⾏开销⼩，但不利于资源的管理和保护；⽽进程正相反

### 10.创建线程

有三种使用线程的方法：

- 实现 Runnable 接口；
- 实现 Callable 接口；
- 继承 Thread 类。

实现 Runnable 和 Callable 接口的类只能当做一个可以在线程中运行的任务，不是真正意义上的线程，因此最后还需要通过 Thread 来调用。可以说任务是通过线程驱动从而执行的。

**实现 Runnable 接口**

需要实现 run() 方法。通过 Thread 调用 start() 方法来启动线程。

```
public class MyRunnable implements Runnable {
    public void run() {
// ...
    }
}
    public static void main(String[] args) {
        MyRunnable instance = new MyRunnable();
        Thread thread = new Thread(instance);
        thread.start();
    }
```

**实现 Callable 接口**

与 Runnable 相比，Callable 可以有返回值，返回值通过 FutureTask 进行封装。

```
public class MyCallable implements Callable<Integer> {
    public Integer call() {
        return 123;
    }
}
public static void main(String[]args)throws ExecutionException,InterruptedException{
        MyCallable mc=new MyCallable();
        FutureTask<Integer> ft=new FutureTask<>(mc);
        Thread thread=new Thread(ft);
        thread.start();
        System.out.println(ft.get());
        }
```

**继承 Thread 类**

同样也是需要实现 run() 方法，因为 Thread 类也实现了 Runable 接口。

当调用 start() 方法启动一个线程时，虚拟机会将该线程放入就绪队列中等待被调度，当一个线程被调度时会执行该线程的 run() 方法。

```
public class MyThread extends Thread {
    public void run() {
// ...
    }
}
public static void main(String[] args) {
        MyThread mt = new MyThread();
        mt.start();
        }
```

### 11.JMM（Java内存模型）

在 JDK1.2 之前， Java 的内存模型实现总是从主存（即共享内存）读取变量，是不需要进⾏特别的注意的。⽽在当前的 Java 内存模型下，线程可以把变量保存本地内存（⽐如机器的寄存器）中，⽽不是直接在主存中进⾏读写。这就可能造成⼀个线程在主存中修改了⼀个变量的值，⽽另外⼀个线程还继续使⽤它在寄存器中的变量值的拷⻉，造成数据的不⼀致。

![image-20211005170848716](C:\Users\mwp\AppData\Roaming\Typora\typora-user-images\image-20211005170848716.png)

要解决这个问题，就需要把变量声明为 volatile ，这就指示 JVM，这个变量是共享且不稳定的，每次使⽤它都到主存中进⾏读取。

所以， volatile 关键字 除了防⽌ JVM 的指令重排 ，还有⼀个重要的作⽤就是保证变量的可⻅性。

### 12.ThreadLocal 

**概述**

ThreadLocal 类主要解决的就是让每个线程绑定⾃⼰的值，如果你创建了⼀个 ThreadLocal 变量，那么访问这个变量的每个线程都会有这个变量的本地副本，这也是 ThreadLocal 变量名的由来。他们可以使⽤ get 和 set⽅法来获取默认值或将其值更改为当前线程所存的副本的值，从⽽避免了线程安全问题。

**原理**

Thread 类中有⼀个 threadLocals 和 ⼀个inheritableThreadLocals 变量，它们都是 ThreadLocalMap 类型的变量,我们可以把ThreadLocalMap 理解为 ThreadLocal 类实现的定制化的 HashMap 。默认情况下这两个变量都是 null，只有当前线程调⽤ ThreadLocal类的 set 或 get ⽅法时才创建它们，实际上调⽤这两个⽅法的时候，我们调⽤的是 ThreadLocalMap 类对应的 get() 、 set() ⽅法。

最终的变量是放在了当前线程的ThreadLocalMap 中，并不是存在 ThreadLocal 上， ThreadLocal 可以理解为只是 ThreadLocalMap 的封装，传递了变量值。 ThrealLocal 类中可以通过 Thread.currentThread()获取到当前线程对象后，直接通过 getMap(Thread t) 可以访问到该线程的 ThreadLocalMap 对象。

ThreadLocal 内部维护的是⼀个类似 Map 的 ThreadLocalMap 数据结构， key 为当前对象的 Thread 对象，值为 Object 对象。

**内存泄漏**

ThreadLocalMap 中使⽤的 key 为 ThreadLocal 的弱引⽤,⽽ value 是强引⽤。所以，如果ThreadLocal 没有被外部强引⽤的情况下，在垃圾回收的时候， key 会被清理掉，⽽ value 不会被清理掉。这样⼀来， ThreadLocalMap 中就会出现 key 为 null 的 Entry。假如我们不做任何措施的话， value 永远⽆法被 GC 回收，这个时候就可能会产⽣内存泄露。 ThreadLocalMap 实现中已经考虑了这种情况，在调⽤ set() 、 get() 、 remove() ⽅法的时候，会清理掉 key 为 null的记录。使⽤完 ThreadLocal ⽅法后 最好⼿动调⽤ remove() ⽅法

### 13.execute()⽅法和 submit()⽅法

1. execute() ⽅法⽤于提交不需要返回值的任务，所以⽆法判断任务是否被线程池执⾏成功与否；
2. submit() ⽅法⽤于提交需要返回值的任务。线程池会返回⼀个 Future 类型的对象，通过这个 Future 对象可以判断任务是否执⾏成功，并且可以通过 Future 的 get() ⽅法来获取返回值， get() ⽅法会阻塞当前线程直到任务完成，⽽使⽤ get long timeout TimeUnitunit ⽅法则会阻塞当前线程⼀段时间后⽴即返回，这时候有可能任务没有执⾏完。

### 14.CountDownLatch

用来控制一个线程等待多个线程。维护了一个计数器 cnt，每次调用 countDown() 方法会让计数器的值减 1，减到 0 的时候，那些因为调用 await() 方法而在等待的线程就会被唤醒。

**CyclicBarrier**

用来控制多个线程互相等待，只有当多个线程都到达时，这些线程才会继续执行。和 CountdownLatch 相似，都是通过维护计数器来实现的。线程执行 await() 方法之后计数器会减 1，并进行等待，直到计数器为 0，所有调用 await() 方法而在等待的线程才能继续执行。

CyclicBarrier 和 CountdownLatch 的一个区别是，CyclicBarrier 的计数器通过调用 reset() 方法可以循环使用，所以它才叫做循环屏障。

CyclicBarrier 有两个构造函数，其中 parties 指示计数器的初始值，barrierAction 在所有线程都到达屏障的时候会执行一次。

### 15.锁优化

这里的锁优化主要是指 JVM 对 synchronized 的优化。

**自旋锁**

互斥同步进入阻塞状态的开销都很大，应该尽量避免。在许多应用中，共享数据的锁定状态只会持续很短的一段时间。自旋锁的思想是让一个线程在请求一个共享数据的锁时执行忙循环（自旋）一段时间，如果在这段时间内能获得锁，就可以避免进入阻塞状态。

自旋锁虽然能避免进入阻塞状态从而减少开销，但是它需要进行忙循环操作占用 CPU 时间，它只适用于共享数据的锁定状态很短的场景。

在 JDK 1.6 中引入了自适应的自旋锁。自适应意味着自旋的次数不再固定了，而是由前一次在同一个锁上的自旋次数及锁的拥有者的状态来决定。

**锁消除**

锁消除是指对于被检测出不可能存在竞争的共享数据的锁进行消除。锁消除主要是通过逃逸分析来支持，如果堆上的共享数据不可能逃逸出去被其它线程访问到，那么就可以把它们当成私有数据对待，也就可以将它们的锁进行消除。

**锁粗化**

如果一系列的连续操作都对同一个对象反复加锁和解锁，频繁的加锁操作就会导致性能损耗。如果虚拟机探测到由这样的一串零碎的操作都对同一个对象加锁，将会把加锁的范围扩展（粗化）到整个操作序列的外部，这样只需要加锁一次就可以了。

**轻量级锁**

JDK 1.6 引入了偏向锁和轻量级锁，从而让锁拥有了四个状态：无锁状态（unlocked）、偏向锁状态（biasble）、轻量级锁状态（lightweight locked）和重量级锁状态（inflated）。

**偏向锁**

偏向锁的思想是偏向于让第一个获取锁对象的线程，这个线程在之后获取该锁就不再需要进行同步操作，甚至连 CAS操作也不再需要。

### 16.AQS

AQS 是⼀个⽤来构建锁和同步器的框架，使⽤ AQS 能简单且⾼效地构造出应⽤⼴泛的⼤量的同步器，⽐如我们提到的 ReentrantLock ， Semaphore ，其他的诸如ReentrantReadWriteLock ， SynchronousQueue ， FutureTask 等等皆是基于 AQS 的。当然，我们⾃⼰也能利⽤ AQS ⾮常轻松容易地构造出符合我们⾃⼰需求的同步器。

AQS 核⼼思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的⼯作线程，并且将共享资源设置为锁定状态。如果被请求的共享资源被占⽤，那么就需要⼀套线程阻塞等待以及被唤醒时锁分配的机制，这个机制 AQS 是⽤ CLH 队列锁实现的，即将暂时获取不到锁的线程加⼊到队列中。

### 17.死锁

**必要条件**

- 互斥：每个资源要么已经分配给了一个进程，要么就是可用的。
- 占有和等待：已经得到了某个资源的进程可以再请求新的资源。
- 不可抢占：已经分配给一个进程的资源不能强制性地被抢占，它只能被占有它的进程显式地释放。
- 循环等待：有两个或者两个以上的进程组成一条环路，该环路中的每个进程都在等待下一个进程所占有的资源。

**死锁预防**
在程序运行之前预防发生死锁。

- 破坏互斥条件

  例如假脱机打印机技术允许若干个进程同时输出，唯一真正请求物理打印机的进程是打印机守护进程。

- 破坏占有和等待条件

  一种实现方式是规定所有进程在开始执行前请求所需要的全部资源。

- 破坏循环等待

  给资源统一编号，进程只能按编号顺序来请求资源。

## Java虚拟机

### 运行时数据区域

JDK 1.8 之前：

![查看源图像](https://cdn.jsdelivr.net/gh/LoveLifeEveryday/FigureBed@master/typora202003/26/151354-661702.png)

JDK 1.8 ：

![image-20211004221436282](C:\Users\mwp\AppData\Roaming\Typora\typora-user-images\image-20211004221436282.png)

**线程私有的：**

- 程序计数器
- 虚拟机栈
- 本地⽅法栈

**线程共享的：**

- 堆
- ⽅法区
- 直接内存 (⾮运⾏时数据区的⼀部分)

#### **程序计数器**

记录正在执行的虚拟机字节码指令的地址（如果正在执行的是本地方法则为空）。

注意：程序计数器是唯⼀⼀个不会出现 OutOfMemoryError 的内存区域，它的⽣命周期随着线程的创建⽽创建，随着线程的结束⽽死亡。

#### **Java 虚拟机栈**

与程序计数器⼀样， Java 虚拟机栈也是线程私有的，它的⽣命周期和线程相同，描述的是 Java⽅法执⾏的内存模型，每次⽅法调⽤的数据都是通过栈传递的。

Java 内存可以粗糙的区分为堆内存（ Heap）和栈内存 (Stack),其中栈就是现在说的虚拟机栈，或者说是虚拟机栈中局部变量表部分。 （实际上， Java 虚拟机栈是由⼀个个栈帧组成，⽽每个栈帧中都拥有：**局部变量表、操作数栈、动态链接、⽅法出⼝信息**。）

局部变量表主要存放了编译期可知的各种数据类型（ boolean、 byte、 char、 short、 int、 float、long、 double）、 对象引⽤（ reference 类型，它不同于对象本身，可能是⼀个指向对象起始地址的引⽤指针，也可能是指向⼀个代表对象的句柄或其他与此对象相关的位置）。

Java 虚拟机栈会出现两种错误： StackOverFlowError 和 OutOfMemoryError 。

#### **本地⽅法栈**

和虚拟机栈所发挥的作⽤⾮常相似，区别是： 虚拟机栈为虚拟机执⾏ Java ⽅法 （也就是字节码）服务，⽽本地⽅法栈则为虚拟机使⽤到的 Native ⽅法服务。 在 HotSpot 虚拟机中和 Java虚拟机栈合⼆为⼀。

本地⽅法被执⾏的时候，在本地⽅法栈也会创建⼀个栈帧，⽤于存放该本地⽅法的局部变量表、操作数栈、动态链接、出⼝信息。

⽅法执⾏完毕后相应的栈帧也会出栈并释放内存空间，也会出现 StackOverFlowError 和OutOfMemoryError 两种错误。

#### **堆**

Java 虚拟机所管理的内存中最⼤的⼀块， Java 堆是所有线程共享的⼀块内存区域，在虚拟机启动时创建。 此内存区域的唯⼀⽬的就是存放对象实例，⼏乎所有的对象实例以及数组都在这⾥分配内存。

Java世界中“⼏乎”所有的对象都在堆中分配，但是，随着JIT编译期的发展与逃逸分析技术逐渐成熟，栈上分配、标量替换优化技术将会导致⼀些微妙的变化，所有的对象都分配到堆上也渐渐变得不那么“绝对”了。从jdk 1.7开始已经默认开启**逃逸分析**，如果某些⽅法中的对象引⽤没有被返回或者未被外⾯使⽤（也就是未逃逸出去），那么对象可以直接在栈上分配内存。

Java 堆是垃圾收集器管理的主要区域，因此也被称作GC 堆（ Garbage Collected Heap） .从垃圾回收的⻆度，由于现在收集器基本都采⽤分代垃圾收集算法，所以 Java 堆还可以细分为：**新⽣代和⽼年代**：再细致⼀点有： **Eden 空间、 From Survivor、 To Survivor 空间**等。 进⼀步划分的⽬的是更好地回收内存，或者更快地分配内存。

在 JDK 7 版本及JDK 7 版本之前，堆内存被通常被分为下⾯三部分：

1. 新⽣代内存(Young Generation)
2. ⽼⽣代(Old Generation)
3. 永⽣代(Permanent Generation)

JDK 8 版本之后⽅法区（ HotSpot 的永久代）被彻底移除了（ JDK1.7 就已经开始了），取⽽代之是元空间，元空间使⽤的是直接内存。

#### **⽅法区**

⽅法区与 Java 堆⼀样，是各个线程共享的内存区域，它⽤于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。虽然 Java 虚拟机规范把⽅法区描述为堆的⼀个逻辑部分，但是它却有⼀个别名叫做 Non-Heap（⾮堆） ，⽬的应该是与 Java 堆区分开来。

⽅法区和永久代的关系很像Java 中接⼝和类的关系，类实现了接⼝，⽽永久代就是 HotSpot 虚拟机对虚拟机规范中⽅法区的⼀种实现⽅式。 

#### Java对象的创建过程

1. 类加载检查

   包含了加载、验证、准备、解析和初始化这 5 个阶段。

2. 分配内存

3. 初始化零值

4. 设置对象头

5. 执行init方法

### 垃圾回收

#### 引用计算算法

为对象添加一个引用计数器，当对象增加一个引用时计数器加 1，引用失效时计数器减 1，引用计数为 0 的对象可被回收。

**缺点：**

无法解决循环引用问题

#### 可达性分析算法

以 GC Roots 为起始点进行搜索，可达的对象都是存活的，不可达的对象可被回收。

Java 虚拟机使用该算法来判断对象是否可被回收，GC Roots 一般包含以下内容：

- 虚拟机栈中局部变量表中引用的对象
- 本地方法栈中 JNI 中引用的对象
- 方法区中类静态属性引用的对象
- 方法区中的常量引用的对象

#### 回收常量

假如在常量池中存在字符串 "abc"，如果当前没有任何String对象引⽤该字符串常量的话，就说明常量 "abc" 就是废弃常量，如果这时发⽣内存回收的话⽽且有必要的话， "abc" 就会被系统清理出常量池。

#### 卸载无用类

类的卸载条件很多，需要满足以下三个条件，并且满足了条件**也不一定**会被卸载：

1. 该类所有的实例都已经被回收，此时堆中不存在该类的任何实例。
2. 加载该类的 ClassLoader 已经被回收。
3. 该类对应的 Class 对象没有在任何地方被引用，也就无法在任何地方通过反射访问该类方法。

### 引用类型

#### 强引用

被强引用关联的对象不会被回收，当内存不足，抛出OOM错误也不会回收。比如可以使用 new 一个新对象的方式来创建强引用。

#### 软引用

被软引用关联的对象只有在内存不够的情况下才会被回收。软引⽤可⽤来实现内存敏感的⾼速缓存。可以使用 SoftReference 类来创建软引用。

```
Object obj = new Object();
SoftReference<Object> sf = new SoftReference<Object>(obj);
obj = null; // 使对象只被软引用关联
```

#### 弱引用

被弱引用关联的对象一定会被回收，也就是说它只能存活到下一次垃圾回收发生之前。
使用 WeakReference 类来创建弱引用。

#### 虚引用

又称为幽灵引用或者幻影引用，一个对象是否有虚引用的存在，不会对其生存时间造成影响，也无法通过虚引用得到一个对象。为一个对象设置虚引用的唯一目的是能在这个对象被回收时收到一个系统通知。如果⼀个对象仅持有虚引⽤，那么它就和没有任何引⽤⼀样，在任何时候都可能被垃圾回收。

### 垃圾收集算法

#### 标记 - 清除

在标记阶段，程序会检查每个对象是否为活动对象，如果是活动对象，则程序会在对象头部打上标记。

在清除阶段，会进行对象回收并取消标志位，另外，还会判断回收后的分块与前一个空闲分块是否连续，若连续，会合并这两个分块。回收对象就是把对象作为分块，连接到被称为 “空闲链表” 的单向链表，之后进行分配时只需要遍历这个空闲链表，就可以找到分块。

**不足：**

- 标记和清除过程效率都不高；
- 会产生大量不连续的内存碎片，导致无法给大对象分配内存。

#### **标记 - 整理**

标记过程仍然与“标记-清除”算法⼀样，但后续步骤不是直接对可回收对象回收，⽽是让所有存活的对象向⼀端移动，然后直接清理掉端边界以外的内存。
**优点:**

- 不会产生内存碎片

**不足:**

- 需要移动大量对象，处理效率比较低

#### 复制算法

将内存划分为大小相等的两块，每次只使用其中一块，当这一块内存用完了就将还存活的对象复制到另一块上面，然后再把使用过的内存空间进行一次清理。

**不足：**

只使用了一半的内存

#### 分代收集算法

将内存划分为大小相等的两块，每次只使用其中一块，当这一块内存用完了就将还存活的对象复制到另一块上面，然后再把使用过的内存空间进行一次清理。

⽐如在新⽣代中，每次收集都会有⼤量对象死去，所以可以选择复制算法，只需要付出少量对象的复制成本就可以完成每次垃圾收集。⽽⽼年代的对象存活⼏率是⽐较⾼的，⽽且没有额外的空间对它进⾏分配担保，所以我们必须选择“标记-清除”或“标记-整理”算法进⾏垃圾收集。

### 垃圾收集器

#### Serial

它是单线程的收集器，只会使用一个线程进行垃圾收集工作。

它的优点是简单高效，在单个 CPU 环境下，由于没有线程交互的开销，因此拥有最高的单线程收集效率。

**新⽣代采⽤复制算法，⽼年代采⽤标记-整理算法。**

#### ParNew

ParNew 收集器其实就是 Serial 收集器的多线程版本，除了使⽤多线程进⾏垃圾收集外，其余⾏为（控制参数、收集算法、回收策略等等）和 Serial 收集器完全⼀样。

**新⽣代采⽤复制算法，⽼年代采⽤标记-整理算法。**

#### Parallel Scavenge

Parallel Scavenge 收集器看上去⼏乎和 ParNew 都⼀样。 Parallel Scavenge 收集器关注点是吞吐量（⾼效率的利⽤ CPU）。 CMS 等垃圾收集器的关注点更多的是⽤户线程的停顿时间（提⾼⽤户体验）。所谓吞吐量就是 CPU 中⽤于运⾏⽤户代码的时间与 CPU 总消耗时间的⽐值。 

**新⽣代采⽤复制算法，⽼年代采⽤标记-整理算法。**

#### Serial Old

Serial 收集器的⽼年代版本，它同样是⼀个单线程收集器。它主要有两⼤⽤途：⼀种⽤途是在JDK1.5 以及以前的版本中与 Parallel Scavenge 收集器搭配使⽤，另⼀种⽤途是作为 CMS 收集器的后备⽅案。

#### Parallel Old

Parallel Scavenge 收集器的⽼年代版本。使⽤多线程和“标记-整理”算法。在注重吞吐量以及CPU 资源的场合，都可以优先考虑 Parallel Scavenge 收集器和 Parallel Old 收集器。

#### CMS

CMS（ Concurrent Mark Sweep）收集器是⼀种以获取最短回收停顿时间为⽬标的收集器。它⾮常符合在注重⽤户体验的应⽤上使⽤。

CMS（ Concurrent Mark Sweep）收集器是 HotSpot 虚拟机第⼀款真正意义上的并发收集器，它第⼀次实现了让垃圾收集线程与⽤户线程（基本上）同时⼯作。

分为以下四个流程：

- 初始标记：仅仅只是标记一下 GC Roots 能直接关联到的对象，速度很快，需要停顿。
- 并发标记：进行 GC Roots Tracing 的过程，它在整个回收过程中耗时最长，不需要停顿。
- 重新标记：为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，需要停顿。
- 并发清除：不需要停顿。

在整个过程中耗时最长的**并发标记和并发清除**过程中，收集器线程都可以与用户线程一起工作，不需要进行停顿。

具有以下缺点：

- 吞吐量低：低停顿时间是以牺牲吞吐量为代价的，导致 CPU 利用率不够高。
- 无法处理浮动垃圾，可能出现 Concurrent Mode Failure。浮动垃圾是指并发清除阶段由于用户线程继续运行而产生的垃圾，这部分垃圾只能到下一次 GC 时才能进行回收。由于浮动垃圾的存在，因此需要预留出一部分内存，意味着 CMS 收集不能像其它收集器那样等待老年代快满的时候再回收。如果预留的内存不够存放浮动垃圾，就会出现 Concurrent Mode Failure，这时虚拟机将临时启用 Serial Old 来替代 CMS。
- **标记 - 清除算法**导致的空间碎片，往往出现老年代空间剩余，但无法找到足够大连续空间来分配当前对象，不得不提前触发一次 Full GC。

#### G1

G1 (Garbage-First) 是⼀款⾯向服务器的垃圾收集器,主要针对配备多颗处理器及⼤容量内存的机器. 以极⾼概率满⾜ GC 停顿时间要求的同时,还具备⾼吞吐量性能特征。

G1 收集器的运作大致可划分为以下几个步骤：

- 初始标记：仅仅只是标记一下 GC Roots 能直接关联到的对象，速度很快，需要停顿。
- 并发标记：进行 GC Roots Tracing 的过程，它在整个回收过程中耗时最长，不需要停顿。
- 最终标记：为了修正在并发标记期间因用户程序继续运作而导致标记产生变动的那一部分标记记录，虚拟机将这段时间对象变化记录在线程的 Remembered Set Logs 里面，最终标记阶段需要把 Remembered Set Logs的数据合并到 Remembered Set 中。这阶段需要停顿线程，但是可并行执行。
- 筛选回收：首先对各个 Region 中的回收价值和成本进行排序，根据用户所期望的 GC 停顿时间来制定回收计划。此阶段其实也可以做到与用户程序一起并发执行，但是因为只回收一部分 Region，时间是用户可控制的，而且停顿用户线程将大幅度提高收集效率。

具备如下特点：

- 空间整合：整体来看是基于“标记 - 整理”算法实现的收集器，从局部（两个 Region 之间）上来看是基于“复制”算法实现的，这意味着运行期间不会产生内存空间碎片。
- 可预测的停顿：能让使用者明确指定在一个长度为 M 毫秒的时间片段内，消耗在 GC 上的时间不得超过 N 毫秒。

G1 把堆划分成多个大小相等的独立区域（Region），新生代和老年代不再物理隔离。

### 内存分配策略

- **对象优先在 Eden 分配**
  大多数情况下，对象在新生代 Eden 上分配，当 Eden 空间不够时，发起 Minor GC。

- **大对象直接进入老年代**
  大对象是指需要连续内存空间的对象，最典型的大对象是那种很长的字符串以及数组。经常出现大对象会提前触发垃圾收集以获取足够的连续空间分配给大对象。-XX:PretenureSizeThreshold，大于此值的对象直接在老年代分配，避免在 Eden 和 Survivor 之间的大量内存复制

  **为什么要这样呢？**

  为了避免为⼤对象分配内存时由于分配担保机制带来的复制⽽降低效率。

- **长期存活的对象进入老年代**
  为对象定义年龄计数器，对象在 Eden 出生并经过 Minor GC 依然存活，将移动到 Survivor 中，年龄就增加 1 岁，增加到一定年龄则移动到老年代中。-XX:MaxTenuringThreshold 用来定义年龄的阈值。

- **动态对象年龄判定**
  虚拟机并不是永远要求对象的年龄必须达到 MaxTenuringThreshold 才能晋升老年代，如果在 Survivor 中相同年龄所有对象大小的总和大于 Survivor 空间的一半，则年龄大于或等于该年龄的对象可以直接进入老年代，无需等到MaxTenuringThreshold 中要求的年龄。

- **空间分配担保**
  在发生 Minor GC 之前，虚拟机先检查老年代最大可用的连续空间是否大于新生代所有对象总空间，如果条件成立的话，那么 Minor GC 可以确认是安全的。

  如果不成立的话虚拟机会查看 HandlePromotionFailure 的值是否允许担保失败，如果允许那么就会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小，如果大于，将尝试着进行一次 Minor GC；如果小于，或者HandlePromotionFailure 的值不允许冒险，那么就要进行一次 Full GC。

### Full GC 的触发条件

对于 Minor GC，其触发条件非常简单，当 Eden 空间满时，就将触发一次 Minor GC。而 Full GC 则相对复杂，有以
下条件：

- 调用 System.gc()
  只是建议虚拟机执行 Full GC，但是虚拟机不一定真正去执行。不建议使用这种方式，而是让虚拟机管理内存。
- 老年代空间不足
  老年代空间不足的常见场景为前文所讲的大对象直接进入老年代、长期存活的对象进入老年代等。
  为了避免以上原因引起的 Full GC，应当尽量不要创建过大的对象以及数组。除此之外，可以通过 -Xmn 虚拟机参数调大新生代的大小，让对象尽量在新生代被回收掉，不进入老年代。还可以通过 -XX:MaxTenuringThreshold 调大对象进入老年代的年龄，让对象在新生代多存活一段时间。
- 空间分配担保失败
  使用复制算法的 Minor GC 需要老年代的内存空间作担保，如果担保失败会执行一次 Full GC。
- JDK 1.7 及以前的永久代空间不足
  在 JDK 1.7 及以前，HotSpot 虚拟机中的方法区是用永久代实现的，永久代中存放的为一些 Class 的信息、常量、静态变量等数据。
  当系统中要加载的类、反射的类和调用的方法较多时，永久代可能会被占满，在未配置为采用 CMS GC 的情况下也会执行 Full GC。如果经过 Full GC 仍然回收不了，那么虚拟机会抛出 java.lang.OutOfMemoryError。为避免以上原因引起的 Full GC，可采用的方法为增大永久代空间或转为使用 CMS GC。
- Concurrent Mode Failure
  执行 CMS GC 的过程中同时有对象要放入老年代，而此时老年代空间不足（可能是 GC 过程中浮动垃圾过多导致暂时性的空间不足），便会报 Concurrent Mode Failure 错误，并触发 Full GC。

### 双亲委派模型

#### 类加载器种类

在理解双亲委派模型之前，先来了解一下类加载器的种类。

从Java虚拟机的角度来说，有两种不同的类加载器：启动类加载器和其它类加载器。启动类加载器在HotSpot虚拟机中使用C++语言实现，它是虚拟机的一部分；除了启动类加载器之外的其它类加载器都由Java语言实现，并且全部继承自java.lang.ClassLoader，它们是独立于虚拟机外部的。

从程序开发人员的角度来说，类加载器分为四类：启动类加载器、扩展类加载器、应用类加载器和自定义类加载器。下面分别介绍这些类加载器。

- **启动类加载器：**它的作用是将JAVA_HOME/lib目录下的类加载到内存中。需要注意的是由于启动类加载器涉及到虚拟机本地的实现细节，开发人员将无法直接获取到启动类加载器的引用，所以不允许直接通过引用进行操作。
- **扩展类加载器：**它是由Sun的ExtClassLoader实现的，它的作用是将JAVA_HOME/lib/ext目录下或由系统变量 java.ext.dir指定位置中的类加载到内存中，它可以由开发人员直接使用。
- **应用程序类加载器：**它是由Sun的AppClassLoader实现的，它的作用是将classpath路径下指定的类加载到内存中。它也可以由开发人员使用。
- **自定义类加载器：**自定义的类加载器继承自ClassLoader，并覆盖findClass方法，它的作用是将特殊用途的类加载到内存中。

#### **双亲委派模型**

所谓的类加载器的双亲委派模型指的是类加载器之间的层次关系。该模型要求除了顶层的启动类加载器外，其余的类加载器都应该有自己的父类加载器，而这种父子关系一般通过组合关系来实现，而不是通过继承。

某一个类加载器在接到加载类的请求时，首先将加载任务委托给父类加载器，依次递归，如果父类加载器可以完成类加载任务，则成功返回；如果父类加载器无法完成加载任务，将抛出ClassNotFoundException异常后，再调用自己的findClass()方法进行加载，依次类推。

#### 双亲委派模型的好处

它的好处可以用一句话总结，即防止内存中出现多份同样的字节码。

从反向思考这个问题，如果没有双亲委派模型而是由各个类加载器自行加载的话，如果用户编写了一个java.lang.Object的同名类并放在ClassPath中，多个类加载器都去加载这个类到内存中，系统中将会出现多个不同的Object类，那么类之间的比较结果及类的唯一性将无法保证，而且如果不使用这种双亲委派模型将会给虚拟机的安全带来隐患。所以，要让类对象进行比较有意义，前提是他们要被同一个类加载器加载。



## 设计模式todo

## 面向对象思想

### 三大特性

**封装**
利用抽象数据类型将数据和基于数据的操作封装在一起，使其构成一个不可分割的独立实体。数据被保护在抽象数据类型的内部，尽可能地隐藏内部的细节，只保留一些对外的接口使其与外部发生联系。用户无需关心对象内部的细节，但可以通过对象对外提供的接口来访问该对象。

**优点：**

- 减少耦合：可以独立地开发、测试、优化、使用、理解和修改
- 减轻维护的负担：可以更容易被程序员理解，并且在调试的时候可以不影响其他模块
- 有效地调节性能：可以通过剖析来确定哪些模块影响了系统的性能
- 提高软件的可重用性
- 降低了构建大型系统的风险：即使整个系统不可用，但是这些独立的模块却有可能是可用的

**继承**
继承实现了 IS-A 关系，例如 Cat 和 Animal 就是一种 IS-A 关系，因此 Cat 可以继承自 Animal，从而获得 Animal 非private 的属性和方法。

继承应该遵循里氏替换原则，子类对象必须能够替换掉所有父类对象。

Cat 可以当做 Animal 来使用，也就是说可以使用 Animal 引用 Cat 对象。父类引用指向子类对象称为 向上转型 。

**多态**

多态分为编译时多态和运行时多态：

- 编译时多态主要指方法的重载
- 运行时多态指程序中定义的对象引用所指向的具体类型在运行期间才确定

运行时多态有三个条件：

- 继承
- 覆盖（重写）
- 向上转型

设计原则

**1.单一责任原则**
修改一个类的原因应该只有一个。

换句话说就是让一个类只负责一件事，当这个类需要做过多事情的时候，就需要分解这个类。

**2.开放封闭原则**
类应该对扩展开放，对修改关闭。
扩展就是添加新功能的意思，因此该原则要求在添加新功能时不需要修改代码。
符合开闭原则最典型的设计模式是装饰者模式，它可以动态地将责任附加到对象上，而不用去修改类的代码。
\3. 里氏替换原则
子类对象必须能够替换掉所有父类对象。
继承是一种 IS-A 关系，子类需要能够当成父类来使用，并且需要比父类更特殊。
如果不满足这个原则，那么各个子类的行为上就会有很大差异，增加继承体系的复杂度。
\4. 接口分离原则
不应该强迫客户依赖于它们不用的方法。
因此使用多个专门的接口比使用单一的总接口要好。
\5. 依赖倒置原则
高层模块不应该依赖于低层模块，二者都应该依赖于抽象；
抽象不应该依赖于细节，细节应该依赖于抽象。
高层模块包含一个应用程序中重要的策略选择和业务模块，如果高层模块依赖于低层模块，那么低层模块的改动就会
直接影响到高层模块，从而迫使高层模块也需要改动。
依赖于抽象意味着：
任何变量都不应该持有一个指向具体类的指针或者引用；
任何类都不应该从具体类派生；
任何方法都不应该覆写它的任何基类中的已经实现的方法。

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

## **计算机网络体系结构**

|  OSI体系   |    TCP/IP的体系    | 五层的体系结构 |
| :--------: | :----------------: | :------------: |
|   应用层   |       应用层       |     应用层     |
|   表示层   |                    |                |
|   会话层   |                    |                |
|   运输层   | 运输层（TCP、UDP） |     运输层     |
|   网络层   |       网络层       |     网络层     |
| 数据链路层 |     网络接口层     |   数据链路层   |
|   物理层   |                    |     物理层     |

### 五层协议

- 应用层 ：为特定应用程序提供数据传输服务，例如 HTTP、DNS 等协议。数据单位为报文。
- 传输层 ：为进程提供通用数据传输服务。由于应用层协议很多，定义通用的传输层协议就可以支持不断增多的应用层协议。运输层包括两种协议：传输控制协议 TCP，提供面向连接、可靠的数据传输服务，数据单位为报文段；用户数据报协议 UDP，提供无连接、尽最大努力的数据传输服务，数据单位为用户数据报。TCP 主要提供完整性服务，UDP 主要提供及时性服务。
- 网络层 ：为主机提供数据传输服务。而传输层协议是为主机中的进程提供数据传输服务。网络层把传输层传递下来的报文段或者用户数据报封装成分组。
- 数据链路层 ：网络层针对的还是主机之间的数据传输服务，而主机之间可以有很多链路，链路层协议就是为同一链路的主机提供数据传输服务。数据链路层把网络层传下来的分组封装成帧。
- 物理层 ：考虑的是怎样在传输媒体上传输数据比特流，而不是指具体的传输媒体。物理层的作用是尽可能屏蔽传输媒体和通信手段的差异，使数据链路层感觉不到这些差异。

## TCP三次握手和四次挥手

### 三次握手流程

1. 客户端–发送带有 SYN 标志的数据包–⼀次握⼿–服务端
2. 服务端–发送带有 SYN/ACK 标志的数据包–⼆次握⼿–客户端
3. 客户端–发送带有带有 ACK 标志的数据包–三次握⼿–服务端

### 三次握手确认的事情

第⼀次握⼿：Client 什么都不能确认；Server 确认了对⽅发送正常，⾃⼰接收正常

第⼆次握⼿：Client 确认了：⾃⼰发送、接收正常，对⽅发送、接收正常；Server 确认了：对⽅发送正常，⾃⼰接收正常

第三次握⼿：Client 确认了：⾃⼰发送、接收正常，对⽅发送、接收正常；Server 确认了：⾃⼰发送、接收正常，对⽅发送、接收正常

### 为什么要传回 SYN

接收端传回发送端所发送的 SYN 是为了告诉发送端，我接收到的信息确实就是你所发送的信号了。

### 四次挥手流程

1. 客户端-发送⼀个 FIN，⽤来关闭客户端到服务器的数据传送
2. 服务器-收到这个 FIN，它发回⼀ 个 ACK，确认序号为收到的序号加1 。和 SYN ⼀样，⼀个FIN 将占⽤⼀个序号
3. 服务器-关闭与客户端的连接，发送⼀个FIN给客户端
4. 客户端-发回 ACK 报⽂确认，并将确认序号设置为收到序号加1

### 为什么要四次挥手

任何⼀⽅都可以在数据传送结束后发出连接释放的通知，待对⽅确认后进⼊半关闭状态。当另⼀⽅也没有数据再发送的时候，则发出连接释放通知，对⽅确认后就完全关闭了TCP连接。

## TCP和UDP的区别

| 类型 | 是否面向连接 | 传输可靠性 | 传输形式   | 传输速率 | 所需资源 | 应用场景             | 首部字节 |
| ---- | ------------ | ---------- | ---------- | -------- | -------- | -------------------- | -------- |
| TCP  | 是           | 可靠       | 字节流     | 慢       | 多       | 要求通信可靠的场景   | 20-60    |
| UDP  | 否           | 不可靠     | 数据报文段 | 快       | 少       | 要求通信速度快的场景 | 8        |

## TCP协议如何保证可靠传输

1. 应⽤数据被分割成 TCP 认为最适合发送的数据块。
2. TCP 给发送的每⼀个包进⾏编号，接收⽅对数据包进⾏排序，把有序数据传送给应⽤层。
3. 校验和： TCP 将保持它⾸部和数据的检验和。这是⼀个端到端的检验和，⽬的是检测数据在传输过程中的任何变化。如果收到段的检验和有差错，TCP 将丢弃这个报⽂段和不确认收到此报⽂段。
4. TCP 的接收端会丢弃重复的数据。
5. 流量控制： TCP 连接的每⼀⽅都有固定⼤⼩的缓冲空间，TCP的接收端只允许发送端发送接收端缓冲区能接纳的数据。当接收⽅来不及处理发送⽅的数据，能提示发送⽅降低发送的速率，防⽌包丢失。TCP 使⽤的流量控制协议是可变⼤⼩的滑动窗⼝协议。 （TCP 利⽤滑动窗⼝实现流量控制）
6. 拥塞控制： 当⽹络拥塞时，减少数据的发送。
7. ARQ协议： 也是为了实现可靠传输的，它的基本原理就是每发完⼀个分组就停⽌发送，等待对⽅确认。在收到确认后再发下⼀个分组。
8. 超时重传： 当 TCP 发出⼀个段后，它启动⼀个定时器，等待⽬的端确认收到这个报⽂段。如果不能及时收到⼀个确认，将重发这个报⽂段。

## ARQ协议

⾃动重传请求（Automatic Repeat-reQuest，ARQ）是OSI模型中数据链路层和传输层的错误纠正协议之⼀。它通过使⽤确认和超时这两个机制，在不可靠服务的基础上实现可靠的信息传输。如果发送⽅在发送后⼀段时间之内没有收到确认帧，它通常会重新发送。ARQ包括停⽌等待ARQ协议和连续ARQ协议。

## 滑动窗口和流量控制

TCP 利⽤滑动窗⼝实现流量控制。流量控制是为了控制发送⽅发送速率，保证接收⽅来得及接收。 接收⽅发送的确认报⽂中的窗⼝字段可以⽤来控制发送⽅窗⼝⼤⼩，从⽽影响发送⽅的发送速率。将窗⼝字段设置为 0，则发送⽅不能发送数据。

## 拥塞控制

在某段时间，若对⽹络中某⼀资源的需求超过了该资源所能提供的可⽤部分，⽹络的性能就要变坏。这种情况就叫拥塞。拥塞控制就是为了防⽌过多的数据注⼊到⽹络中，这样就可以使⽹络中的路由器或链路不致过载。拥塞控制所要做的都有⼀个前提，就是⽹络能够承受现有的⽹络负荷。拥塞控制是⼀个全局性的过程，涉及到所有的主机，所有的路由器，以及与降低⽹络传输性能有关的所有因素。相反，流量控制往往是点对点通信量的控制，是个端到端的问题。流量控制所要做到的就是抑制发送端发送数据的速率，以便使接收端来得及接收。

为了进⾏拥塞控制，TCP 发送⽅要维持⼀个 拥塞窗⼝(cwnd) 的状态变量。拥塞控制窗⼝的⼤⼩取决于⽹络的拥塞程度，并且动态变化。发送⽅让⾃⼰的发送窗⼝取为拥塞窗⼝和接收⽅的接受窗⼝中更⼩的⼀个。

TCP的拥塞控制采⽤了四种算法，即 慢开始 、 拥塞避免 、快重传 和 快恢复。在⽹络层也可以使路由器采⽤适当的分组丢弃策略（如主动队列管理 AQM），以减少⽹络拥塞的发⽣。

- **慢开始**： 慢开始算法的思路是当主机开始发送数据时，如果⽴即把⼤量数据字节注⼊到⽹络，那么可能会引起⽹络阻塞，因为现在还不知道⽹络的符合情况。经验表明，较好的⽅法是先探测⼀下，即由⼩到⼤逐渐增⼤发送窗⼝，也就是由⼩到⼤逐渐增⼤拥塞窗⼝数值。cwnd初始值为1，每经过⼀个传播轮次，cwnd加倍。
- **拥塞避免**： 拥塞避免算法的思路是让拥塞窗⼝cwnd缓慢增⼤，即每经过⼀个往返时间RTT就把发送放的cwnd加1.
- **快重传与快恢复**：在 TCP/IP 中，快速重传和恢复（fast retransmit and recovery，FRR）是⼀种拥塞控制算法，它能快速恢复丢失的数据包。没有 FRR，如果数据包丢失了，TCP 将会使⽤定时器来要求传输暂停。在暂停的这段时间内，没有新的或复制的数据包被发送。有了 FRR，如果接收机接收到⼀个不按顺序的数据段，它会⽴即给发送机发送⼀个重复确认。如果发送机接收到三个重复确认，它会假定确认件指出的数据段丢失了，并⽴即重传这些丢失的数据段。有了 FRR，就不会因为重传时要求的暂停被耽误。 　当有单独的数据包丢失时，快速重传和恢复（FRR）能最有效地⼯作。当有多个数据信息包在某⼀段很短的时间内丢失时，它则不能很有效地⼯作。

## URL解析过程

1. DNS解析(浏览器缓存、路由器缓存、DNS缓存)
2. TCP连接
3. 发送HTTP请求
4. 服务器处理请求并返回HTTP报⽂
5. 浏览器解析渲染⻚⾯
6. 连接结束

## HTTP协议

### 长连接

在HTTP/1.0中默认使⽤短连接。也就是说，客户端和服务器每进⾏⼀次HTTP操作，就建⽴⼀次连接，任务结束就中断连接。⽽从**HTTP/1.1**起，默认使⽤⻓连接，⽤以保持连接特性。

### 无状态

HTTP 是⼀种不保存状态，即⽆状态（stateless）协议。也就是说 HTTP 协议⾃身不对请求和响应之间的通信状态进⾏保存。那么我们保存⽤户状态呢？Session 机制的存在就是为了解决这个问题，Session 的主要作⽤就是通过服务端记录⽤户的状态。典型的场景是购物⻋，当你要添加商品到购物⻋的时候，系统不知道是哪个⽤户操作的，因为 HTTP 协议是⽆状态的。服务端给特定的⽤户创建特定的 Session 之后就可以标识这个⽤户并且跟踪这个⽤户了（⼀般情况下，服务器会在⼀定时间内保存这个 Session，过了时间限制，就会销毁这个Session）。

实现方式：

- 放在Cookie中附加一个SessionID来进行跟踪
- 如果Cookie被禁用，可以使用URL重写，将SessionID直接附加在URL后面

### 与HTTPS的区别

1. 端⼝ ：HTTP的URL由“http://”起始且默认使⽤端⼝80，⽽HTTPS的URL由“https://”起始且默认使⽤端⼝443。

2. 安全性和资源消耗： HTTP协议运⾏在TCP之上，所有传输的内容都是明⽂，客户端和服务器端都⽆法验证对⽅的身份。HTTPS是运⾏在SSL/TLS之上的HTTP协议，SSL/TLS 运⾏在TCP之上。所有传输的内容都经过加密，加密采⽤对称加密，**但对称加密的密钥⽤服务器⽅的证书进⾏了⾮对称加密。**所以说，HTTP 安全性没有 HTTPS⾼，但是 HTTPS ⽐HTTP耗费更多服务器资源。

   **对称加密**：密钥只有⼀个，加密解密为同⼀个密码，且加解密速度快，典型的对称加密算法有DES、AES等；

   **⾮对称加密**：密钥成对出现（且根据公钥⽆法推知私钥，根据私钥也⽆法推知公钥），加密解密使⽤不同密钥（公钥加密需要私钥解密，私钥加密需要公钥解密），相对对称加密速度较慢，典型的⾮对称加密算法有RSA、DSA等。

### 状态码

|      |     类别      |                         含义                         |
| :--: | :-----------: | :--------------------------------------------------: |
| 1XX  | Informational |                  接收的请求正在处理                  |
| 2XX  |    Success    |                   请求正常处理完毕                   |
| 3XX  |  Redirection  |              需要进行附加操作以完成请求              |
| 4XX  | Client Error  | 服务器无法处理请求（如参数校验失败，客户端出错在前） |
| 5XX  | Server Error  |                  服务器处理请求出错                  |

## I/O模型

一个输入操作通常包括两个阶段：

- 等待数据准备好
- 从内核向进程复制数据

对于一个套接字上的输入操作，第一步通常涉及等待数据从网络中到达。当所等待数据到达时，它被复制到内核中的某个缓冲区。第二步就是把数据从内核缓冲区复制到应用进程缓冲区。

Unix 有五种 I/O 模型：

- 阻塞式 I/O
- 非阻塞式 I/O
- I/O 复用（select 和 poll）
- 信号驱动式 I/O（SIGIO）
- 异步 I/O（AIO）

#### 阻塞式I/O

应用进程被阻塞，直到数据从内核缓冲区复制到应用进程缓冲区中才返回。

应该注意到，在阻塞的过程中，其它应用进程还可以执行，因此阻塞不意味着整个操作系统都被阻塞。因为其它应用进程还可以执行，所以不消耗 CPU 时间，这种模型的 CPU 利用率会比较高。

#### 非阻塞式I/O

应用进程执行系统调用之后，内核返回一个错误码。应用进程可以继续执行，但是需要不断的执行系统调用来获知I/O 是否完成，这种方式称为轮询（polling）。

由于 CPU 要处理更多的系统调用，因此这种模型的 CPU 利用率比较低。

#### I/O复用

使用 select 或者 poll 等待数据，并且可以等待多个套接字中的任何一个变为可读。这一过程会被**阻塞**，当某一个套接字可读时返回，之后再使用 recvfrom 把数据从内核复制到进程中。

它可以让单个进程具有处理多个 I/O 事件的能力。又被称为 Event Driven I/O，即事件驱动 I/O。如果一个 Web 服务器没有 I/O 复用，那么每一个 Socket 连接都需要创建一个线程去处理。如果同时有几万个连接，那么就需要创建相同数量的线程。相比于多进程和多线程技术，I/O 复用不需要进程线程创建和切换的开销，系统开销更小。

#### 信号驱动 I/O

应用进程使用 sigaction 系统调用，内核立即返回，应用进程可以继续执行，也就是说等待数据阶段应用进程是非阻塞的。内核在数据到达时向应用进程发送 SIGIO 信号，应用进程收到之后在信号处理程序中调用 recvfrom 将数据从内核复制到应用进程中。

相比于非阻塞式 I/O 的轮询方式，信号驱动 I/O 的 CPU 利用率更高。

#### 异步 I/O

应用进程执行 aio_read 系统调用会立即返回，应用进程可以继续执行，不会被阻塞，内核会在所有操作完成之后向应用进程发送信号。

异步 I/O 与信号驱动 I/O 的区别在于，异步 I/O 的信号是通知应用进程 I/O 完成，而信号驱动 I/O 的信号是通知应用进程可以开始 I/O。

#### 五大 I/O 模型比较

- 同步 I/O：将数据从内核缓冲区复制到应用进程缓冲区的阶段（第二阶段），应用进程会阻塞。
- 异步 I/O：第二阶段应用进程不会阻塞。

同步 I/O 包括阻塞式 I/O（**BIO**）、非阻塞式 I/O**（NIO）**、I/O 复用和信号驱动 I/O ，它们的主要区别在第一个阶段。

非阻塞式 I/O 、信号驱动 I/O 和异步 I/O 在第一阶段不会阻塞。

## Session和Cookie

**Cookie**
HTTP 协议是无状态的，主要是为了让 HTTP 协议尽可能简单，使得它能够处理大量事务。HTTP/1.1 引入 Cookie 来保存状态信息。

Cookie 是服务器发送到用户浏览器并保存在本地的一小块数据，它会在浏览器之后向同一服务器再次发起请求时被携带上，用于告知服务端两个请求是否来自同一浏览器。由于之后每次请求都会需要携带 Cookie 数据，因此会带来额外的性能开销（尤其是在移动环境下）。

Cookie 曾一度用于客户端数据的存储，因为当时并没有其它合适的存储办法而作为唯一的存储手段，但现在随着现代浏览器开始支持各种各样的存储方式，Cookie 渐渐被淘汰。新的浏览器 API 已经允许开发者直接将数据存储到本地，如使用 Web storage API（本地存储和会话存储）或 IndexedDB。

**用途**

- 会话状态管理（如用户登录状态、购物车、游戏分数或其它需要记录的信息）
- 个性化设置（如用户自定义设置、主题等）
- 浏览器行为跟踪（如跟踪分析用户行为等）

**Session**

除了可以将用户信息通过 Cookie 存储在用户浏览器中，也可以利用 Session 存储在服务器端，存储在服务器端的信息更加安全。

Session 可以存储在服务器上的文件、数据库或者内存中。也可以将 Session 存储在 Redis 这种内存型数据库中，效率会更高。

使用 Session 维护用户登录状态的过程如下：

1. 用户进行登录时，用户提交包含用户名和密码的表单，放入 HTTP 请求报文中；
2. 服务器验证该用户名和密码，如果正确则把用户信息存储到 Redis 中，它在 Redis 中的 Key 称为 Session ID；
3. 服务器返回的响应报文的 Set-Cookie 首部字段包含了这个 Session ID，客户端收到响应报文之后将该 Cookie值存入浏览器中；
4. 客户端之后对同一个服务器进行请求时会包含该 Cookie 值，服务器收到之后提取出 Session ID，从 Redis 中取出用户信息，继续之前的业务操作。

应该注意 Session ID 的安全性问题，不能让它被恶意攻击者轻易获取，那么就不能产生一个容易被猜到的 SessionID 值。此外，还需要经常重新生成 Session ID。在对安全性要求极高的场景下，例如转账等操作，除了使用 Session管理用户状态之外，还需要对用户进行重新验证，比如重新输入密码，或者使用短信验证码等方式。

**Cookie 与 Session 选择**

Cookie 只能存储 ASCII 码字符串，而 Session 则可以存储任何类型的数据，因此在考虑数据复杂性时首选Session；

Cookie 存储在浏览器中，容易被恶意查看。如果非要将一些隐私数据存在 Cookie 中，可以将 Cookie 值进行加密，然后在服务器进行解密；

对于大型网站，如果用户所有的信息都存储在 Session 中，那么开销是非常大的，因此不建议将所有的用户信息都存储到 Session 中。

# 四、系统设计

## 一、系统性能

### 系统指标

#### 1.**RPS**(request per second)

用来描述**客户端**每秒钟向系统(服务端)发送请求的数量，该指标用于描述客户端发送请求的能力，该能力只跟客户端的发送频率、软件逻辑以及客户端硬件配置有关，尤其受CPU、网络配置影响较大。

#### 2.**TPS**(transactions per second)

**对系统(服务端)而言，每秒钟处理的事务数量**。即1秒钟内，系统(服务端)能够响应(完成处理)客户端请求并成功返回给客户端的事务总数。并且客户端对于不同的应用场景而言，T的解读是不一样的，一般而言可以有如下两种解读方式：

- **api级别的事务**（调用了多少次接口）
- **业务级别的事务**（业务行为发生的次数）

#### 3.**QPS**(query per second)

**用来描述系统(服务端)每秒钟可支撑的查询数量**，该指标最初用来描述RDBMS中每秒中执行的SQL数量，但是后来慢慢的就被用来描述系统的吞吐量了。也就是说我们现在通常说的系统QPS实际上说的是系统的**吞吐量**。由于只是描述查询的效率，因此该指标是用来描述系统(服务端)查询能力有多大的。相比于TPS，QPS只能反映系统(服务端)处理请求能力的一部分，可以理解为**TPS的一个子集**。

#### 4.吞吐量

**于一通信通道上单位时间能成功传递的平均资料量叫做系统吞吐量**。用来反映系统承压能力的指标，现在通常被人用来形容系统的QPS指标。

#### 5.响应时间

**也叫系统时延，即系统(服务端)在接受客户端请求后，经过一些列逻辑或链路处理后再返回结果，整个过程所花费的时间**。该指标用来反映服务端**处理速度有多快**的。

#### 6.在线用户数

**只登陆到系统(建立连接)，但是不一定发起客户端请求的用户**。严格来说系统(服务端)的压力跟在线用户数没有直接的因果关系，而是跟并发数正相关，而在线用户数跟并发数存在一定比例的转换关系，简称**并发度**，具体并发度的多少跟具体的业务场景相关(比如10%，20%等)。而一个系统(服务端)能够允许多少在线用户数，跟服务端的设置有关，比如设置服务端允许的最大连接数等。

#### 7.并发数

上面提到并发数(也叫并发用户数)是通过在线用户数乘以并发度转换而来。其中并发可以分为**绝对并发**和**相对并发**两种。绝对并发是指在**某一时刻**(非常短的时间内)服务端收到的请求数量。而相对并发指的是在**某个时间范围内**(比如：1秒钟，1分钟)，服务端收到请求数量。因为绝对并发对时间点的要求非常苛刻，因此我们日常一般会用相对并发来描述一个系统的并发数量。

### 系统优化

#### 1、扩展

扩展是最常见的提升系统可靠性的方法，系统的扩展可以避免单点故障，即一个节点出现了问题造成整个系统无法正常工作。换一个角度讲，一个容易扩展的系统，能够通过扩展来成倍的提升系统能力，轻松应对系统访问量的提升。

#### 2、隔离

是对系统、业务所占有的资源进行隔离，限制某个业务对资源的占用数量，避免一个业务占用整个系统资源，对其他业务造成影响。

#### 3、解耦

模块A和模块B只通过接口交互，只要接口设计不变，那么模块B内部细节的变化不影响模块A对模块B服务能力。

#### 4、限流

一个系统的处理能力是有上限的，当服务请求量超过处理能力，通常会引起排队，造成响应时间迅速提升。如果对服务占用的资源量没有约束，还可能因为系统资源占用过多而宕机。因此，为了保证系统在遭遇突发流量时，能够正常运行，需要为你的服务加上限流。

#### 5、降级

业务降级，是指牺牲非核心的业务功能，保证核心功能的稳定运行。简单来说，要实现优雅的业务降级，需要将功能实现拆分到相对独立的不同代码单元，分优先级进行隔离。在后台通过开关控制，降级部分非主流程的业务功能，减轻系统依赖和性能损耗，从而提升集群的整体吞吐率。

#### **6、异步**

某些流程可以将操作转换为消息，将消息发送到消息队列之后立即返回，之后这个操作会被异步处理。

#### 7、缓存

缓存能够提高性能的原因如下：

- 缓存数据通常位于内存等介质中，这种介质对于读操作特别快；
- 缓存数据可以位于靠近用户的地理位置上；
- 可以将计算结果进行缓存，从而避免重复计算。

#### 8、监控

对 CPU、内存、磁盘、网络等系统负载信息进行监控，当某个信息达到一定阈值时通知运维人员，从而在系统发生故障之前及时发现问题。

## 二、分布式

#### 1.分布式锁

##### 数据库唯一索引

获得锁时向表中插入一条记录，释放锁时删除这条记录。唯一索引可以保证该记录只被插入一次，那么就可以用这个记录是否存在来判断是否存于锁定状态。

存在以下几个问题：

- 锁没有失效时间，解锁失败的话其它进程无法再获得该锁。
- 只能是非阻塞锁，插入失败直接就报错了，无法重试。
- 不可重入，已经获得锁的进程也必须重新获取锁。

##### **Redis** **的** **SETNX** **指令**

使用 SETNX（set if not exist）指令插入一个键值对，如果 Key 已经存在，那么会返回 False，否则插入成功并返回True。

SETNX 指令和数据库的唯一索引类似，保证了只存在一个 Key 的键值对，那么可以用一个 Key 的键值对是否存在来判断是否存于锁定状态。

EXPIRE 指令可以为一个键值对设置一个过期时间，从而避免了数据库唯一索引实现方式中释放锁失败的问题。

##### **Zookeeper**选举

- 创建一个锁目录 /lock；
- 当一个客户端需要获取锁时，在 /lock 下创建临时的且有序的子节点；
- 客户端获取 /lock 下的子节点列表，判断自己创建的子节点是否为当前子节点列表中序号最小的子节点，如果是则认为获得锁；否则监听自己的前一个子节点，获得子节点的变更通知后重复此步骤直至获得锁；
- 执行业务代码，完成后，删除对应的子节点。

###### 会话超时

如果一个已经获得锁的会话超时了，因为创建的是临时节点，所以该会话对应的临时节点会被删除，其它会话就可以获得锁了。可以看到，Zookeeper 分布式锁不会出现数据库的唯一索引实现的分布式锁释放锁失败问题。

###### **羊群效应**

一个节点未获得锁，只需要监听自己的前一个子节点，这是因为如果监听所有的子节点，那么任意一个子节点状态改变，其它所有子节点都会收到通知（羊群效应），而我们只希望它的后一个子节点收到通知。

#### 2.分布式事务

#### 3.CAP

分布式系统不可能同时满足一致性（C：Consistency）、可用性（A：Availability）和分区容忍性（P：Partition Tolerance），最多只能同时满足其中两项。

**一致性**

一致性指的是多个数据副本是否能保持一致的特性，在一致性的条件下，系统在执行数据更新操作之后能够从一致性状态转移到另一个一致性状态。

对系统的一个数据更新成功之后，如果所有用户都能够读取到最新的值，该系统就被认为具有强一致性。

**可用性**

可用性指分布式系统在面对各种异常时可以提供正常服务的能力，可以用系统可用时间占总时间的比值来衡量，4 个9 的可用性表示系统 99.99% 的时间是可用的。

在可用性条件下，要求系统提供的服务一直处于可用的状态，对于用户的每一个操作请求总是能够在有限的时间内返回结果。

**分区容忍性**

网络分区指分布式系统中的节点被划分为多个区域，每个区域内部可以通信，但是区域之间无法通信。

在分区容忍性条件下，分布式系统在遇到任何网络分区故障的时候，仍然需要能对外提供一致性和可用性的服务，除非是整个网络环境都发生了故障。

**权衡**

在分布式系统中，分区容忍性必不可少，因为需要总是假设网络是不可靠的。因此，CAP 理论实际上是要在可用性和一致性之间做权衡。

可用性和一致性往往是冲突的，很难使它们同时满足。在多个节点之间进行数据同步时，

- 为了保证一致性（CP），不能访问未同步完成的节点，也就失去了部分可用性；
- 为了保证可用性（AP），允许读取所有节点的数据，但是数据可能不一致。

#### 4.BASE

BASE 是基本可用（Basically Available）、软状态（Soft State）和最终一致性（Eventually Consistent）三个短语的缩写。

BASE 理论是对 CAP 中一致性和可用性权衡的结果，它的核心思想是：即使无法做到强一致性，但每个应用都可以根据自身业务特点，采用适当的方式来使系统达到最终一致性。

**基本可用**

指分布式系统在出现故障的时候，保证核心可用，允许损失部分可用性。

例如，电商在做促销时，为了保证购物系统的稳定性，部分消费者可能会被引导到一个降级的页面。

**软状态**

指允许系统中的数据存在中间状态，并认为该中间状态不会影响系统整体可用性，即允许系统不同节点的数据副本之间进行同步的过程存在时延。

**最终一致性**

最终一致性强调的是系统中所有的数据副本，在经过一段时间的同步后，最终能达到一致的状态。

ACID 要求强一致性，通常运用在传统的数据库系统上。而 BASE 要求最终一致性，通过牺牲强一致性来达到可用性，通常运用在大型分布式系统中。

在实际的分布式场景中，不同业务单元和组件对一致性的要求是不同的，因此 ACID 和 BASE 往往会结合在一起使用。

## 三、负载均衡

### 负载均衡算法

#### 1.轮询

轮询算法把每个请求轮流发送到每个服务器上。

该算法比较适合每个服务器的性能差不多的场景，如果有性能存在差异的情况下，那么性能较差的服务器可能无法承担过大的负载。

#### 2.加权轮询

加权轮询是在轮询的基础上，根据服务器的性能差异，为服务器赋予一定的权值，性能高的服务器分配更高的权值。

#### 3.最少连接

由于每个请求的连接时间不一样，使用**轮询或者加权轮询算法**的话，可能会让一台服务器当前连接数过大，而另一台服务器的连接过小，造成负载不均衡。

最少连接算法就是将请求发送给当前最少连接数的服务器上。

#### 4.加权最少连接

在最少连接的基础上，根据服务器的性能为每台服务器分配权重，再根据权重计算出每台服务器能处理的连接数。

#### 5.随机算法

把请求随机发送到服务器上。

和轮询算法类似，该算法比较适合服务器性能差不多的场景。

#### 6.原地址哈希法

源地址哈希通过对客户端 IP 计算哈希值之后，再对服务器数量取模得到目标服务器的序号。

可以保证同一 IP 的客户端的请求会转发到同一台服务器上，用来实现会话粘滞（Sticky Session）

### 转发实现

#### 1.HTTP重定向

HTTP 重定向负载均衡服务器使用某种负载均衡算法计算得到服务器的 IP 地址之后，将该地址写入 HTTP 重定向报文中，状态码为 302。客户端收到重定向报文之后，需要重新向服务器发起请求。

**缺点：**

- 需要两次请求，因此访问延迟比较高；
- HTTP 负载均衡器处理能力有限，会限制集群的规模

#### 2.DNS域名解析

在 DNS 解析域名的同时使用负载均衡算法计算服务器 IP 地址。

优点：

- DNS 能够根据地理位置进行域名解析，返回离用户最近的服务器 IP 地址。

**缺点：**

- 由于 DNS 具有多级结构，每一级的域名记录都可能被缓存，当下线一台服务器需要修改 DNS 记录时，需要过很长一段时间才能生效。

大型网站基本使用了 DNS 做为第一级负载均衡手段，然后在内部使用其它方式做第二级负载均衡。也就是说，域名解析的结果为内部的负载均衡服务器 IP 地址。

#### 3.反向代理服务器

反向代理服务器位于源服务器前面，用户的请求需要先经过反向代理服务器才能到达源服务器。反向代理可以用来进行缓存、日志记录等，同时也可以用来做为负载均衡服务器。

在这种负载均衡转发方式下，客户端不直接请求源服务器，因此源服务器不需要外部 IP 地址，而反向代理需要配置内部和外部两套 IP 地址。

优点：

- 与其它功能集成在一起，部署简单。

缺点：

- 所有请求和响应都需要经过反向代理服务器，它可能会成为性能瓶颈。

#### 4.网络层

在操作系统内核进程获取网络数据包，根据负载均衡算法计算源服务器的 IP 地址，并修改请求数据包的目的 IP 地

址，最后进行转发。

源服务器返回的响应也需要经过负载均衡服务器，通常是让负载均衡服务器同时作为集群的网关服务器来实现。

优点：

在内核进程中进行处理，性能比较高。

缺点：

和反向代理一样，所有的请求和响应都经过负载均衡服务器，会成为性能瓶颈。

#### 5.链路层

在链路层根据负载均衡算法计算源服务器的 MAC 地址，并修改请求数据包的目的 MAC 地址，并进行转发。

通过配置源服务器的虚拟 IP 地址和负载均衡服务器的 IP 地址一致，从而不需要修改 IP 地址就可以进行转发。也正因为 IP 地址一样，所以源服务器的响应不需要转发回负载均衡服务器，可以直接转发给客户端，避免了负载均衡服务器的成为瓶颈。

这是一种三角传输模式，被称为直接路由。对于提供下载和视频服务的网站来说，直接路由避免了大量的网络传输数据经过负载均衡服务器。

这是目前大型网站使用最广负载均衡转发方式，在 Linux 平台可以使用的负载均衡服务器为 LVS（Linux VirtualServer）

## 四、CDN内容分发网络

内容分发网络（Content distribution network，CDN）是一种互连的网络系统，它利用更靠近用户的服务器从而更快更可靠地将 HTML、CSS、JavaScript、音乐、图片、视频等静态资源分发给用户。

CDN 主要有以下优点：

- 更快地将数据分发给用户；
- 通过部署多台服务器，从而提高系统整体的带宽性能；
- 多台服务器可以看成是一种冗余机制，从而具有高可用性。

# 五、数据库

## **MySQL**

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

##### 全文索引和like的区别

- MySQL 5.6 以前的版本，只有 MyISAM 存储引擎支持全文索引；

- MySQL 5.6 及以后的版本，MyISAM 和 InnoDB 存储引擎均支持全文索引;

- 只有字段的数据类型为 char、varchar、text 及其系列才可以建全文索引。

- 全文索引比 like + % 快 N 倍，但是可能存在精度问题

- 全文索引是针对单词的，不能匹配其中的单个！也就是说如果你在"abcd,efg,hijklmn"中检索"hi"，那么全文检索也没有用，如果你检索efg，那么可以使用全文检索

- 可以采用覆盖索引，主键来解决like左右%%模糊匹配问题！

- like只有前缀匹配可以走索引查询

- MySQL 中的全文索引，有两个变量，最小搜索长度和最大搜索长度，对于长度小于最小搜索长度和大于最大搜索长度的词语，都不会被索引。通俗点就是说，想对一个词语使用全文索引搜索，那么这个词语的长度必须在以上两个变量的区间内。

  ```
  // MyISAM
  ft_min_word_len = 4;
  ft_max_word_len = 84;
  
  // InnoDB
  innodb_ft_min_token_size = 3;
  innodb_ft_max_token_size = 84;
  //可以看到最小搜索长度 MyISAM 引擎下默认是 4，InnoDB 引擎下是 //3，也即，MySQL 的全文索引只会对长度大于等于 4 或者 3 的词语建
  //立索引，而刚刚搜索的只有 aaaa 的长度大于等于 4。
  ```

  

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

- 平衡树查找操作的时间复杂度和树高 h 相关，O(h)=O(logdN)，其中 d 为每个节点的出度。
- 红黑树的出度为 2，而 B+ Tree 的出度一般都非常大，所以红黑树的树高 h 很明显比 B+ Tree 大非常多，查找的次数也就更多。

**（二）利用磁盘预读特性**

- 为了减少磁盘 I/O 操作，磁盘往往不是严格按需读取，而是每次都会预读。预读过程中，磁盘进行顺序读取，顺序读取不需要进行磁盘寻道，并且只需要很短的磁盘旋转时间，速度会非常快。
- 操作系统一般将内存和磁盘分割成固定大小的块，每一块称为一页，内存与磁盘以页为单位交换数据。数据库系统将索引的一个节点的大小设置为页的大小，使得一次 I/O 就能完全载入一个节点。并且可以利用预读特性，相邻的节点也能够被预先载入。

### 10.索引优化

#### **索引的优点**

- 大大减少了服务器需要扫描的数据行数。
- 帮助服务器避免进行排序和分组，以及避免创建临时表（B+Tree 索引是有序的，可以用于 ORDER BY 和GROUP BY 操作。临时表主要是在排序和分组过程中创建，不需要排序和分组，也就不需要创建临时表）。
- 将随机 I/O 变为顺序 I/O（B+Tree 索引是有序的，会将相邻的数据都存储在一起）。

#### 索引设计规范

- 限制每张表上的索引数量,建议单张表索引不超过 5 个
- 禁止给表中的每一列都建立单独的索引，建立联合索引效果更好
- 索引列的顺序
  - 区分度最高的放在联合索引的最左侧（区分度=列中不同值的数量/列的总行数）
  - 尽量把字段长度小的列放在联合索引的最左侧（因为字段长度越小，一页能存储的数据量越大，IO 性能也就越好）
  - 使用最频繁的列放到联合索引的左侧（这样可以比较少的建立一些索引）

- 对于频繁的查询优先考虑使用覆盖索引
  - 避免 Innodb 表进行索引的二次查询
  - 可以把随机 IO 变成顺序 IO 加快查询效率

#### 数据库SQL开发规范

- 避免数据类型的隐式转换，会导致索引失效
- 禁止使用 SELECT * 
  - 消耗更多CPU和IO网络资源带宽
  - 无法使用覆盖索引
  - 可减少表结构变更带来的影响
- 避免使用子查询
  - 子查询无法使用索引
  - 子查询的结果会被存储到临时表中，影响服务器性能，并且临时表也没有索引，容易产生大量慢查询
  - 可以用join来优化子查询

- 使用批量处理减少与数据库的交互次数
- 利用in代替or
  - in的值不要超过500个
  - in可以更有效的使用索引
  - or大多数情况下很少能用到索引
- where从句中进制对列进行函数转换和计算
- 拆分复杂的大SQL为多个小SQL
  - 一个SQL只能用一个CPU计算
  - 拆分后可以通过并行来提供处理效率

#### 数据库操作行为规范

1. 超 100 万行的批量写 (UPDATE,DELETE,INSERT) 操作,要分批多次进行操作
2. 对于大表使用 pt-online-schema-change 修改表结构

#### **索引的使用条件**

- 对于非常小的表、大部分情况下简单的全表扫描比建立索引更高效；
- 对于中到大型的表，索引就非常有效；
- 但是对于特大型的表，建立和维护索引的代价将会随之增长。这种情况下，需要用到一种技术可以直接区分出需要查询的一组数据，而不是一条记录一条记录地匹配，例如可以使用分区技术。

#### **优化查询**

##### **使用** **Explain** **进行分析**

Explain 用来分析 SELECT 查询语句，开发人员可以通过分析 Explain 结果来优化查询语句。

比较重要的字段有：

select_type : 查询类型，有简单查询、联合查询、子查询等

key : 使用的索引

rows : 扫描的行数

### 11.分区表

分区表在物理上表现为多个文件，在逻辑上表现为一个表；

谨慎选择分区键，跨分区查询效率可能更低；

建议采用物理分表的方式管理大数据。

### 12.主从复制

主要涉及三个线程：binlog 线程、I/O 线程和 SQL 线程。

- **binlog** **线程** ：负责将主服务器上的数据更改写入二进制日志（Binary log）中。
- **I/O** **线程** ：负责从主服务器上读取二进制日志，并写入从服务器的中继日志（Relay log）。
- **SQL** **线程** ：负责读取中继日志，解析出主服务器已经执行的数据更改并在从服务器中重放（Replay）。

### 13.读写分离

主服务器处理写操作以及实时性要求比较高的读操作，而从服务器处理读操作。

读写分离能提高性能的原因在于：

- 主从服务器负责各自的读和写，极大程度缓解了锁的争用；
- 从服务器可以使用 **MyISAM**，提升查询性能以及节约系统开销；
- 增加冗余，提高可用性。

读写分离常用代理方式来实现，代理服务器接收应用层传来的读写请求，然后决定转发到哪个服务器。

## **Redis**

### 数据类型及应用场景

**SRING**

1. 介绍 ：string 数据结构是简单的 key-value 类型。虽然 Redis 是⽤ C 语⾔写的，但是 Redis并没有使⽤ C 的字符串表示，⽽是⾃⼰构建了⼀种 简单动态字符串（simple dynamicstring，SDS）。相⽐于 C 的原⽣字符串，Redis 的 SDS 不光可以保存⽂本数据还可以保存⼆进制数据，并且获取字符串⻓度复杂度为 O(1)（C 字符串为 O(N)）,除此之外,Redis 的SDS API 是安全的，不会造成缓冲区出。
2. 常⽤命令: set,get,strlen,exists,dect,incr,setex 等等。
3. 应⽤场景 ：⼀般常⽤在需要计数的场景，⽐如⽤户的访问次数、热点⽂章的点赞转发数量等等。缓存、分布式锁、会话缓存。

**LIST**

1. 介绍 ：list 即是 链表。链表是⼀种⾮常常⻅的数据结构，特点是易于数据元素的插⼊和删除并且且可以灵活调整链表⻓度，但是链表的随机访问困难。许多⾼级编程语⾔都内置了链表的实现⽐如 Java 中的 LinkedList，但是 C 语⾔并没有实现链表，所以 Redis 实现了⾃⼰的链表数据结构。Redis 的 list 的实现为⼀个 双向链表，即可以⽀持反向查找和遍历，更⽅便操作，不过带来了部分额外的内存开销。
2. 常⽤命令: rpush,lpop,lpush,rpop,lrange,llen 等。
3. 应⽤场景: 发布与订阅或者说消息队列、慢查询。

**SET**

1. 介绍 ： set 类似于 Java 中的 HashSet 。Redis 中的 set 类型是⼀种⽆序集合，集合中的元素没有先后顺序。当你需要存储⼀个列表数据，⼜不希望出现重复数据时，set 是⼀个很好的选择，并且 set 提供了判断某个成员是否在⼀个 set 集合内的重要接⼝，这个也是 list 所不能提供的。可以基于 set 轻易实现交集、并集、差集的操作。⽐如：你可以将⼀个⽤户所有的关注⼈存在⼀个集合中，将其所有粉丝存在⼀个集合。Redis 可以⾮常⽅便的实现如共同关注、共同粉丝、共同喜好等功能。这个过程也就是求交集的过程。
2. 常⽤命令： sadd,spop,smembers,sismember,scard,sinterstore,sunion 等。
3. 应⽤场景: 需要存放的数据不能重复以及需要获取多个数据源交集和并集等场景

**HASH**

1. 介绍 ：hash 类似于 JDK1.8 前的 HashMap，内部实现也差不多(数组 + 链表)。不过，Redis 的 hash 做了更多优化。另外，hash 是⼀个 string 类型的 field 和 value 的映射表，特别适合⽤于存储对象，后续操作的时候，你可以直接仅仅修改这个对象中的某个字段的值。 ⽐如我们可以 hash 数据结构来存储⽤户信息，商品信息等等。
2. 常⽤命令： hset,hmset,hexists,hget,hgetall,hkeys,hvals 等。
3. 应⽤场景: 系统中对象数据的存储。

**ZSET**

1. 介绍： 和 set 相⽐，sorted set 增加了⼀个权重参数 score，使得集合中的元素能够按 score进⾏有序排列，还可以通过 score 的范围来获取元素的列表。有点像是 Java 中 HashMap和 TreeSet 的结合体。
2. 常⽤命令： zadd,zcard,zscore,zrange,zrevrange,zrem 等。
3. 应⽤场景： 需要对数据根据某个权重进⾏排序的场景。⽐如在直播系统中，实时排⾏信息包含直播间在线⽤户列表，各种礼物排⾏榜，弹幕消息（可以理解为按消息维度的消息排⾏榜）等信息。

### Redis 和 Memcached 的异同

**共同点 ：**

1. 都是基于内存的数据库，⼀般都⽤来当做缓存使⽤。
2. 都有过期策略。
3. 两者的性能都⾮常⾼。

**区别 ：**

1. Redis ⽀持更丰富的数据类型（⽀持更复杂的应⽤场景）。Redis 不仅仅⽀持简单的 k/v 类型的数据，同时还提供 list，set，zset，hash 等数据结构的存储。Memcached 只⽀持最简单的 k/v 数据类型。
2. Redis ⽀持数据的持久化，可以将内存中的数据保持在磁盘中，重启的时候可以再次加载进⾏使⽤,⽽ Memecache 把数据全部存在内存之中。
3. Redis 有灾难恢复机制。 因为可以把缓存中的数据持久化到磁盘上。
4. Redis 在服务器内存使⽤完之后，可以将不⽤的数据放到磁盘上。但是，Memcached 的数据一直存放在内存中，在服务器内存使⽤完之后，就会直接报异常。
5. redis内存利用率更高，Memcached 将内存分割成特定长度的块来存储数据，以完全解决内存碎片的问题。但是这种方式会使得内存的利用率不高，例如块的大小为 128 bytes，只存储 100 bytes 的数据，那么剩下的 28 bytes 就浪费掉了。
6. Memcached 没有原⽣的集群模式，需要依靠客户端来实现往集群中分⽚写⼊数据；但是Redis ⽬前是原⽣⽀持 cluster 模式的.
7. Memcached 是多线程，⾮阻塞 IO 复⽤的⽹络模型；Redis 使⽤单线程的多路 IO 复⽤模型。 （Redis 6.0 引⼊了多线程 IO ）
8. Redis ⽀持发布订阅模型、Lua 脚本、事务等功能，⽽ Memcached 不⽀持。并且，Redis⽀持更多的编程语⾔。
9. Memcached过期数据的删除策略只⽤了惰性删除，⽽ Redis 同时使⽤了惰性删除与定期删除。

### **数据淘汰策略**

1. volatile-lru（least recently used）：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使⽤的数据淘汰
2. volatile-ttl：从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
3. volatile-random：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰
4. allkeys-lru（least recently used）：当内存不⾜以容纳新写⼊数据时，在键空间中，移除最近最少使⽤的 key（这个是最常⽤的）
5. allkeys-random：从数据集（server.db[i].dict）中任意选择数据淘汰
6. no-eviction：禁⽌驱逐数据，也就是说当内存不⾜以容纳新写⼊数据时，新写⼊操作会报错。这个应该没⼈使⽤吧！

### 删除策略

如果假设你设置了⼀批 key 只能存活 1 分钟，那么 1 分钟后，Redis 是怎么对这批 key 进⾏删除的呢？
常⽤的过期数据的删除策略就两个（重要！⾃⼰造缓存轮⼦的时候需要格外考虑的东⻄）：

1. 惰性删除 ：只会在取出key的时候才对数据进⾏过期检查。这样对CPU最友好，但是可能会造成太多过期 key 没有被删除。
2. 定期删除 ： 每隔⼀段时间抽取⼀批 key 执⾏删除过期key操作。并且，Redis 底层会通过限制删除操作执⾏的时⻓和频率来减少删除操作对CPU时间的影响。
typedef struct redisDb {
 ...

 dict *dict; //数据库键空间,保存着数据库中所有键值对
 dict *expires // 过期字典,保存着键的过期时间
 ...
} redisDb;（用来判断key是否过期）
定期删除对内存更加友好，惰性删除对CPU更加友好。两者各有千秋，所以Redis 采⽤的是 定期删除+惰性/懒汉式删除 。
但是，仅仅通过给 key 设置过期时间还是有问题的。因为还是可能存在定期删除和惰性删除漏掉了很多过期 key 的情况。这样就导致⼤量过期 key 堆积在内存⾥，然后就Out of memory了。
怎么解决这个问题呢？答案就是： Redis 内存淘汰机制。

### **持久化**

**快照（snapshotting）持久化（RDB）**
Redis 可以通过创建快照来获得存储在内存⾥⾯的数据在**某个时间点上的副本**。Redis 创建快照之后，可以对快照进⾏备份，可以将快照复制到其他服务器从⽽创建具有相同数据的服务器副本（Redis 主从结构，主要⽤来提⾼ Redis 性能），还可以将快照留在原地以便重启服务器的时候使⽤。
快照持久化是 Redis 默认采⽤的持久化⽅式，在 Redis.conf 配置⽂件中默认有此下配置：

```
#在900秒(15分钟)之后，如果⾄少有1个key发⽣变化，Redis就会⾃动触发BGSAVE命令创建快照。
save 900 1

#在300秒(5分钟)之后，如果⾄少有10个key发⽣变化，Redis就会⾃动触发BGSAVE命令创建快照。
save 300 10 

#在60秒(1分钟)之后，如果⾄少有10000个key发⽣变化，Redis就会⾃动触发BGSAVE命令创建快照。
save 60 10000
```

**缺点：**

- 如果系统发生故障，将会丢失最后一次创建快照之后的数据。
- 如果数据量很大，保存快照的时间会很长。

**AOF（append-only file）持久化**

与快照持久化相⽐，AOF 持久化 的实时性更好，因此已成为主流的持久化⽅案。**默认情况下Redis 没有开启 AOF**（append only file）⽅式的持久化，可以通过 appendonly 参数开启：

```
appendonly yes
```

开启 AOF 持久化后每执⾏⼀条会更改 Redis 中的数据的命令，Redis 就会将该命令写⼊硬盘中的 AOF ⽂件。AOF ⽂件的保存位置和 RDB ⽂件的位置相同，都是通过 dir 参数设置的，默认的⽂件名是 appendonly.aof。

在 Redis 的配置⽂件中存在三种不同的 AOF 持久化⽅式，它们分别是：

```
#每次有数据修改发⽣时都会写⼊AOF⽂件,这样会严重降低Redis的速度
appendfsync always
#每秒钟同步⼀次，显示地将多个写命令同步到硬盘
appendfsync everysec 
#让操作系统决定何时进⾏同步
appendfsync no 
```

为了兼顾数据和写⼊性能，⽤户可以考虑 **appendfsync everysec** 选项 ，让 Redis 每秒同步⼀次AOF ⽂件，Redis 性能⼏乎没受到任何影响。⽽且这样即使出现系统崩溃，⽤户最多只会丢失⼀秒之内产⽣的数据。当硬盘忙于执⾏写⼊操作的时候，Redis 还会优雅的放慢⾃⼰的速度以便适应硬盘的最⼤写⼊速度。

**缺点：**

随着服务器写请求的增多，AOF 文件会越来越大。Redis 提供了一种将 AOF 重写的特性，能够去除 AOF 文件中的冗余写命令。

**AOF 重写**
AOF 重写可以产⽣⼀个新的 AOF ⽂件，这个新的 AOF ⽂件和原有的 AOF ⽂件所保存的数据库状态⼀样，但体积更⼩。
AOF 重写是⼀个有歧义的名字，该功能是通过读取数据库中的键值对来实现的，程序⽆须对现有AOF ⽂件进⾏任何读⼊、分析或者写⼊操作。
在执⾏ BGREWRITEAOF 命令时，Redis 服务器会维护⼀个 AOF 重写缓冲区，该缓冲区会在⼦进程创建新 AOF ⽂件期间，记录服务器执⾏的所有写命令。当⼦进程完成创建新 AOF ⽂件的⼯作之后，服务器会将重写缓冲区中的所有内容追加到新 AOF ⽂件的末尾，使得新旧两个 AOF ⽂件所保存的数据库状态⼀致。最后，服务器⽤新的 AOF ⽂件替换旧的 AOF ⽂件，以此来完成AOF ⽂件重写操作

**Redis 4.0 对于持久化机制的优化**
Redis 4.0 开始⽀持 RDB 和 AOF 的混合持久化（默认关闭，可以通过配置项 aof-use-rdb-preamble 开启）。
如果把混合持久化打开，AOF 重写的时候就直接把 RDB 的内容写到 AOF ⽂件开头。这样做的好处是可以结合 RDB 和 AOF 的优点, 快速加载同时避免丢失过多的数据。当然缺点也是有的，AOF ⾥⾯的 RDB 部分是压缩格式不再是 AOF 格式，可读性差。

**事务**

Redis 是不⽀持 roll back 的，因⽽不满⾜原⼦性的（⽽且不满⾜持久性）。

Redis事务提供了⼀种将多个命令请求打包的功能。然后，再按顺序执⾏打包的所有命令，并且不会被中途打断。

一个事务包含了多个命令，服务器在执行事务期间，不会改去执行其它客户端的命令请求。

事务中的多个命令被一次性发送给服务器，而不是一条一条发送，这种方式被称为流水线，它可以减少客户端与服务器之间的网络通信次数从而提升性能。

Redis 最简单的事务实现方式是使用 MULTI 和 EXEC 命令将事务操作包围起来。

### redis常见生产问题

#### **缓存穿透**

缓存穿透说简单点就是⼤量请求的 key 根本不存在于缓存中，导致请求直接到了数据库上，根本没有经过缓存这⼀层。举个例⼦：某个⿊客故意制造我们缓存中不存在的 key 发起⼤量请求，导致⼤量请求落到数据库。

**解决办法：**

- 缓存⽆效 key（攻击者可能制造大量不存在数据的请求，打爆redis，设置过期时间尽量短也只能缓解）

- 布隆过滤器

  先通过哈希函数对元素值进行计算，得到哈希值，再把位数组中的对应下标的值置为1。

  把所有可能存在的请求的值都存放在布隆过滤器中，当⽤户请求过来，先判断⽤户发来的请求的值是否存在于布隆过滤器中。不存在的话，直接返回请求参数错误信息给客户端，存在的话才会⾛下⾯的流程。

#### 缓存雪崩

缓存在同⼀时间⼤⾯积的失效，后⾯的请求都直接落到了数据库上，造成数据库短时间内承受⼤量请求。 这就好⽐雪崩⼀样，摧枯拉朽之势，数据库的压⼒可想⽽知，可能直接就被这么多请求弄宕机了。

**解決办法：**

- 采⽤ Redis 集群，避免单机出现问题整个缓存服务都没办法使⽤。
- 限流，避免同时处理⼤量的请求。
- 设置不同的失效时间⽐如随机设置缓存的失效时间。

#### 缓存击穿

有⼀些被⼤量访问数据（热点缓存）在某⼀时刻⼤⾯积失效，导致对应的请求直接落到了数据库上。

**解决办法：**

- 缓存永不失效。
- 定时更新

### 如何保证缓存和数据库数据的⼀致性

- 延时双删策略

  删缓存

  写数据库

  休眠业务读取数据库的时间

  再次删除缓存

- 待删除数据丢到消息队列确保数据删除成功

- 和业务协商定期刷新缓存

### **缓存无底洞现象**

指的是为了满足业务要求添加了大量缓存节点，但是性能不但没有好转反而下降了的现象。

**产生原因：**缓存系统通常采用 hash 函数将 key 映射到对应的缓存节点，随着缓存节点数目的增加，键值分布到更多的节点上，导致客户端一次批量操作会涉及多次网络操作，这意味着批量操作的耗时会随着节点数目的增加而不断增大。此外，网络连接数变多，对节点的性能也有一定影响。

**解决方案：**

- 优化批量数据操作命令；
- 减少网络通信次数；
- 降低接入成本，使用长连接 / 连接池，NIO 等。

### **主从复制**

通过使用 slave of host port 命令来让一个服务器成为另一个服务器的从服务器。一个从服务器只能有一个主服务器，并且不支持主主复制。

**连接过程**

1. 主服务器创建快照文件，发送给从服务器，并在发送期间使用缓冲区记录执行的写命令。快照文件发送完毕之后，开始向从服务器发送存储在缓冲区中的写命令；
2. 从服务器丢弃所有旧数据，载入主服务器发来的快照文件，之后从服务器开始接受主服务器发来的写命令；
3. 主服务器每执行一次写命令，就向从服务器发送相同的写命令。

**主从链**

随着负载不断上升，主服务器可能无法很快地更新所有从服务器，或者重新连接和重新同步从服务器将导致系统超载。为了解决这个问题，可以创建一个中间层来分担主服务器的复制工作。中间层的服务器是最上层服务器的从服务器，又是最下层服务器的主服务器。

### Sentinel（哨兵）

Sentinel（哨兵）可以监听集群中的服务器，并在主服务器进入下线状态时，自动从从服务器中选举出新的主服务器。

### 分片

分片是将数据划分为多个部分的方法，可以将数据存储到多台机器里面，这种方法在解决某些问题时可以获得线性级别的性能提升。

**三种分片方式**：

- 客户端分片：客户端使用一致性哈希等算法决定键应当分布到哪个节点。
- 代理分片：将客户端请求发送到代理上，由代理转发请求到正确的节点上。
- 服务器分片：Redis Cluster。

###  Redis 单线程模型详解

Redis 基于 Reactor 模式来设计开发了⾃⼰的⼀套⾼效的事件处理模型 （Netty 的线程模型也基于 Reactor 模式），这套事件处理模型对应的是 Redis中的⽂件事件处理器（file event handler）。由于**⽂件事件处理器（file event handler）是单线程⽅式运⾏的**，所以我们⼀般都说 Redis 是单线程模型。

Redis 通过IO 多路复⽤程序 来监听来⾃客户端的⼤量连接（或者说是监听多个 socket），它会将感兴趣的事件及类型(读、写）注册到内核中并监听每个事件是否发⽣。

**优点：** I/O 多路复⽤技术的使⽤让 Redis 不需要额外创建多余的线程来监听客户端的⼤量连接，降低了资源的消耗（和 NIO 中的Selector 组件很像）。

**⽂件事件处理器（file event handler）主要是包含 4 个部分：**

- 多个 socket（客户端连接）
- IO 多路复⽤程序（⽀持多个客户端连接的关键）
- ⽂件事件分派器（将 socket 关联到相应的事件处理器）
- 事件处理器（连接应答处理器、命令请求处理器、命令回复处理器）

**Redis6.0 引⼊多线程**主要是为了提⾼⽹络 IO 读写性能，因为这个算是 Redis 中的⼀个性能瓶颈（Redis 的瓶颈主要受限于内存和络）。

虽然，Redis6.0 引⼊了多线程，但是 Redis 的多线程只是在⽹络数据的读写这类耗时操作上使⽤了， 执⾏命令仍然是单线程顺序执⾏。

因此，你也不需要担⼼线程安全问题。Redis6.0 的多线程默认是禁⽤的，只使⽤主线程。如需开启需要修改 redis 配置⽂件 redis.conf。

# 六、操作系统todo
