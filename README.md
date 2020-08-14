# 一、SpringBoot入门

# 二、配置文件

## 1、配置文件

SpringBoot使用全局配置文件，配置文件名是固定的：

- application.properties
- application.yml

配置文件的作用：

- 修改SpringBoot自动配置的默认值
- 很多设置SpringBoot在底层都给我们自动配置好

## 2、YAML

> YAML 是 "YAML Ain't a Markup Language"（YAML 不是一种标记语言）的递归缩写。

### 1、基本语法

k:(空格)v：表示一对键值对（必须要有空格）；

以**空格**的缩进来控制层级关系；只要是左对齐的一列数据，都是同一个层级的；

```yaml
server:
    port: 8081
    path: /hello
```

属性和值也是大小写敏感。



### 2、值的写法

#### 字面量（数字，字符串，布尔）

```yaml
k: v
# 字符串默认不需要加单引号或者双引号;
# "":不会转义字符串里面的特殊字符
# '':会转义特殊字符

name: "zhangsan \n lisi"
# 输出 zhangsan \n lisi

name: 'zhangsan \n lisi'
# 输出 zhangsan 换行 lisi
```

#### 对象（Map）

```yaml
person:
  lastName: zhangsan
  age: 20
# 在下一行空格缩进，来写对象的键值对

# 行内写法 
person: {lastName: zhangsan,age: 20}
```

#### 数组（List、Set）

```yaml
# -表示数组元素
pet:
  - cat
  - dog
  - pig

# 行内写法
pets: [cat,dog,pig]
```





# 三、日志

## 1、日志框架

| 日志门面 | 日志实现               |
| -------- | ---------------------- |
| sl4j     | logback，log4j，log4j2 |

## 2、SL4J使用

### 1、在系统中使用sl4j

> 官网：http://www.slf4j.org/

sl4j对不同日志框架的支持：

1. 实现了sl4j-api的日志类，直接支持
2. 未实现了sl4j-api的日志类，通过适配层，向上实现sl4j-api，向下调用日志实现类。例如，log4j。

