# 日志文件分析工具 #
该工具可实现对日志文件的分析，可以方便地对日志文件进行类似于数据库查询统计一样的操作。主要功能包括：
- 条件筛选：包含、不包含、相等、不等、大于、大于等于、小于、小于等于、正则匹配
- 不同条件的“或”与“并”关系可灵活控制
- 函数：可以通过自定义`AttPreProcessor`对字段进行预处理，以此来实现“函数”能力
- 分组求值：最大值、最小值、平均值、求和
- 支持对统计结果的排序
- 使用Limit控制输出数量
- 可同时处理多个文件，支持文件名通配符
- [New]支持查询语句，LQL（Log query language）


## 按空格split ##
分析器认为每一行由空格分隔的每一段为一个列，例如：
```bash
120.244.106.255 - - 15/04/2019:01:38:49 +0800 "GET / HTTP/1.1" 302 -
120.244.106.255 - - 15/04/2019:01:38:49 +0800 "GET /auth.do HTTP/1.1" 302 -
120.244.106.255 - - 15/04/2019:01:38:49 +0800 "GET /loginUI.do HTTP/1.1" 302 -
120.244.106.255 - - 15/04/2019:01:39:04 +0800 "GET /loginUI.do HTTP/1.1" 200 13881
120.244.106.255 - - 15/04/2019:01:39:04 +0800 "GET /admin/filee/1ec45f5bfbd718a0e5b93a9 HTTP/1.1" 200 39735
120.244.106.255 - - 15/04/2019:01:39:05 +0800 "GET /img/favicon/favicon.ico HTTP/1.1" 404 1040
120.244.106.255 - - 15/04/2019:01:39:33 +0800 "GET /admin/filee/5f5bfbd718a0e5b93a9 HTTP/1.1" 200 39735
222.137.114.157 - - 15/04/2019:04:27:01 +0800 "GET / HTTP/1.1" 302 -
222.137.114.157 - - 15/04/2019:04:27:02 +0800 "GET / HTTP/1.1" 302 -
222.137.114.157 - - 15/04/2019:04:27:04 +0800 "GET / HTTP/1.1" 302 -
222.137.114.157 - - 15/04/2019:04:27:07 +0800 "GET / HTTP/1.1" 302 -
222.137.114.157 - - 15/04/2019:04:27:14 +0800 "GET / HTTP/1.1" 302 -
205.205.150.10 - - 15/04/2019:07:19:15 +0800 "GET / HTTP/1.1" 302 -
205.205.150.10 - - 15/04/2019:07:19:15 +0800 "GET /auth.do HTTP/1.1" 302 -
205.205.150.10 - - 15/04/2019:07:19:15 +0800 "GET /loginUI.do HTTP/1.1" 302 -
177.93.97.240 - - 15/04/2019:08:22:23 +0800 "GET / HTTP/1.1" 302 -
...
```
比如第一行，从0开始，各列的值为：
- 0：120.244.106.255
- 1：-
- 2：-
- 3：15/04/2019:01:38:49
- 4：+0800
- 5："GET
- 6：HTTP/1.1"
- 7：302
- 8：-

使用分析器进行列选的时候，给出的是行的index。如果某一行按空格split之后，长度不够给定的index，那么这一行就会被忽略。


## 初始化 ##
主要工具类`FileScaner`,可以同时指定由逗号分隔的多个路径，并且支持通配符：
```java
scaner = new FileScaner("/usr/local/tomcat/logs/localhost_access_log*.txt,/usr/local/tomcat2/logs/localhost_access_log*.txt");
```
## 字符匹配 ##
用where查询状态码为404的访问记录：
```java
	@Test
	public void testWhere()throws Exception{
		scaner.select("0,3,6,8")    //选择的列
		.where(Conditions.contains("8", "404")) //指定列的筛选条件
		.list();
		scaner.print();
	}
```
## 组合条件 ##
多个查询条件的情况，例如查询状态码为404或者500的访问记录：
```java
	@Test
	public void testMoreCondition()throws Exception{
		scaner.select("0,3,6,8")
		.where(Conditions.or(Conditions.eq("8", "404"),Conditions.eq("8", "500")))
		.list();
		scaner.print();
	}
```
## 计数统计 ##
统计404总共出现的次数：
```java
	@Test
	public void testWhere()throws Exception{
		scaner.select("8,count(1)")
		.where(Conditions.contains("8", "404"))
		.list();
		scaner.print();
	}
```
除了count，还支持sum、max、min和avg。这里不再举例。
> count中的1其实是没意义的，既不表示列的index，并且写成`count(0)`并不会少计数。其他统计函数则是有意义的，表示的是**列的索引**。

