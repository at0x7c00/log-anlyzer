package me.huqiao.loganlyzer.condition;

import me.huqiao.loganlyzer.enumtype.CompareType;
import me.huqiao.loganlyzer.enumtype.RelationType;
import me.huqiao.loganlyzer.main.AttPreProcessor;

public abstract class Conditions {
	
	public static Condition and(Condition...conditions){
		Condition c = Condition.getInstanceForAnd();
		if(conditions!=null){
			for(Condition con : conditions){
				con.setRelationType(RelationType.and);
				c.getChild().add(con);
			}
		}
		return c;
	}
	
	public static Condition or(Condition...conditions){
		Condition c = Condition.getInstanceForOr();
		if(conditions!=null){
			for(Condition con : conditions){
				con.setRelationType(RelationType.or);
				c.getChild().add(con);
			}
		}
		return c;
	}


	public static Condition eq(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.eq);
	}

	public static Condition ne(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.ne);
	}

	public static Condition gt(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.gt);
	}

	public static Condition lt(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.lt);
	}

	public static Condition ge(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.ge);
	}

	public static Condition le(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.le);
	}

	public static Condition contains(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.contains);
	}
	public static Condition regexMatch(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.regex);
	}

	public static Condition notcontains(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.notcontains);
	}

	public static Condition startwith(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.startwith);
	}

	public static Condition endwith(String prop,Object pattern) {
		return new Condition(prop,pattern,CompareType.endwith);
	}
	public static Condition between(String prop,Object pattern,Object pattern2) {
		return new Condition(prop,pattern,CompareType.between);
	}
	
	public static Condition eq(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.eq,processor);
	}

	public static Condition ne(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.ne,processor);
	}

	public static Condition gt(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.gt,processor);
	}

	public static Condition lt(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.lt,processor);
	}

	public static Condition ge(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.ge,processor);
	}

	public static Condition le(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.le,processor);
	}

	public static Condition contains(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.contains,processor);
	}

	public static Condition notcontains(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.notcontains,processor);
	}

	public static Condition startwith(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.startwith,processor);
	}

	public static Condition endwith(String prop,Object pattern,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.endwith,processor);
	}
	public static Condition between(String prop,Object pattern,Object pattern2,AttPreProcessor processor) {
		return new Condition(prop,pattern,CompareType.between,processor);
	}

}
