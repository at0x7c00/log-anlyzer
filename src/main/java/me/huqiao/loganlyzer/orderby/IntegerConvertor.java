package me.huqiao.loganlyzer.orderby;

public class IntegerConvertor implements Convertor {

	@Override
	public Comparable doConvert(String str) {
		if(str==null) return str;
		return Integer.parseInt(str);
	}

}
