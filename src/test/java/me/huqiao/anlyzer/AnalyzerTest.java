package me.huqiao.anlyzer;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import me.huqiao.loganlyzer.condition.Conditions;
import me.huqiao.loganlyzer.main.AttPreProcessor;
import me.huqiao.loganlyzer.main.FileScaner;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.util.DateUtil;

import org.junit.Before;
import org.junit.Test;

public class AnalyzerTest {

	static Logger log = Logger.getLogger("AnalyzerTest");

	private FileScaner scaner = null;
	private AttPreProcessor processor = new AttPreProcessor() {
		@Override
		public String process(String att) {
			att = att.substring(1);
			att = att.replaceAll("Mar", "03");
			Date date = DateUtil.parse(att);
			if(date==null){
				return "";
			}
			return DateUtil.format(date, "yyyy-MM-dd HH:mm");
		}
	};

	@Before
	public void init(){
		scaner = new FileScaner(
				"D:\\项目日志分析\\dongxu\\localhost_access_log.2019-03-21.txt");
	}


	@Test
	public void testWhere()throws Exception{
		scaner.select("count(1)")
				.where(Conditions.and(Conditions.contains("6", "service/ws/WsService?"),
						Conditions.eq("8", "200")))
				.list();
		scaner.print();
	}

	@Test
	public void testMoreCondition()throws Exception{
		scaner.select("0,3,6,8")
				.where(Conditions.or(Conditions.eq("8", "404"),Conditions.eq("8", "302")))
				.list();
		scaner.print();
	}

	@Test
	public void testCount()throws Exception{
		scaner.select("8,count(1)")
				.where(Conditions.contains("8", "404"))
				.list();
		scaner.print();
	}

	@Test
	public void testAttPreProcessor()throws Exception{

		scaner.select("0,3,6")
				.processor(3, processor)
				.where(Conditions.contains("8", "404"))
				.list();
		scaner.print();
	}


	@Test
	public void testGroupBy()throws Exception{

		scaner.select("3,count(1)")
				.processor(3, processor)
				.groupBy("0")
				.list();
		scaner.print();
	}


	@Test
	public void testOrderBy()throws Exception{
		scaner.select("3,count(1)")
				.processor(3, processor)
				.groupBy("0")
				.orderBy(OrderBy.desc(1))
				.list();
		scaner.print();
	}

	@Test
	public void testLimit()throws Exception{
		scaner.select("3,count(1)")
				.processor(3, processor)
				.groupBy("0")
				.orderBy(OrderBy.desc(1))
				.limit(10)
				.list();
		scaner.print();
	}

}
