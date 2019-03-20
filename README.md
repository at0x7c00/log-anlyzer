# 日志文件分析 #
该工具可实现对日志文件的分析，包括条件
- 筛选：范围、包含、相等、不等、大于、大于等于、小于、小于等于、正则匹配
- 分组求值：最大值，最小值，平均值，求和
- Limit控制输出数量
- 排序

## 使用示例 ##
访问日志示例：
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
下面的代码实现对访问量的统计，输出每分钟访问总数,并且按访问数排序：

```java
	@Test
	public void testCount()throws Exception{
		FileScaner scaner = new FileScaner(
				"path\\to\\access_log_file\\localhost_access_log.txt");
		List<List<Object>> list = 
				scaner.select("3,count(1)")
				 .groupBy("3")
				 .processor(3, new AttPreProcessor() {
					@Override
					public String process(String att) {
						Date date = DateUtil.parse(att);
						return DateUtil.format(date, "yyyy-MM-dd HH:mm");
					}
				  })
				 .orderBy(OrderBy.desc(1))
				 .limit(10)
				.list();
		for(List<Object> l : list){
			System.out.println(l);
		}
	}

```