## 函数：自定义AttPreProcessor预处理行为 ##
例如，我们想将日志文件中的时间，去掉秒，只精确到分钟，只需要实现AttPreProcessor接口即可：
```java
	@Test
	public void testAttPreProcessor()throws Exception{
		AttPreProcessor processor = new AttPreProcessor() {
			@Override
			public String process(String att) {
				Date date = DateUtil.parse(att);
				if(date==null){
					return "";
				}
				return DateUtil.format(date, "yyyy-MM-dd HH:mm");
			}
		};
		scaner.select("0,3,6")
		.processor(3, processor)
		.where(Conditions.contains("8", "404"))
		.list();
		scaner.print();
	}
```
结果类似如下，可以看到秒已经被去掉了：
```bash
[120.244.106.255, 2019-03-15 01:39, /img/favicon/favicon.ico]
[216.245.197.254, 2019-03-15 08:32, /robots.txt]
[223.72.82.114, 2019-03-15 13:52, /img/favicon/favicon.ico]
[223.72.82.114, 2019-03-15 15:03, /img/favicon/favicon.ico]
[5.8.55.40, 2019-03-15 16:32, /index.php?x=HelloThinkPHP]
...
```
## 分组 ##
使用groupBy来进行分组统计。

**注意：** groupBy中的索引值是select中的列的的索引值。比如下面的代码中，select的值是“3,count(1)"，一个选择了2列，需要以第1列排序，所以需要`groupBy("0")`。
```java
	@Test
	public void testGroupBy()throws Exception{
		AttPreProcessor processor = new AttPreProcessor() {
			@Override
			public String process(String att) {
				Date date = DateUtil.parse(att);
				if(date==null){
					return "";
				}
				return DateUtil.format(date, "yyyy-MM-dd HH:mm");
			}
		};
		scaner.select("3,count(1)")
		.processor(3, processor)
		.groupBy("0")
		.list();
		scaner.print();
	}
```
输出效果：
```bash
[2019-03-15 01:38, 14.0]
[2019-03-15 04:27, 10.0]
[2019-03-15 07:19, 6.0]
[2019-03-15 08:22, 2.0]
[2019-03-15 08:32, 2.0]
[2019-03-15 09:28, 34342.0]
[2019-03-15 10:34, 4.0]
[2019-03-15 12:42, 2.0]
...
[2019-03-15 17:16, 20.0]
[2019-03-18 03:29, 1.0]
[2019-03-18 07:45, 1.0]
[2019-03-18 07:55, 1.0]
[2019-03-18 10:25, 47.0]
[2019-03-18 10:25, 2.0]
[2019-03-18 11:04, 1.0]
[2019-03-18 11:34, 376.0]
[2019-03-18 11:44, 6.0]
[2019-03-18 12:47, 2.0]
...
[2019-03-18 17:07, 41503.0]
[2019-03-18 19:09, 1.0]
[2019-03-18 21:08, 2.0]
[2019-03-20 13:42, 1.0]
[2019-03-20 13:42, 1.0]
```

