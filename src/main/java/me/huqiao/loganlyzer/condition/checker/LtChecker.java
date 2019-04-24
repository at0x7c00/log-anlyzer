package me.huqiao.loganlyzer.condition.checker;

import java.util.Date;

import me.huqiao.loganlyzer.util.DateUtil;

public class LtChecker implements ConditionChecker {

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(value == null){
			return false;
		}
		//字符
		if(pattern instanceof String){
			return value.compareTo((String)pattern) < 0;
		}
		//数字
		if(pattern instanceof Number){
			Double dValue = Double.parseDouble(value);
			return dValue.compareTo(((Number)pattern).doubleValue()) < 0;
		}
		//日期
		if(pattern instanceof Date){
			Date date = DateUtil.parse(value);
			return date.compareTo((Date)pattern) < 0;
		}
		return false;
	}

}
