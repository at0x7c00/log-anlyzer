package me.huqiao.loganlyzer.condition.checker;

import java.util.Date;

import me.huqiao.loganlyzer.util.DateUtil;

public class EqChecker implements ConditionChecker {
	
	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(value == null){
			return false;
		}
		//×Ö·û
		if(pattern instanceof String){
			return value != null && value.equals(pattern);
		}
		//Êý×Ö
		if(pattern instanceof Number){
			Double dValue = Double.parseDouble(value);
			return dValue.equals(pattern);
		}
		//ÈÕÆÚ
		if(pattern instanceof Date){
			Date date = DateUtil.parse(value);
			return date.equals(pattern);
		}
		return false;
	}
	
}
