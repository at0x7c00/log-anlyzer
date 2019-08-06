package me.huqiao.anlyzer.example;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.huqiao.loganlyzer.condition.Conditions;
import me.huqiao.loganlyzer.main.AttPreProcessor;
import me.huqiao.loganlyzer.main.FileScaner;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.util.DateUtil;

import org.junit.Test;

/**
 * 示例程序展示了如何统计access_log文件，示例中的日志文件由Tomcat产生，在/conf/server.xml中配置的日志格式为：<br/>
 * <code>%h %t %s %T %b %{__current_username}r %r</code><br/>
 * 内容大致如下：
 * <pre>
IP           时间                                                                             状态    响应时间   长度         用户名           method  路径                           
101.41.10.45 [05/Aug/2019:13:07:52 +0800] 200 0.047 39210 user001 GET /exmaple/path.do HTTP/1.1
101.41.10.45 [05/Aug/2019:13:07:52 +0800] 200 0.015 13229 - GET /exmaple/path.do HTTP/1.1
101.41.10.45 [05/Aug/2019:13:07:52 +0800] 200 0.015 2207 user002 GET /exmaple/path.do HTTP/1.1
101.41.10.45 [05/Aug/2019:13:07:53 +0800] 200 0.016 3837 user003 GET /exmaple/path.do HTTP/1.1
101.41.10.45 [05/Aug/2019:13:07:53 +0800] 200 0.016 7979 user002 GET /exmaple/path.do HTTP/1.1
...
</pre>
 */
public class Examples {
	
	static AttPreProcessor byMinProcessor = new AttPreProcessor() {
		@Override
		public String process(String att) {
			att = att.substring(1);
			att = replaceMonth(att);
			Date date = DateUtil.parse(att);
			if(date==null){
				return "";
			}
			return DateUtil.format(date, "yyyy-MM-dd HH:mm");
		}
	};
	
	static AttPreProcessor byHourProcessor = new AttPreProcessor() {
		@Override
		public String process(String att) {
			att = att.substring(1);
			att = replaceMonth(att);
			Date date = DateUtil.parse(att);
			if(date==null){
				return "";
			}
			return DateUtil.format(date, "yyyy-MM-dd HH");
		}
	};
	
	static AttPreProcessor bySecProcessor = new AttPreProcessor() {
		@Override
		public String process(String att) {
			att = att.substring(1);
			att = replaceMonth(att);
			Date date = DateUtil.parse(att);
			if(date==null){
				return "";
			}
			String str = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
			return str.substring(0, str.length() - 1) + "0";
		}
	};
	
	/**
	 * 获取不带参数的请求地址
	 */
	static AttPreProcessor urlProcessor = new AttPreProcessor() {
		@Override
		public String process(String att) {
			if(att.contains(".do")){
				String res = att.substring(0, att.indexOf(".do")+3);
				return res;
			}
			return att;
		}
	};
	
	static public String replaceMonth(String str){
		if(str==null)return str;
		str = str.replaceAll("Jul", "01");
		str = str.replaceAll("Feb", "02");
		str = str.replaceAll("Mar", "03");
		str = str.replaceAll("Apr", "04");
		str = str.replaceAll("May", "05");
		str = str.replaceAll("Jun", "06");
		str = str.replaceAll("Jul", "07");
		str = str.replaceAll("Aug", "08");
		str = str.replaceAll("Sep", "09");
		str = str.replaceAll("Otc", "10");
		str = str.replaceAll("Nov", "11");
		str = str.replaceAll("Dec", "12");
		return str;
	}
	
	static String file = "D:\\********\\localhost_access_log.2019-08-05.txt";
	
	/**
	 * 某个地址的请求量
	 * @throws Exception
	 */
	@Test
	public void example1()throws Exception{
		System.out.println("****/user/list.do请求量*****");
		
		FileScaner scaner9 = new FileScaner(file);
		scaner9.select("1,8,count(1)")
				.processor(8, urlProcessor)
				.processor(1, byHourProcessor)
				.where(Conditions.contains("8", "/user/list.do"))
				.groupBy("1")
				.list();
		scaner9.print();
	}
	
	/**
	 * 最慢响应时间TOP50
	 * @throws Exception
	 */
	@Test
	public void example2()throws Exception{
		FileScaner scaner = new FileScaner(file);
		System.out.println("最慢响应时间TOP50:");
		scaner.select("0,1,3,4,5,7,8")
			  .processor(1, byMinProcessor)
			  //.where(Conditions.ge("4", "2"))
			  .orderBy(OrderBy.desc(3))
			  .limit(50)
			  .list();
		scaner.print();
		
		System.out.println("****总体平均响应时长*****");
		
		FileScaner scaner2 = new FileScaner(
				file);
		scaner2.select("1,avg(4)")
			  .processor(1, byHourProcessor)
			  .where(Conditions.and(Conditions.contains("8", ".do"),
					  Conditions.ne("4", "0"),
					  Conditions.eq("3", "200")))
			  //.orderBy(OrderBy.desc(3))
			  .groupBy("1")
			  //.limit(100)
			  .list();
		scaner2.print();
	
	}
	
