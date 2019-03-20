package me.huqiao.anlyzer;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import me.huqiao.loganlyzer.condition.Conditions;
import me.huqiao.loganlyzer.main.AttPreProcessor;
import me.huqiao.loganlyzer.main.FileScaner;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.util.DateUtil;

import org.junit.Test;

public class AnalyzerTest {
	
	static Logger log = Logger.getLogger("AnalyzerTest");
	
	@Test
	public void testCount()throws Exception{
		FileScaner scaner = new FileScaner(
				"D:\\****\\localhost_access_*****.txt");
		List<List<Object>> list =scaner.select("3,count(1)")
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

}
