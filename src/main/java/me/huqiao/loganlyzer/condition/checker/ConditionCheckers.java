package me.huqiao.loganlyzer.condition.checker;

import java.util.HashMap;
import java.util.Map;

import me.huqiao.loganlyzer.enumtype.CompareType;

public abstract class ConditionCheckers {
	
	static Map<CompareType,ConditionChecker> map = new HashMap<CompareType,ConditionChecker>();
	static {
		
		map.put(CompareType.between, new BetweenChecker());
		map.put(CompareType.contains, new ContainsChecker());
		map.put(CompareType.endwith, new EndwithChecker());
		map.put(CompareType.eq, new EqChecker());
		map.put(CompareType.ge, new GeChecker());
		map.put(CompareType.gt, new GtChecker());
		map.put(CompareType.le, new LeChecker());
		map.put(CompareType.lt, new LtChecker());
		map.put(CompareType.ne, new NeChecker());
		map.put(CompareType.notcontains, new NotcontainsChecker());
		map.put(CompareType.startwith, new StartwithChecker());
		map.put(CompareType.regex, new RegexChecker());
	}

	public static ConditionChecker getChecker(CompareType type){
		return map.get(type);
	}
	
}
