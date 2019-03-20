package me.huqiao.loganlyzer.condition.checker;

import java.util.Date;

import me.huqiao.loganlyzer.util.DateUtil;

public class LeChecker implements ConditionChecker {

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(value == null){
			return false;
		}
		//×Ö·û
		if(pattern instanceof String){
			return value.compareTo((String)pattern) <= 0;  
		}
		//Êý×Ö
		if(pattern instanceof Number){
			Double dValue = Double.parseDouble(value);
			return dValue.compareTo(((Number)pattern).doubleValue()) <= 0;
		}
		//ÈÕÆÚ
		if(pattern instanceof Date){
			Date date = DateUtil.parse(value);
			System.out.println(date + "------" + pattern + ":" + ( date.compareTo((Date)pattern)));
			return date.compareTo((Date)pattern) <= 0; 
		}
		return false;
	}

}