	/**
	 * 每分钟平均响应时长<br/>
	 * @throws Exception
	 */
	@Test
	public void example3()throws Exception{
		System.out.println("****每分钟平均响应时长*****");
		
		FileScaner scaner3 = new FileScaner(
				file);
		scaner3.select("1,avg(4)")
			  .processor(1, byMinProcessor)
			  .where(Conditions.contains("8", ".do"))
			  .groupBy("1")
			  .orderBy(OrderBy.desc(1))
			  .limit(10)
			  .list();
		scaner3.print();
	
	}
	
	
	/**
	 * 每分钟请求并发量
	 * @throws Exception
	 */
	@Test
	public void example4()throws Exception{
		System.out.println("****每分钟.do请求并发量*****");
		
		FileScaner scaner4 = new FileScaner(
				file);
		scaner4.select("1,count(1)")
			  .processor(1, byMinProcessor)
			  .where(Conditions.contains("8", ".do"))
			  .groupBy("1")
			  .orderBy(OrderBy.desc(1))
			  .limit(10)
			  .list();
		scaner4.print();
	
	}

	/**
	 * 用户请求数统计 & 总体活跃用户数
	 * @throws Exception
	 */
	@Test
	public void example5()throws Exception{
		System.out.println("****用户请求数*****");
		
		FileScaner scaner5 = new FileScaner(
				file);
		List<List<Object>> list = scaner5.select("6,count(1)")
			  .processor(1, byMinProcessor)
			  .where(Conditions.contains("8", ".do"))
			  .groupBy("6")
			  .orderBy(OrderBy.desc(1))
			  //.limit(100)
			  .list();
		System.out.println("总活动用户数："+list.size());
		int count = 0;
		for(List<Object> item : list){
			System.out.println(item);
			if(count++>20)break;
		}
	}
	
	/**
	 * 访问次数最多的地址
	 * @throws Exception
	 */
	@Test
	public void example6()throws Exception{
		System.out.println("****访问次数最多的地址****");
		FileScaner scaner7 = new FileScaner(
				file);
		scaner7.select("8,count(1)")
			  .processor(8, urlProcessor)
			  .where(Conditions.and(Conditions.contains("8", ".do"),
					  Conditions.eq("3", "200")))
			  .groupBy("8")
			  .orderBy(OrderBy.desc(1))
			  .limit(50)
			  .list();
		scaner7.print();
	}
	
	/**
	 * 平均响应时间最慢TOP50
	 * @throws Exception
	 */
	@Test
	public void example7()throws Exception{
		System.out.println("****平均访问时间最慢的地址****");
		FileScaner scaner8 = new FileScaner(
				file);
		scaner8.select("8,avg(4)")
			  .processor(8, urlProcessor)
			  .where(Conditions.and(Conditions.contains("8", ".do"),
					  Conditions.eq("3", "200")))
			  .groupBy("8")
			  .orderBy(OrderBy.desc(1))
			  .limit(50)
			  .list();
		scaner8.print();
	}
	
	
	/**
	 * 并发请求量
	 * <pre>
		2019-08-05 10:13:00 :   8  ||||||||
		2019-08-05 10:13:10 :  11  |||||||||||
		2019-08-05 10:13:20 :   7  |||||||
		2019-08-05 10:13:30 :   9  |||||||||
		2019-08-05 10:13:40 :  10  ||||||||||
		2019-08-05 10:13:50 :  11  |||||||||||
		2019-08-05 10:14:00 :  10  ||||||||||
		2019-08-05 10:14:10 :   7  |||||||
		2019-08-05 10:14:20 :  12  ||||||||||||
		2019-08-05 10:14:30 :  12  ||||||||||||
		2019-08-05 10:14:40 :  17  |||||||||||||||||
		2019-08-05 10:14:50 :  12  ||||||||||||
		...
	 * </pre>
	 * @throws Exception
	 */
	@Test
	public void example8()throws Exception{
		System.out.println("*****每秒钟并发用户数*******");
		FileScaner scaner6 = new FileScaner(
				file);
		List<List<Object>> list6 = scaner6.select("1,6")
			  .processor(1, bySecProcessor)
			  .where(Conditions.contains("8", ".do"))
			  .list();
		
		Map<String,Map<String,Integer>> map = new LinkedHashMap<String,Map<String,Integer>>();
		for(List<Object> item : list6){
			String time = (String)item.get(0);
			String person = (String)item.get(1);
			
			Map<String,Integer> personCount = map.get(time);
			if(personCount==null){
				personCount = new HashMap<String,Integer>();
			}
			Integer c = personCount.get(person);
			if(c==null){
				personCount.put(person, 1);
			}
			map.put(time, personCount);
		}
		
		for(Map.Entry<String, Map<String,Integer>> entry : map.entrySet()){
			int size = entry.getValue().size();
			if(size<=1) continue;
			System.out.print(entry.getKey() + " :  " + (size<10 ? " " + size : size + "") + "  ");
			for(int i = 0;i<size ;i++){
				System.out.print("|");
			}
			System.out.println();
		}
	}
	
}
