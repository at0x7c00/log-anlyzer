package me.huqiao.loganlyzer.querylanguage;


public class QueryStringParser {

	/**
	 * select 0,2,3 where 0 eq 'x' and (2 eq 'x'  or 3 eq 'x') order by 0 group by 0 limit 10
	 * @param queryString
	 * @return
	 */
	public static QueryStringParseResult parse(String queryString){
		String str = "select 0,2,3 where 0 eq 'x' and (2 eq 'x'  or 3 eq 'x') order by 0 group by 0 limit 10";
		char[] charArray = str.toCharArray();
		
		return null;
	}
	
}