![sl4j](https://image.5460cc.com/springboot/sl4j-concrete-bindings.png)



使用：

1. 导入sl4j和日志实现框架（以及适配器，如需）
2. 实际开发时调用sl4j的接口，实现接口的是低层具体的日志实现框架
3. 日志配置文件仍使用日志实现框架的配置文件

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```



### 2、日志统一问题

问题：

我们自己的系统中使用的日志系统是sl4j+logback，但是系统依赖的库使用的是其他日志系统（比如依赖Spring，Spring使用的是commons-logging），如何让系统所有的日志都统一到sl4j？



解决方法：

1. ==排除系统中的其他日志框架==
2. ==用slf4j中间换皮包替换原有的日志框架==
3. ==导入sl4j-api和其他的实现==



底层原理：

1. 系统中依赖原有日志框架时，可以到中间换皮包中找到依赖的类（sl4j中间换皮包具有原有日志框架相同的包名和类名）
2. 中间换皮包统一调用sl4j-api
3. sl4j-api调用具体的日志实现框架

![sl4j-legacy](https://image.5460cc.com/springboot/sl4j-legacy.png)

## 3、SpringBoot日志关系

**SpringBoot使用的是sl4j+logback记录日志，引入其他库时，只需要把原有日志框架擦除掉就，引入中间包可以了。**

![springboot-logging](https://image.5460cc.com/springboot/springboot-logging.png)



## 4、日志使用

> SpringBoot日志相关文档：https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-logging

### 1、默认配置

**日志输出级别**

SpringBoot使用sl4j框架，sl4j的日志输出级别从低到高：trace<debug<info<warn<error。

**日志输出位置**

| logging.file.name              | logging.file.path           | 解析                                 |
| :----------------------------- | :-------------------------- | ------------------------------------ |
| 无                             | 无                          | 只在控制台打印                       |
| mylogging.log                  | 无                          | 项目目录下输出到mylogging.log        |
| mylogging.log                  | 绝对路径：/opt/data/wwwlog/ | 输出到/opt/data/wwwlog/mylogging.log |
| mylogging.log                  | 相对路径：./                | 项目目录下输出到mylogging.log        |
| 无                             | 绝对路径：/opt/data/wwwlog/ | 输出到/opt/data/wwwlog/spring.log    |
| 无                             | 相对路径：./                | 项目目录下输出到spring.log           |
| /opt/data/wwwlog/mylogging.log | ./                          | logging.file.name覆盖path的设置      |

总结：

1. 都不指定，不会输出到文件
2. 文件默认输出位置是项目当前目录
3. 文件名默认是spring.log
4. logging.file.name是路径+文件名的时候，覆盖path的设置

**日志输出格式**

logback输出格式：http://logback.qos.ch/manual/layouts.html

**参考配置**

```properties
# 日志输出级别
logging.level.root=debug

# 日志输出文件名
logging.file.name=mylogging.log
# 日志输出目录
logging.file.path=/opt/data/wwwlog/

# 控制台日志输出格式
# 时间 [ 级别 ] [ 线程ID ] --- [ 行号 ] [ 全类名 ] 信息
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [ %level ] [ %t ] --- [ %L ] [ %-20C ] %m %n
# 日志文件输出格式
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [ %level ] [ %t ] --- [ %L ] [ %-20C ] %m %n
```

### 2、指定配置

SpringBoot可以在类路径下放置日志的指定配置文件，或者通过*logging.config*指定配置文件的位置。

SpringBoot可以自动识别加载以下日志配置：

| 日志系统                | 指定配置文件                    |
| ----------------------- | ------------------------------- |
| logback                 | logback-spring.xml，logback.xml |
| log4j2                  | log4j2-spring.xml，log4j2.xml   |
| JDK (Java Util Logging) | logging.properties              |

logback-spring.xml配置文件：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration scan="false" scanPeriod="60 seconds" debug="false">
    <!--定义日志文件的存储地址，勿在LogBack 的配置中使用相对路径-->
    <property name="LOG_HOME" value="/opt/data/wwwlogs/springboot" />
    <property name="APP_NAME" value="springboot-logging" />

    <!--控制台日志， 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!-- springProfile 仅在文件名为logback-spring.xml时支持 -->
            <springProfile name="dev">
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [ %-5level ] [ %t ] --- [ %L ] [ %-20C ] %m %n</pattern>
            </springProfile>
            <springProfile name="!dev">
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [ %-5level ] [ %t ] === [ %L ] [ %-20C ] %m %n</pattern>
            </springProfile>
        </encoder>
    </appender>

    <!--
        RollingFileAppender：滚动记录文件，现将日志记录到指定文件，符合滚动条件时，将日志记录到其他文件。
    -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--
            滚动策略：常见有TimeBasedRollingPolicy和SizeBasedTriggeringPolicy，根据时间和文件大小滚动。
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天滚动 -->　　　　　　
            <fileNamePattern>${LOG_HOME}/${APP_NAME}.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 最多保留近30天的日志 -->
            <maxHistory>30</maxHistory>　
        </rollingPolicy>　　　　　　　　
        <encoder>　　　　　　　　　　　　
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [ %-5level ] [ %t ] --- [ %L ] [ %-20C ] %m %n</pattern>　　　　　　　　　　　　
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>10MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <logger name="org.springframework.boot" level="INFO" />
    <logger name="com.chan.springboot" level="TRACE" />

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## 5、切换日志框架

### 1、切换到log4j1.2

参考sl4j提供的统一日志框架方法

1. 排除原有的日志实现框架，springboot默认的是logback
2. 排除统一日志框架用到的中间换皮包：sl4j-over-log4j，sl4j-to-log4j（原本使用log4j记录日志的不需要统一）
3. 引入sl4j-log4j12包，添加log4j配置文件到类路径

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <!-- 排除logback -->
                <exclusion>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-classic</artifactId>
                </exclusion>

                <!-- 排除log4j-to-slf4j，因为原本使用log4j的无需改变 -->
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>log4j-to-slf4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- 引入sl4j-log4j12 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </dependency>
</dependencies>
```

### 2、spring-boot-starter-log4j2

1. 排除spring-boot-starter-logging
2. 引入spring-boot-starter-log4j2
3. 添加log4j2的配置文件

log4j2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，你会看到log4j2内部各种详细输出。
    monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数。
-->
<configuration status="error" monitorInterval="30">
    <!--先定义所有的appender-->
    <appenders>
        <!--这个输出控制台的配置-->
        <Console name="Console" target="SYSTEM_OUT">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
            <!--这个都知道是输出日志的格式-->
            <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
        </Console>
        <!--文件会打印出所有信息，这个log每次运行程序会自动清空，由append属性决定，这个也挺有用的，适合临时测试用-->
        <File name="log" fileName="/opt/data/wwwlogs/springboot/app.log" append="false">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
        </File>
        <!-- 这个会打印出所有的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFile" fileName="/opt/data/wwwlogs/springboot/springboot-logging.log"
                     filePattern="/opt/data/wwwlogs/springboot/$${date:yyyy-MM}/app-%d{MM-dd-yyyy}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>
            <SizeBasedTriggeringPolicy size="50MB"/>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件，这里设置了20 -->
            <DefaultRolloverStrategy max="20"/>
        </RollingFile>
    </appenders>
    <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
    <loggers>
        <!--建立一个默认的root的logger-->
        <root level="INFO">
            <appender-ref ref="RollingFile"/>
            <appender-ref ref="Console"/>
        </root>
    </loggers>
</configuration>
```



日志配置总结

1. appenders：日志输出器，定义输出目标，级别，信息等
2. loggers：指定项目、包、类使用何种appenders记录日志

# 四、web开发

## 1、简介

SpringBootWeb

1. 创建SpringBoot项目，选择需要的模块；
2. SpringBoot已经默认将这些场景配置好了，只需要在配置文件中指定少量配置就可以运行；
3. 编写业务代码。



思考：

1. SpringBoot完成了那些自动配置？
2. 如何修改这些配置？
3. 如何扩展这些配置？



自动配置：

xxxxAutoConfiguration：帮我们给容器中自动配置组件；
xxxxProperties：配置类来封装配置文件的内容；

## 2、静态资源映射规则

```java
package org.springframework.boot.autoconfigure.web.servlet;

public class WebMvcAutoConfiguration {
    
    //...
    
    @Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			if (!this.resourceProperties.isAddMappings()) {
				logger.debug("Default resource handling disabled");
				return;
			}
			Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
			CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
			if (!registry.hasMappingForPattern("/webjars/**")) {
				customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
						.addResourceLocations("classpath:/META-INF/resources/webjars/")
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
			String staticPathPattern = this.mvcProperties.getStaticPathPattern();
			if (!registry.hasMappingForPattern(staticPathPattern)) {
				customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
						.addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
		}
}
```



```java
package org.springframework.boot.autoconfigure.web;

@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties {
	
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };
    
	// 静态资源的位置
    // WebMvcAutoConfiguration中this.resourceProperties.getStaticLocations()使用了该属性
	private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
    
    //...
}
```

> 官方文档：https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration

### 1、webjars

所有/webjars/路径请求，都去classpath:/META-INF/resources/webjars/下找资源。

webjars官网：https://www.webjars.org/

```xml
<!-- webjars 引入jquery -->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
</dependency>
```

![](https://image.5460cc.com/springboot/web-webjars-jquery.png)

访问：http://localhost:8080/webjars/jquery/3.5.1/jquery.js

### 2、静态资源目录

```
classpath:/META-INF/resources/
classpath:/resources/
classpath:/static/
classpath:/public/
这些目录对应请求路径的/根路径，优先级从高到低
```

举例：/static/asserts/js/Chart.min.js ===> http://localhost:8080/asserts/js/Chart.min.js

```yaml
spring:
  resources:
    # 自定义静态资源目录，优先级为数组顺序
    static-locations:
      - classpath:/mystatic1
      - classpath:/mystatic2
```

### 3、欢迎页

 静态资源目录下的index.html文件 ===> http://localhost:8080/

### 4、图标

**/favicon.ico 都对应静态资源目录下favicon.ico

## 3、模板引擎

### 1、简介

模板引擎将数据渲染到预定义的模板中，生成html文档输出。

![](https://image.5460cc.com/springboot/web-temaplate.png)

### 2、Thymeleaf

> 官网：https://www.thymeleaf.org/

#### 1、引入

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

#### 2、使用

```java
package org.springframework.boot.autoconfigure.thymeleaf;

@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";
}
```

模板的路径：**classpath:/templates/**，后缀是**.html**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功！</h1>
    <!--th:text 将div里面的文本内容设置为 -->
    <div th:text="${hello}">这是显示欢迎信息</div>
</body>
</html>
```



