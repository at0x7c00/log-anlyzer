package me.huqiao.loganlyzer.condition.checker;

import java.util.Date;

import me.huqiao.loganlyzer.util.DateUtil;

public class EqChecker implements ConditionChecker {

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(value == null){
			return false;
		}
		//字符
		if(pattern instanceof String){
			return value != null && value.equals(pattern);
		}
		//数字
		if(pattern instanceof Number){
			Double dValue = Double.parseDouble(value);
			return dValue.equals(pattern);
		}
		//日期
		if(pattern instanceof Date){
			Date date = DateUtil.parse(value);
			return date.equals(pattern);
		}
		return false;
	}

}