## 排序 ##
orderBy中可以有多个排序，排序的index和groupBy同理，是只选择结果的列index。下面的代码按每分钟访问量降序排列：
```java
	@Test
	public void testOrderBy()throws Exception{
		AttPreProcessor processor = new AttPreProcessor() {
			@Override
			public String process(String att) {
				Date date = DateUtil.parse(att);
				if(date==null){
					return "";
				}
				return DateUtil.format(date, "yyyy-MM-dd HH:mm");
			}
		};
		scaner.select("3,count(1)")
		.processor(3, processor)
		.groupBy("0")
		.orderBy(OrderBy.desc(1))
		.list();
		scaner.print();
	}
```
排序结果类似：
```bash
[2019-03-18 17:07, 41503.0]
[2019-03-15 09:28, 34342.0]
[2019-03-15 13:52, 582.0]
[2019-03-18 11:34, 376.0]
[2019-03-18 13:48, 267.0]
[2019-03-18 10:25, 47.0]
[2019-03-18 14:16, 33.0]
...
[2019-03-18 14:29, 9.0]
[2019-03-18 14:29, 8.0]
[2019-03-15 07:19, 6.0]
[2019-03-18 11:44, 6.0]
[2019-03-15 10:34, 4.0]
[2019-03-15 14:00, 3.0]
...
```
## limit：限制输出数量 ##
如果只关心访问量最高的10个记录，可以这样写：
```java
	@Test
	public void testLimit()throws Exception{
		AttPreProcessor processor = new AttPreProcessor() {
			@Override
			public String process(String att) {
				Date date = DateUtil.parse(att);
				if(date==null){
					return "";
				}
				return DateUtil.format(date, "yyyy-MM-dd HH:mm");
			}
		};
		scaner.select("3,count(1)")
		.processor(3, processor)
		.groupBy("0")
		.orderBy(OrderBy.desc(1))
		.limit(10)
		.list();
		scaner.print();
	}
```
输出结果类似：
```bash
[2019-03-18 17:07, 41503.0]
[2019-03-15 09:28, 34342.0]
[2019-03-15 13:52, 582.0]
[2019-03-18 11:34, 376.0]
[2019-03-18 13:48, 267.0]
[2019-03-18 10:25, 47.0]
[2019-03-18 14:16, 33.0]
[2019-03-15 17:16, 20.0]
[2019-03-15 01:38, 14.0]
[2019-03-15 16:32, 14.0]
```

## Log Query Language(LQL),日志查询语句 ##
例如：
```bash
select 0,2,3 
from 'localhost_access_log.txt,localhost_access_log02.txt'
where 0 eq 'x' and (2 eq 'x'  or 3 eq 'x') 
order by 0 desc, 1 asc 
group by 0 
limit 10
```

实现原理：
* [TextNodeParser](./blob/master/src/main/java/me/huqiao/loganlyzer/querylanguage/TextNodeParser.java)复制将字符串分解为word组，重点是支持引号（单引号或双引号），引号内的字符会被当成一个word
* [LogQueryLanguageParserStateMachine](./blob/master/src/main/java/me/huqiao/loganlyzer/querylanguage/LogQueryLanguageParserStateMachine.java)状态机中包含一些节点，这些节点之间的关系已经在[StatusNodeFactory](./blob/master/src/main/java/me/huqiao/loganlyzer/querylanguage/statusnode/StatusNodeFactory.java)中预定义好了
* 遍历TextNodeParser生成的word组，feed状态机，状态机会自动切换状态节点。[每个节点](./tree/master/src/main/java/me/huqiao/loganlyzer/querylanguage/statusnode)也会根据自己身的情况来解析当前的word
* [AbstractNode](./blob/master/src/main/java/me/huqiao/loganlyzer/querylanguage/statusnode/StatusNodeFactory.java)是所有节点的基类，实现了大部分通用功能，包括判断是否是关键字、以及一些通用的断言

详见[源码](./tree/master/src/main/java/me/huqiao/loganlyzer/querylanguage)。

支持的比较方式
* 大于等于：<code>>=</code>或者<code>ge</code>
* 等于：<code>=</code>或者<code>eq</code>
* 不等于：<code>!=</code>或者<code>ne</code>
* 小于等于：<code><=</code>或者<code>le</code>
* 小于：<code><</code>或者<code>lt</code>
* 区间：<code>between</code>
* 包含：<code>contains</code>或者<code>like</code>
* 不包含：<code>notcontains</code>
* startwith:<code>startwith</code>
* notstartwith:<code>notcontains</code>
* 正则表达式：<code>regex</code>
  
> 暂不支持<code>not</code>!
         

         
        
        
        