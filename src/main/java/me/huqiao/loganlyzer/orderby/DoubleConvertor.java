package me.huqiao.loganlyzer.orderby;

public class DoubleConvertor implements Convertor {

	@Override
	public Comparable doConvert(String str) {
		if(str==null) return str;
		return Double.parseDouble(str);
	}

}
