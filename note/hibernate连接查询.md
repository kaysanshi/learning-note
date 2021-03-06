## hibernate连接查询：

### 连接查询

#### 内连接

在HQL中，inner join关键字表示内连接（inner关键字可以省略，单独使用join默认表示内连接）。只要两个持久化类对应的表的关联字段之间有相符的值，内连接就会组合两个表中的连接。内连接在一对多或多对一的关联中比较常见。
下述代码用于实现任务描述6.D.9，在Customer.hbm.xml文件中对orders集合设置了延迟检索策略，利用HQL的inner join来查询用户名以“z”开头的Customer对象的所有订单编号。
【描述6.D.9】HQL内连接

```java
public static void findCustomerByJoin() {
  Session session = HibernateUtils.getSession();
  String hql = "from Customer c inner join c.orders o where c.userName like :name";
  Query query = session.createQuery(hql);
  query.setString("name", "z%");
  //list对象中包含多个Object[]对象，每个Object[]的长度为2
  List<Object[]> list = query.list();
  for (Object[] objs : list) {
      Customer customer = (Customer)objs[0];
      System.out.print(customer.getId()+" "+customer.getUserName()+" ");
      Order order = (Order) objs[1];
      System.out.print(order.getOrderNo());
      System.out.println();
  }
}
```

上述结果中，分别在控制台打印了Customer的userName和Order的orderNo信息，结果中打印了两次“1”，说明这两组Object[]对象数组重复引用OID为1的Customer对象。此外，由于在Customer.hbm.xml文件中对orders集合配置了延迟检索策略，因此，orders集合并没有被初始化。只有当程序第一次调用OID为1的Customer对象的getOrders().iterator()方法时，才会初始化Customer对象的orders集合。
如果要求Query的list()方法返回的集合中仅包含Customer对象，可以在HQL语句中使用select关键字，HQL语句如下所示。

String hql = "select c from Customer c inner join c.orders o where c.userName like :name";
此时，在Query的list()方法返回的list对象中，只包含Customer类型的数据。
注意 如果在Customer.hbm.xml映射文件中，对orders集合设置了立即检索策略，HQL在执行inner join的同时，会把Customer对象的orders集合属性初始化。
#### 预先抓取内连接

在HQL查询语句中，“inner join fetch”表示预先抓取内连接，“inner join”在默认情况下是延迟加载的，而使用“fetch”关键字后会一次性取出当前对象和该对象的关联实例或关联集合，这种情况就是“预先抓取（预先加载）”。
下述代码用于实现任务描述6.D.9，利用HQL的inner join fetch查询用户名以“z”开头的Customer对象的所有订单编号。 
【描述6.D.9】HQL预先抓取内连接

```java
public static void findCustomerByFetchJoin() {
  Session session = HibernateUtils.getSession();
  String hql = "from Customer c inner join fetch c.orders o where c.userName
like :name";
  Query query = session.createQuery(hql);
  query.setString("name", "z%");
  List<Customer> list = query.list();
  for (Customer customer : list) {
      System.out.print(customer.getId() + " " + customer.getUserName()- " ");
  rder : customer.getOrders()) {
            System.out.print(order.getOrderNo() + " ");
        }
        System.out.println();
    }
  }
```



#### 左外连接

在HQL中，left outer join关键字表示左外连接（可以省略outer关键字，left join默认为左外连接）。在使用左外连接查询时，将根据映射文件的配置来决定orders集合的检索策略。
下述代码用于实现任务描述6.D.9，在Customer.hbm.xml文件中对orders集合设置了延迟检索策略，利用HQL的left outer join来查询年龄大于18的Customer对象的所有订单编号。
【描述6.D.9】HQL左外连接

```java
public static void findCustomerByLeftJoin() {
        int age = 17;
        Session session = HibernateUtils.getSession();
        String hql = "from Customer c left outer join c.orders o where c.age >?";
        Query query = session.createQuery(hql);
        query.setInteger(0, age);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            Customer customer = (Customer) objs[0];
            System.out.print(customer.getId() + " " + customer.getUserName()
                      + " ");
            Order order = (Order) objs[1];
            if(objs[1]！= null)
                System.out.print(order.getOrderNo());
            System.out.println();
        }
    }
```


上述代码中，对Customer和Order类使用了左外连接的查询方式，通过Query对象的list()方法返回满足条件的元素集合，每个元素对应查询结果中的一条记录，都是Object[]类型，并且其长度为2。每个Object[]数组中都存放了一对Customer和Order对象。与内连接查询不同的是，如果Customer对象满足条件，而该对象没有对应的Order对象，这时Hibernate仍然将该对象检索出来，只不过objs[1]的值为null。执行结果如下所示。

1 zhangsan 20100706
1 zhangsan 20100712
2 lisi
上述结果中，分别在控制台打印了Customer的userName和Order的orderNo信息，输出结果中打印了两次“1”和一次“2”，说明前两组Object[]对象数组引用OID为1的同一个Customer对象。而对于OID为2的Customer对象，由于没有对应的Order对象，所以其没有对应的订单信息。
此外，由于在Customer.hbm.xml文件中对orders集合配置了延迟检索策略，因此orders集合并没有被初始化。只有当程序第一次调用OID为1的Customer对象的getOrders().iterator()方法时，才会初始化Customer对象的orders集合。
如果要求Query的list()方法只返回Customer对象，可以在HQL语句中使用select关键字，HQL语句如下所示。

