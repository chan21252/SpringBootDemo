

# SpringBoot



## 三、日志

### 1、日志框架

| 日志门面 | 日志实现               |
| -------- | ---------------------- |
| sl4j     | logback，log4j，log4j2 |

### 2、SL4J使用

#### 1、在系统中使用sl4j

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



#### 2、日志统一问题

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

### 3、SpringBoot日志关系

**SpringBoot使用的是sl4j+logback记录日志，引入其他库时，只需要把原有日志框架擦除掉就可以了。**

![springboot-logging](https://image.5460cc.com/springboot/springboot-logging.png)



### 4、日志使用

> SpringBoot日志相关文档：https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-logging

#### 1、默认配置

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

#### 2、指定配置

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

### 5、切换日志框架

比如切换日志框架到log4j1.2，参考sl4j提供的统一日志框架方法

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



