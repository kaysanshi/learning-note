## Spring注解：

在说spring注解时我们先看下什么是JAVA注解：Annotation(注解)是JDK1.5及以后版本引入的。它可以用于创建文档，跟踪代码中的依赖性，甚至执行基本编译时检查。注解是以‘@注解名’在代码中存在的，根据注解参数的个数，我们可以将注解分为`：标记注解、单值注解、完整注解`三类。它们都不会直接影响到程序的语义，只是作为注解（标识）存在，我们可以通过反射机制编程实现对这些元数据（用来描述数据的数据）的访问。

AnnotationConfigApplicationContext:是一个用来管理注解bean的容器

### 组件的添加：

![](https://mmbiz.qpic.cn/mmbiz_png/B77kSvewKqXRz1TazsVnVfXHaEO0ETqV9xyJUSPnRARAy8Eic9qHCXFwmFLptWzpQQg6HNWpffLFnibIJZuMmm8A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

​		@Configuration:告诉spring这是个配置类（等同于配置文件）
​		@ComponentScan:指定扫描的包	

```java
@ComponentScans(
        value = {
                @ComponentScan(value = "com.atguigu", includeFilters = {
                        /*	@Filter(type=FilterType.ANNOTATION,classes={Controller.class}),@Filter(type=FilterType.ASSIGNABLE_TYPE,classes={BookService.class}),*/
                        @Filter(type = FilterType.CUSTOM, classes = {MyTypeFilter.class})
                }, useDefaultFilters = false)
        }
)
```

> //@ComponentScan  value:指定要扫描的包
> //excludeFilters = Filter[] ：指定扫描的时候按照什么规则排除那些组件
> //includeFilters = Filter[] ：指定扫描的时候只需要包含哪些组件
> //FilterType.ANNOTATION：按照注解
> //FilterType.ASSIGNABLE_TYPE：按照给定的类型；
> //FilterType.ASPECTJ：使用ASPECTJ表达式
> //FilterType.REGEX：使用正则指定
> //FilterType.CUSTOM：使用自定义规则

​		`@Bean：`给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id

​		`@Component:`默认加在ioc容器中的组件，容器启动会调用无参构造器创建对象，再进行初始化赋值等操作

​		`@Controller：`声明是一个控制层 名字默认是类名首字母小写

​		`@Service：`声明是service层 名字默认是类名首字母小写 其中自己可以改变这个实例名字：	@Service("userService");

​		`@Repository：`声明是dao层。名字默认是类名首字母小写 同样可以自己手动书写实例名称

​		`@Conditional`: 按照一定的条件进行判断，满足条件给容器中注册bean

```java
/**
* @Conditional({Condition}) ： 按照一定的条件进行判断，满足条件给容器中注册bean
* 
* 如果系统是windows，给容器中注册("bill")
* 如果是linux系统，给容器中注册("linus")
*/
@Bean("bill")
public Person person01(){
return new Person("Bill Gates",62);
}

@Conditional(LinuxCondition.class)
@Bean("linus")
public Person person02(){
return new Person("linus", 48);
}
```

 `@Primary:` 让Spring进行自动装配的时候，默认使用首选的bean；也可以继续使用 `@Qualifier`指定需要装配的bean的名字

`@Lazy`：懒加载：

​	 		单实例bean：默认在容器启动的时候创建对象；懒加载：容器启动不创建对象。第一次使用(获取)Bean创建对象，并初始化；

`@scope` 默认为单例模式

```
/**
 * ConfigurableBeanFactory#SCOPE_PROTOTYPE
 * @see ConfigurableBeanFactory#SCOPE_SINGLETON
 * @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST  request
 * @see org.springframework.web.context.WebApplicationContext#SCOPE_SESSION     sesssion
 * @return\
 * @Scope:调整作用域
 * prototype：多实例的：ioc容器启动并不会去调用方法创建对象放在容器中。
 *                 每次获取的时候才会调用方法创建对象；
 * singleton：单实例的（默认值）：ioc容器启动会调用方法创建对象放到ioc容器中。
 *           以后每次获取就是直接从容器（map.get()）中拿，
 * request：同一次请求创建一个实例
 * session：同一个session创建一个实例
 *
 * 懒加载：
 *        单实例bean：默认在容器启动的时候创建对象；
 *        懒加载：容器启动不创建对象。第一次使用(获取)Bean创建对象，并初始化；
 *
 */
```

`@Import:`要导入到容器的组件,容器中就会自动的注册这个组件，id默认是全类名

```
/**
 * 给容器中注册组件；
 * 1）、包扫描+组件标注注解（@Controller/@Service/@Repository/@Component）[自己写的类]
 * 2）、@Bean[导入的第三方包里面的组件]
 * 3）、@Import[快速给容器中导入一个组件]
 *        1）、@Import(要导入到容器中的组件)；容器中就会自动注册这个组件，id默认是全类名
 *        2）、ImportSelector:返回需要导入的组件的全类名数组；
 *        3）、ImportBeanDefinitionRegistrar:手动注册bean到容器中
 * 4）、使用Spring提供的 FactoryBean（工厂Bean）;
 *        1）、默认获取到的是工厂bean调用getObject创建的对象
 *        2）、要获取工厂Bean本身，我们需要给id前面加一个&
 *           &colorFactoryBean
 */
```

### 组件赋值：

![](https://mmbiz.qpic.cn/mmbiz_png/B77kSvewKqXRz1TazsVnVfXHaEO0ETqV4tGYkQlibOBWjcxkOzRh1tHibVmJwVXmyib36jJ1aquZz0j09oZSkNJYQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

​	`@Value:` @Value赋值；

```java
	//1、基本数值
​	//2、可以写SpEL； #{}
​	//3、可以写${}；取出配置文件【properties】中的值（在运行环境变量里面的值）
​	@Value("张三")
​	private String name;
```



### 	自动装配bean: 

`@Autowired`通过匹配`数据类型`自动装配  `byType`方式:

​	//标注在方法，Spring容器创建当前对象，就会调用方法，完成赋值；
​	//方法使用的参数，自定义类型的值从ioc容器中获取

​	1）、默认优先按照类型去容器中找对应的组件:applicationContext.getBean(BookDao.class);找到就赋值

​	2）、如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找applicationContext.getBean("bookDao")

​	3）、`@Qualifier("bookDao")：`使用@Qualifier指定需要装配的组件的id，而不是使用属性名

​	4）、自动装配默认一定要将属性赋值好，没有就会报错；可以使用@Autowired(required=false);

​	5）、`@Primary：`让Spring进行自动装配的时候，默认使用首选的bean；也可以继续使用`@Qualifier`指定需要装配的bean的名字	@Qualifier：

​		`@Qualifier("bookDao")：`使用@Qualifier指定需要装配的组件的id，而不是使用属性名

​		`@Resources:`可以和@`Autowired`一样实现自动装配功能；默认是按照`组件名称`进行装配的 `byName`； 没有能支持@Primary功能没有支持@Autowired（reqiured=false）;

​		`@Resource(name="userDao")` 注入userDao根据用户id指定的用户时用到

​		`@Inject：`需要导入javax.inject的包，和Autowired的功能一样。没有required=false的功能；

​		`@PropertySource：`读取外部配置文件中的k/v保存到运行的环境变量中;加载完外部的配置文件以后使用${}取出配置文件的值`@PropertySource(value{"classpath:/person.properties"})`

​		`@PropertySources`

​		`@Profile：`指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件

​		1）、加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境

​		2）、写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效

​		3）、没有标注环境标识的bean在，任何环境下都是加载的；

### 组件注入：

![](https://mmbiz.qpic.cn/mmbiz_png/B77kSvewKqXRz1TazsVnVfXHaEO0ETqVSKiatlgs1a0tQtbAqTQkEAPYZYCv2dicYARkaImlJ7h3gRMp83EzoAFA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

​		方法参数
​		构造器
​		ApplicationContextAware

### Aop:

![](https://mmbiz.qpic.cn/mmbiz_png/B77kSvewKqXRz1TazsVnVfXHaEO0ETqVibfY9TS7UxDiad6krBJ6sHE7kH8CicALibrykUIPU4qFzcqscBp83Csd5g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

​		`@EnableAspectJAutoProxy：`开启AOP功能会给容器中注册一个组件 AnnotationAwareAspectJAutoProxyCreator

​		AnnotationAwareAspectJAutoProxyCreator是一个后置处理器；

​		`@Before/@Aftert/@AfterReturning/@AfterThrowing/@Around`

​		`@Pointcut`：切点

###  AOP：【动态代理】

指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式；

* 1、导入aop模块；Spring AOP：(spring-aspects)

* 2、定义一个业务逻辑类（MathCalculator）；在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束、方法出现异常，xxx）

* 3、定义一个日志切面类（LogAspects）：切面类里面的方法需要动态感知MathCalculator.div运行到哪里然后执行；

  **通知方法：**

  `前置通知(@Before)：logStart：在目标方法(div)运行之前运行`

  `后置通知(@After)：logEnd：在目标方法(div)运行结束之后运行（无论方法正常结束还是异常结束）`

  `返回通知(@AfterReturning)：logReturn：在目标方法(div)正常返回之后运行`

  `异常通知(@AfterThrowing)：logException：在目标方法(div)出现异常以后运行`

  `环绕通知(@Around)：动态代理，手动推进目标方法运行（joinPoint.procced()）`

* 4、给切面类的目标方法标注何时何地运行（通知注解）；

* 5、将切面类和业务逻辑类（目标方法所在类）都加入到容器中;

* 6、必须告诉Spring哪个类是切面类(给切面类上加一个注解：@Aspect)

* 7、给配置类中加 @EnableAspectJAutoProxy 【开启基于注解的aop模式】

  

声明式事务：
	`@EnableTransctionManagement：` 开启基于注解的事务管理功能；
	`@Transactional`: 给方法上标注 @Transactional 表示当前方法是一个事务方法；

### SpringMVC注解：

​		除了spring的基本注解你还可以使用下面的注解

​		`@EnableWebMvc:`  在配置类中开启Web MVC的配置支持，如一些ViewResolver或者MessageConverter等，若无此句，重写WebMvcConfigurerAdapter方法（用于对SpringMVC的配置）

​		`@RequestMapping("item/{id}")`  声明请求的url{xxx}叫做占位符，请求的URL可以是“item /1”或“item/2”

​		`@PathVariable `获取url上的数据

```java
使用(@PathVariable() Integer id)获取url上的数据		
/**
* 使用RESTful风格开发接口，实现根据id查询商品
* 如果不一致，例如"item/{ItemId}"则需要指定名称@PathVariable("itemId")。
* @param id
* @return
*/
@RequestMapping("item/{id}")
@ResponseBody
public Item queryItemById(@PathVariable() Integer id) {
Item item = this.itemService.queryItemById(id);
return item;
}
			
```
`@ PathVariable`  是 获取 url 上数据 的。 @RequestParam获取请求参数的（包括post表单提交）

`@ResponseBody` 注解，就不会走视图解析器，不会返回页面，目前返回的json数据。如果不加，就走视图解析器，返回页面
`@RequestBody`  允许request的参数在request体中，而不是在直接连接在地址后面。（放在参数前）

`@NumberFormat`    支持对数字类型的属性使用 @NumberFormat 注解

`@DateTimeFormat` 		– JodaDateTimeFormatAnnotationFormatterFactroy：支持对日期类型的属性使用 @DateTimeFormat 注解 ；可以对pattern 属性：类型为字符串。指定解析/格式化字段数据的模式，

如：”yyyy-MM-dd hh:mm:ss”等其他的
`@RequestParam(name="file") :`接收前端传入的参数 required=false或者true来要@RequestParam配置的前端参数是否一定要传 