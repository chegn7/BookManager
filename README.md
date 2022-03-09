# 跟做牛客 图书管理系统

# 1. 搭建框架

[https://start.spring.io/](https://start.spring.io/)

![image-20220308215858510](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203082158611.png)

添加依赖

在pom.xml中添加

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
```

## 模块简介

### Freemaker

Freemaker是用来代替JSP的模板渲染工具，用来将Model中的数据渲染到View上。

### Web

Web组件本质上就是SpringMVC，用来控制整个MVC主体流程，部分同学将SpringMVC理解为Web
端的入口也是可以的。

### MyBatis

负责与数据库交互

### Aspect

Spring中重要的AOP组件，用来面向切面编程，多用在日志打印、权限认证等地方。

## 已有文件解释

### 自动生成的文件：

* static（文件夹） 用来放css、font、img、js等静态文件
* templates（文件夹） 存放html文件以及模板文件
* application.properties（文件） Spring Boot的配置文件
* log4j.properties（文件） log4j的配置文件
* mybatis-config.xml（文件） MyBatis的配置文件，一般不做改动
* BookManagerApplication.java（文件） Spring Boot的入口，你搭好项目后可以运行一下试试看

## 自己创建的文件夹

* biz 用来存放比较复杂的逻辑
* configuration 用来放Spring Boot的代码配置
* controllers 控制器都在这里，也可以认为是网页的入口都在这
* dao 跟数据库交互的包，主要是MyBatis在这里编码
* interceptor AOP的代码都在这
* model 各种数据模型，对数据的描述
* service 一般用作对dao层的封装，建议稍复杂的逻辑全部放到biz包，而不是service
* utils 工具包，一般都是静态方法。

这里的核心主要是controllers、service、dao、model这四个包。

### 在model里面还有几个包：

* constants 放项目中的常量类（类里面全部是常量）
* enums 所有的枚举类
* exceptions 自定义的异常

# 2. 完成图书的增删改查

## 建立数据库

需要先建立名为test的database

- book

```sql
USE test;
DROP TABLE
IF
	EXISTS `book`;
CREATE TABLE `book` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR ( 256 ) DEFAULT NULL,
	`author` VARCHAR ( 256 ) DEFAULT NULL,
	`price` VARCHAR ( 256 ) DEFAULT NULL,
	`status` INT ( 11 ) DEFAULT '0',
	PRIMARY KEY ( `id` ),
	UNIQUE KEY `name` ( `name` ) 
) ENGINE = INNODB DEFAULT CHARSET = utf8;
LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` 
	DISABLE KEYS
*/;
INSERT INTO `book` ( `id`, `name`, `author`, `price`, `status` )
VALUES
	( 1, '枪毙任老道', '大英雄王思文', '100￥', 0 ),
	( 2, '论一个演员的自我修养', '斯坦尼斯拉夫斯基', '20.40￥', 0 );
/*!40000 ALTER TABLE `book` 
	ENABLE KEYS
*/;
UNLOCK TABLES;
```

- ticket

```sql
USE test;
DROP TABLE
IF
	EXISTS `ticket`;
CREATE TABLE `ticket` (
	`id` INT ( 11 ) UNSIGNED NOT NULL AUTO_INCREMENT,
	`user_id` INT ( 11 ) DEFAULT NULL,
	`ticket` VARCHAR ( 1024 ) DEFAULT NULL,
	`expired_at` datetime DEFAULT NULL,
	PRIMARY KEY ( `id` ),
	UNIQUE KEY `uid` ( `user_id` ),
UNIQUE KEY `t` ( `ticket` ) 
) ENGINE = INNODB DEFAULT CHARSET = utf8;
```

- user

```sql
USE test;
DROP TABLE
IF
	EXISTS `user`;
CREATE TABLE `user` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR ( 256 ) DEFAULT NULL,
	`email` VARCHAR ( 256 ) DEFAULT '',
	`password` VARCHAR ( 256 ) DEFAULT '',
PRIMARY KEY ( `id` ) 
) ENGINE = INNODB DEFAULT CHARSET = utf8;
```

## Book.java 最底层的图书模型

图书基本属性，id，书名，作者和价格，状态

```java
package com.example.BookManager.model;

public class Book {
    //图书基本属性，id，书名，作者和价格，状态
    private Integer id;
    private String name;
    private String author;
    private String price;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", price='" + price + '\'' +
                ", status=" + status +
                '}';
    }
}
```

## BookDao.java 和数据库交互

采用xml写sql语句

application.properties配置

```properties
# datasource config
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
# mybatis config
mybatis.mapper-locations=classpath:mapper/*Dao.xml
```

修改mybatis-config.xml文件，更改mapper为扫描包

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <mappers>
        <package name="com.example.BookManager.dao"/>
    </mappers>
</configuration>
```

在resource/mapper下创建BookDao.xml文件，初始填充

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.BookManager.dao.BookDao">

</mapper>
```

在xml文件里写sql语句，参考https://mybatis.org/mybatis-3/sqlmap-xml.html

BookDao.java

```java
package com.example.BookManager.dao;


import com.example.BookManager.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BookDao {

    List<Book> selectAll();

    int addBook(Book book);

    Book selectById(int id);

    Book selectByName(String name);
}

```

