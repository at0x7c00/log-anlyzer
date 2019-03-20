package me.huqiao.loganlyzer.condition.checker;

public class RegexChecker implements ConditionChecker{

	@Override
	public boolean check(String value, Object pattern, Object pattern2) {
		if(pattern instanceof String){
			return value.matches((String)pattern);
		}
		return false;
	}

}