String hql = "select c from Customer c left outer join c.orders o where c.age> ?;
此时，在Query的list()方法返回的list对象中，只包含Customer类型的数据。
注意 如果在Customer.hbm.xml映射文件中，对orders集合设置了立即检索策略，HQL在执行left outer join的同时，会把Customer对象的orders集合属性初始化。

#### 预先抓取左外连接

在HQL查询语句中，left outer join fetch表示预先抓取左外连接，也可以省略为left join fetch。
下述代码用于实现任务描述6.D.9，利用HQL的left outer join fetch来查询年龄大于18的Customer对象的所有订单编号。
【描述6.D.9】HQL预先抓取左外连接

```java
public static void findCustomerByLeftFetch() {
    int age = 17;
    Session session = HibernateUtils.getSession();
    String hql = "from Customer c left join fetch c.orders where c.age >?";
    Query query = session.createQuery(hql);
    query.setInteger(0, age);
    List<Customer> list = query.list();
        for (Customer customer :list) {
        System.out.print(customer.getId() + " " + customer.getUserName()

- " ");
  er : customer.getOrders()) {
              System.out.print(order.getOrderNo() + " ");
          }
          System.out.println();
      }
  }
```

上述代码中，使用了预先抓取左外连接的查询方式，Query对象的list()方法返回的集合中包含了满足条件的元素，每个元素都是Customer类型的，并且每个Customer类型的对象中的orders集合已经被初始化。
在Hibernate中，还可以通过使用Projection接口实现Criteria的方式来进行分组与统计查询。Projection接口类位于org.hibernate.criterion包中，通过Criteria对象的setProjection()方法将投影应用到一个查询中。此外，通过Projection接口的工厂类Projections获取常用的统计函数，Projections类和Projection接口位于同一包中。
例如：
分组排序时：

```java
public static void groupByCustomer2(){
    Session session = HibernateUtils.getSession();
    Criteria criteria = session.createCriteria(Customer.class);
    ProjectionList p = Projections.projectionList();
    p.add(Projections.groupProperty("userName"));
    p.add(Projections.rowCount());
    criteria.setProjection(p);
    List<Object[]> list = criteria.list();
    for (Object[] objs : list) {
        System.out.println("用户名："+objs[0]+",个数: "+objs[1]);
    }
}
```



### QBC动态查询

```java
public static List<Customer> findCustomersByCriteria(String name,
        Integer age) {
    Session session = HibernateUtils.getSession();
    Criteria criteria = session.createCriteria(Customer.class);
    if (name！= null) {
        criteria.add(Restrictions.ilike("userName", name,
                  MatchMode.ANYWHERE));
    }
    if (age！= null && age！= 0) {
        criteria.add(Restrictions.eq("age", age));
    }
    return criteria.list();
}
```

上述代码中，利用QBC来动态生成查询语句，在代码的编写过程中，不需要考虑HQL语句复杂的拼凑，只需要把满足条件的Criterion对象放入Criteria对象中即可。与HQL相比代码简单了许多。
DetachedCriteria离线查询

```java
public class BusniessService {
    public static void main(String[] args) {
        DetachedCriteria cri = DetachedCriteria.forClass(Customer.class);
        // 根据用户的动态查询条件，创建DetachedCriteria对象
        cri.add(Restrictions.eq("age", 18));
        cri.add(Restrictions.ilike("userName", "z", MatchMode.ANYWHERE));
        List<Customer> list = findCustomers(cri);
        //结果最终会在表示层显示
        for (Customer customer : list) {
            System.out.println(customer.getUserName());
        }
    }
    // 在业务逻辑层把DetachedCriteria对象与Session对象绑定，并返回查询结果
    public static List<Customer> findCustomers(DetachedCriteria      detachedCriteria) {
        Session session = HibernateUtils.getSession();
        Criteria criteria = detachedCriteria.getExecutableCriteria(session);
        return criteria.list();
    }
}
```

上述代码中，利用DetachedCriteria的静态方法forClass()来创建基于Customer类的DetachedCriteria对象，然后把查询条件加入到该对象中，接着将该对象传递到findCustomers()方法中。注意 DetachedCriteria在底层依赖于Criteria对象，因此，操作DetachedCriteria对象的一些方法时，如add()方法，实际上操作的是Criteria对象中的add()方法，对于DetachedCriteria的其他方法也与Criteria中的类似。与Criteria不同的是，DetachedCriteria对象可以独立于Session对象来创建，但在条件查询时则必须与Session对象绑定来实现查询。

### AOP把软件系统分为两个部分：核心关注点和横切关注点。核心关注点是业务处理的主要流程，而横切关注点是与核心业务无关的部分，它常常发生在核心关注点的周围并且代码类似或相同，如日志、权限等。