BookDao.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.BookManager.dao.BookDao">
    <resultMap id="BookResult" type="com.example.BookManager.model.Book">
        <id property="id" column="id"></id>
        <result property="name" column="name"></result>
        <result property="author" column="author"></result>
        <result property="price" column="price"></result>
    </resultMap>

    <insert id="addBook" parameterType="com.example.BookManager.model.Book">
        insert into book (name, author, price)
        values (#{name}, #{author}, #{price})
    </insert>
    <select id="selectById" resultMap="BookResult">
        select name, author, price, status
        from book
        where id = #{id}
    </select>
    <select id="selectByName" resultMap="BookResult">
        select name, author, price, status
        from book
        where name = #{name}
    </select>
</mapper>
```

## BookService.java Proxy代理模式

在` BookService.java `中，首先持有一个` BookDAO `的对象，这个对象由Spring自动帮你注入（@Autowired），
你不用亲自去实例化，Spring已经很聪明的帮你实例化了。你需要的是将` BookDAO `的方法“包装”
一下，供上层的类去调用。

代理（包装）的作用

1. 分层，统一处理一些功能

```java
public List<Book> getAllBooks() {
    try {
        return bookDAO.selectAll();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
        /*- 或者抛出自定义的异常 -*/
    }
}
```

这样，所有的异常都在service层被处理掉了，层调用的时候再也不用关心底层的异常问题。这里只是举一service层的例子，你可以做更多的事情。关于异常需要说一句，千万不要静默的吞掉异常（就是在catch里面什么都不做，后者返回一个假数据），不然排查问题的时候你将无从下手，如果你不知道怎么处理异常又非处理不可的时候，至少先将异常记录在日志中吧！

* 多态、封装、重载

很显然多了一层你可以干更多的事情，而且能将DAO层的方法封装的更优雅一点，以至于上层完全不知道你在跟数据库打交道。而且你可以有更多的操作空间，比如：

``` java
    //在BookDAO.java中有如下两个方法根据不同的条件查询一本书
    Book selectBookById(int id);
    Book selectBookByName(String name);
    
    //在对应的BookService.java中可以这么写
    Book getBook(int id) { return bookDAO.selectBookById(id); }
    Book getBook(String name) { return bookDAO.selectBookByName(name); }
```

你可能会问，好不容易在DAO层用不用的命名将不同的查询方法区分开，而在Service层中又合并成了一个重载的方法名称getBook，那为什么不在DAO层就重载呢？

这是一个很重要的问题，你需要真正理解的是，不同的层关心的东西不一样，DAO层关心的就是跟数据库打交道，这样所有的方法名都应该要尽量的去描述自己的功能；而Service层关心的是功能，就是说，根据Name也好还是根据id也好在上层来看并没有区别，都是给我去取一本书来，我不用管你是根据书的什么属性去取。在面向对象的编程中，慢慢养成面向对象思维才是最重要的，不然就是用着java写着C。

## Test 单元测试

测试service

/src/test/java/com/example/BookManager/service/BookServiceTest.java

```java
package com.example.BookManager.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class BookServiceTest {

    @Resource
    BookService bookService;

    @Test
    void getAllBooks() {
        System.out.println(bookService.getAllBooks());
    }

    @Test
    void getBook() {
        System.out.println(bookService.getBook(1));
    }

    @Test
    void testGetBook() {
    }

    @Test
    void addBook() {
    }
}
```

![image-20220309145638932](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203091456014.png)

## Controller 

Controller并非网页的入口。

我们看看BookController.java

``` java
package com.example.BookManager.controllers;

import com.example.BookManager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String bookList(Model model) {
        System.out.println("bookList");
        loadAllBooksView(model);
        return "book/books";
    }

    private void loadAllBooksView(Model model) {
        Map map = new HashMap<>();
        map.put("books", bookService.getAllBooks());
        model.addAllAttributes(map);
    }
}
```

这是一个简单的controller方法，极其优雅的为我们展现了MVC框架：

* 第一行告诉了web什么样的url才能进入这个方法，
* 方法的主体部分：` loadAllBooksView(model); ` 告诉web如何处理和组装Model
* 最后` return "book/books"; `告诉web返回什么样的View
* 而这整段代码整体就是一个控制器Controller，它控制了怎么进入、怎么处理、怎么返回的所有操作。

####  return "book/books"; 这句，return到哪去了？

在resources/templates包下你应该就能找到book/books.html，你是不是恍然大悟？

#### 最后讲讲application.properties

我们在里面添加了一些内容，数据库的部分很简单，相信大家能看明白，

```properties
spring.freemarker.suffix=.html
```

这一句呢，作用就是让我们能保持习惯，将templates文件夹下的模板文件以.html结尾。
关于application.properties的设置还有很多，同样建议你去查看官方文档。

如果你的代码都敲完啦，那么打开` localhost:8080/index `看看吧！

访问页面，控制台输出了"bookList"，说明经过了这个controller

![image-20220309152752501](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203091527548.png)

**tomcat已经被嵌入到内部了，大家不用操心怎么搭建tomcat。**
