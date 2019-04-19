package me.huqiao.loganlyzer.querylanguage;

import java.util.ArrayList;
import java.util.List;

public class QueryLanguageWalker {
	
	private String status;
	private List<Exceptor> exceptors = new ArrayList<Exceptor>();
	public QueryLanguageWalker(){
		exceptors.add(new StringEqExceptor("select"));
	}

}
