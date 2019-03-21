# 日志文件分析工具 #
该工具可实现对日志文件的分析，主要功能包括
- 条件筛选：包含、不包含、相等、不等、大于、大于等于、小于、小于等于、正则匹配
- 不同条件的“或”与“并”关系可灵活控制
- 函数：可以通过自定义`AttPreProcessor`对字段进行预处理，以此来实现“函数”能力
- 分组求值：最大值、最小值、平均值、求和
- 支持对统计结果的排序
- 使用Limit控制输出数量
- 可同时处理多个文件，支持文件名通配符


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
scaner = new FileScaner(
				"/usr/local/tomcat/logs/localhost_access_log*.txt,/usr/local/tomcat2/logs/localhost_access_log*.txt");
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