#### 3、语法

（1）属性

![](https://image.5460cc.com/springboot/web-thymeleaf-precedence.png)

（2）表达式

```properties
Simple expressions:（表达式）
    Variable Expressions: ${...}：变量的值，OGNL
        （1）获取对象的属性，调用方法
        （2）内置对象：
            #ctx: the context object.
            #vars: the context variables.
            #locale: the context locale.
            #request: (only in Web Contexts) the HttpServletRequest object.
            #response: (only in Web Contexts) the HttpServletResponse object.
            #session: (only in Web Contexts) the HttpSession object.
            #servletContext: (only in Web Contexts) the ServletContext object.
            ${#ctx.session}
            ${session.foo}
		（3）内置的一些工具对象：#execInfo
    Selection Variable Expressions: *{...}：选择表达式，和${...}功能一样
    	配合 th:object，对所选择的对象求值
    	<div th:object="${session.user}">
            <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
            <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
            <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
        </div>
    Message Expressions: #{...}：获取国际化内容
    Link URL Expressions: @{...}：定义URL
    	@{/order/process(execId=${execId},execType='FAST')}
    Fragment Expressions: ~{...}：片段引用表达式
    
Literals（字面量）
    Text literals: 'one text', 'Another one!',…
    Number literals: 0, 34, 3.0, 12.3,…
    Boolean literals: true, false
    Null literal: null
    Literal tokens: one, sometext, main,…
Text operations:（文本操作）
    String concatenation: +
    Literal substitutions: |The name is ${name}|
Arithmetic operations:（数学运算）
    Binary operators: +, -, *, /, %
    Minus sign (unary operator): -
Boolean operations:（布尔运算）
    Binary operators: and, or
    Boolean negation (unary operator): !, not
Comparisons and equality:（比较运算）
    Comparators: >, <, >=, <= (gt, lt, ge, le)
    Equality operators: ==, != (eq, ne)
Conditional operators:（条件运算）
    If-then: (if) ? (then)
    If-then-else: (if) ? (then) : (else)
    Default: (value) ?: (defaultvalue)
Special tokens:
	No-Operation: _
```



## 4、SpringMVC自动配置

> 官方文档：https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications

### 1、Spring MVC auto-configuration

SpringBoot为SpringMVC提供了自动配置。

在Spring的默认基础上，自动配置添加了如下特性：

- 包含 `ContentNegotiatingViewResolver` 和 `BeanNameViewResolver` bean；

  - 自动配置了`ViewResolver`（视图解析器，根据方法的返回值获得视图对象，决定如何返回，渲染？重定向？转发？）
  - `ContentNegotiatingViewResolver` ：组合所有的视图解析器
  - ==如何定制：给容器中添加自己的视图解析器，会自动组合进来==

- 自动注册 `Converter`， `GenericConverter`，`Formatter` bean；

  - Converter：转换器，类型转换器
  - Formatter：格式化器

- 支持 `HttpMessageConverters`；

  - HttpMessageConverter：SpringMVC用来转换Http请求和响应的；

  - `HttpMessageConverters` 是从容器中确定；获取所有的HttpMessageConverter；

    ==自己给容器中添加HttpMessageConverter，只需要将自己的组件注册容器中（@Bean,@Component）==

- 自动注册 `MessageCodesResolver`；定义错误代码生成规则

- 支持静态资源、webjars；

- 静态 `index.html` 首页访问；

- 自定义 `Favicon` 支持；

- 自动使用 `ConfigurableWebBindingInitializer` bean；

  - ==我们可以配置一个ConfigurableWebBindingInitializer来替换默认的；（添加到容器）==

### 2、扩展SpringMVC配置

自己添加一个`WebMvcConfigurer` 类，标注`Configuration` 注解，不要使用`@EnableWebMvc`注解。

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/chan").setViewName("success");
    }
}
```

原理：

（1）`WebMvcAutoConfiguration`是SpringMVC的自动配置类

（2）`WebMvcAutoConfiguration`中配置`WebMvcAutoConfigurationAdapter`时，会`@Import(EnableWebMvcConfiguration.class)`

```java
@Configuration(proxyBeanMethods = false)
@Import(EnableWebMvcConfiguration.class)
@EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class })
@Order(0)
public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
}
```

（3）`EnableWebMvcConfiguration`继承`DelegatingWebMvcConfiguration`

```java
public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {

}
```

（4）`DelegatingWebMvcConfiguration`中注入了`WebMvcConfigurer`，包括我们扩展的配置类。

```java
@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();


	@Autowired(required = false)
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addWebMvcConfigurers(configurers);
		}
	}
}
```



### 3、全面接管SpringMVC

在配置类中添加`@EnableWebMvc`注解。

```java
@EnableWebMvc
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/chan").setViewName("success");
    }
}
```

原理：

（1）@EnableWebMvc导入DelegatingWebMvcConfiguration组件

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

（2）DelegatingWebMvcConfiguration是一个WebMvcConfigurationSupport

```java
//WebMvcConfigurationSupport定义了SpringMVC的基本配置
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    
}
```

（3）WebMvcAutoConfiguration在容器中没有WebMvcConfigurationSupport组件的时候，自动配置才生效

```java
//容器中没有WebMvcConfigurationSupport组件的时候，自动配置才生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
}
```

（4）总结：@EnableWebMvc会导入WebMvcConfigurationSupport，容器中有WebMvcConfigurationSupport时，自动配置不会生效。

## 5、如何修改SpringBoot的默认配置

- SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（`@Bean`、`@Component`）如果有就用用户配置的，如果没有，才自动配置；如果有些组件可以有多个（ViewResolver）将用户配置的和自己默认的组合起来；
- 在SpringBoot中会有非常多的`xxxConfigurer`帮助我们进行扩展配置
- 在SpringBoot中会有很多的`xxxCustomizer`帮助我们进行定制配置

## 6、RestfulCRUD

### 1、默认访问首页

添加视图控制器，“/”和“/index.html”对应视图“login”

（1）重写`addViewControllers`方法，添加视图控制；

（2）向容器中添加一个`WebMvcConfigurer`bean，返回一个添加视图控制的内部类。

```
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //跳转首页登录
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/index.html").setViewName("login");
    }
    
    @Bean
    public WebMvcConfigurer indexWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
            }
        };
    }
}
```



### 2、国际化

（1）编写国际化文件

![](https://image.5460cc.com/springboot/web-i18n-properties.png)

（2）配置国际化文件的beanname

```yaml
spring:
  messages:
    basename: i18n.login
