package me.huqiao.anlyzer.querylanguage;

import me.huqiao.loganlyzer.querylanguage.TextNodeParser;
import me.huqiao.loganlyzer.querylanguage.WordList;
import me.huqiao.loganlyzer.querylanguage.exception.InvalidLQLException;

import org.junit.Test;

public class QueryNodeParserTest {

	@Test
	public void test() throws InvalidLQLException {
		String str = "select * from sys_user where username like '\"122%'    \t    and org like \"fdsf' xxxx ' ad\"         and (email>='zoozle@qq.com' or age!='12') order by a desc,b desc,c ";

		TextNodeParser p = new TextNodeParser(str);
		p.doParse();
		WordList wl = p.getWordList();
		while(wl.hasNext()){
			String s = wl.next();
			System.out.println("["+s+"]");
		}
	}

}
