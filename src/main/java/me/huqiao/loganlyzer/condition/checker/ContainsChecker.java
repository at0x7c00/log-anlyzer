package me.huqiao.loganlyzer.condition.checker;

public class ContainsChecker implements ConditionChecker {

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(pattern instanceof String){
			return value.toString().contains((String)pattern);
		}
		return false;
	}

}