```

原理：

```java
public class MessageSourceAutoConfiguration {

    //spring.message配置注入到MessageSourceProperties中
    //MessageSourceProperties中basename默认值是"messages"
    //默认读取类路径下的message.properties文件
	@Bean
	@ConfigurationProperties(prefix = "spring.messages")
	public MessageSourceProperties messageSourceProperties() {
		return new MessageSourceProperties();
	}
    
    @Bean
	public MessageSource messageSource(MessageSourceProperties properties) {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        //从配置文件中读取配置文件的基础名（去掉国家代码的）
		if (StringUtils.hasText(properties.getBasename())) {
			messageSource.setBasenames(StringUtils
					.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
		}
    }
}
```

（3）模板页面获取国际化的值

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<form class="form-signin" action="dashboard.html">
    <h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign in</h1>
    <label class="sr-only" th:text="#{login.username}">Username</label>
    <input type="text" class="form-control" th:placeholder="#{login.username}" placeholder="Username" required=""
           autofocus="">
    <label class="sr-only" th:text="#{login.password}">Password</label>
    <input type="password" class="form-control" th:placeholder="#{login.password}" placeholder="Password" required="">
    <div class="checkbox mb-3">
        <label>
            <input type="checkbox" value="remember-me"> [[#{login.rememberme}]]
        </label>
    </div>
    <button class="btn btn-lg btn-primary btn-block" th:text="#{login.btn}" type="submit">Sign in</button>
</form>
</body>
</html>
```



