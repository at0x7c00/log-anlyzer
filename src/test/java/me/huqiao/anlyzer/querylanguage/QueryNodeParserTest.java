package me.huqiao.anlyzer.querylanguage;

import me.huqiao.loganlyzer.querylanguage.TextNodeParser;
import me.huqiao.loganlyzer.querylanguage.exception.InvalidQueryStringException;

import org.junit.Test;

public class QueryNodeParserTest {

	@Test
	public void test() throws InvalidQueryStringException{
		String str = "select * from sys_user where username like '\"122%'        and org like \"fdsf' xxxx ' ad\"         and (email>='zoozle@qq.com' or age!='12') ";

		TextNodeParser p = new TextNodeParser(str);
		p.doParse();
		for(String s : p.getTextList()){
			System.out.println("["+s+"]");
		}
	}

}
