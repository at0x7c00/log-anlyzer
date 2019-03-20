package me.huqiao.loganlyzer.condition.checker;

import me.huqiao.loganlyzer.condition.checker.ConditionChecker;

public class StartwithChecker implements ConditionChecker {

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(pattern instanceof String){
			return pattern.toString().startsWith(value);
		}
		return false;
	}

}
