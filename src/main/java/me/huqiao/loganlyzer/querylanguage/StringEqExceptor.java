package me.huqiao.loganlyzer.querylanguage;

import me.huqiao.loganlyzer.enumtype.CompareType;

public class StringEqExceptor implements Exceptor {

	String exceptValue;
	
	public StringEqExceptor(String exceptValue) {
		this.exceptValue = exceptValue;
	}

	@Override
	public CompareType getCompareType() {
		return CompareType.eq;
	}

	@Override
	public String getExceptValue() {
		return exceptValue;
	}
	
}