横切关注点虽然与核心业务的实现无关，但却是一种更为通用的业务，各个横切关注点离散地穿插于核心业务之中，导致系统中的每一个模块都与这些业务具有很强的依赖性。横切关注点所代表的行为就是“切面”。

总之，OOP提高了代码的重用，而AOP将分散在各个业务逻辑中的相同代码，通过横向切割的方式抽取成一个独立的模块，使得业务逻辑类更加简洁明了在AOP编程过程中，需要开发人员参与的有三个方面。

定义普通业务类；

定义切入点，一个切入点可能横切多个业务组件；

定义增强，增强就是在AOP框架为普通业务组件织入的处理逻辑。

对于AOP编程而言，最关键的就是定义切入点和增强，一旦定义了合适的切入点和增强，AOP框架会自动生成AOP代理。

```xml
 <aop:config proxy-target-class="true">
        <aop:aspect id="adviceAspect" ref="aspectBean">
    <！-- 配置Before增强，以切面Bean中的checkAuth()方法作为增强处理方法 -->
            <aop:before method="checkAuth"
                  pointcut="execution(* com..*.*Service.*(..))" />
    <！-- 配置AfterReturning增强，以切面中的log()方法作为增强处理方法 -->
            <aop:after-returning method="log"
                  pointcut="execution(* com..*.*Service.select(..))"
                  returning="result" />
    <！-- 配置AfterThrowing增强，以切面中的processException方法作为增强处理方法-->
            <aop:after-throwing method="processException"
                pointcut="execution(* com..*.*Service.*(..))"
                throwing="ex" />
    <！-- 配置Around增强，以切面中的processTrans()方法作为增强处理方法 -->
            <aop:around method="proceedInTrans"
                pointcut="execution(* com..*.*Service.*(..))" />
    <！-- 配置After增强，以切面Bean中的release()方法作为增强处理方法 -->
            <aop:after method="release"
                pointcut="execution(* com..*.*Service.*(..))" />
        </aop:aspect>
    </aop:config>
    <！-- 配置日志切面 -->
    <bean id="aspectBean" class="com.haiersoft.ch08.aspect.AspectBean" />
    <！-- 配置UserService类 -->
    <bean id="userService" class="com.haiersoft.ch08.service.UserServiceImpl" />
……省略
```

上述配置中，配置了Before增强、AfterReturning增强、AfterThrowing增强、Around增强和After增强。在每个增强元素中method属性值都设定为切面中的方法，例如，在Before增强中method属性值为“checkAuth”，对应切面AspectBean中的checkAuth()方法，表示当满足条件时会调用切面AspectBean的checkAuth()方法；pointcut属性的值为切入点表达式：

execution(* com..*.*Service.*(..))*

表示“com”包或其子孙包中，名称以“Service”结尾的接口/类中任意的方法，如果被调用的方法满足上述条件，则会被增强，即在调用的过程中首先会被检查是否具备调用该方法的权限。

注意 当<aop:config…/>中proxy-target-class属性值设置为true时，表示其中声明的切面均使用CGLib动态代理技术；当设置为false时，使用JDK动态代理技术，一个配置文件可以同时定义多个<aop:config…/>，不同的<aop:config…/>可以采用不同的代理技术。

切入点表达式：`execution(* com.haiersoft..*(..))&&args(com.haiersoft.ch08.pojos.User)`

可以使用“*”和“..”通配符来表示方法的形参，其中“*”表示任意类型的参数；
而“..”表示任意类型参数且参数个数是0个或多个。

使用注解配置通知：

```java
@Aspect
public class AspectBean {
    /**
    * 模拟进行权限检查
        */
        @Before("execution(* com..*.*Service.*(..))")
        public void checkAuth() {
        System.out.println("权限检查...");
        }
        /**
    * 模拟释放资源
        */
        @After("execution(* com..*.*Service.*(..))")
        public void release() {
        System.out.println("最后释放资源...");
        }
        /**
        *日志记录功能
        */
        @AfterReturning(returning="result",pointcut="execution(*
        com..*.*Service.select(..))")
        public void log(Object result) {
        if (result == null) {
            System.out.println("数据库中无结果！");
        } else {
            System.out.println("数据库中有结果！");
        }
        }
        /**
      * 模拟异常处理
      */
        @AfterThrowing(
pointcut="execution(* com..*.*Service.*(..))",throwing="ex")
        public void processException(Throwable ex) {
        System.out.println("异常信息为:" + ex.getMessage());
        }
        /**
      * 模拟进行事务操作
      */
        @Around("execution(* com..*.*Service.*(..))")
        public void proceedInTrans(ProceedingJoinPoint joinpoint)
        throws Throwable {
        System.out.println("开始事务...");
        joinpoint.proceed();
        System.out.println("提交事务...");
        }
}
```

> 需要在spring配置文件配置
> <！-- 启动AspectJ注解的支持 -->
>         <aop:aspectj-autoproxy/>
>         <！-- 配置切面Bean -->
>         <bean id="aspectBean" class="com.haiersoft.ch08.aspect.AspectBean" />