package me.huqiao.loganlyzer.condition;

import java.util.ArrayList;
import java.util.List;

import me.huqiao.loganlyzer.condition.checker.ConditionChecker;
import me.huqiao.loganlyzer.condition.checker.ConditionCheckers;
import me.huqiao.loganlyzer.enumtype.CompareType;
import me.huqiao.loganlyzer.enumtype.RelationType;
import me.huqiao.loganlyzer.main.AttPreProcessor;
import me.huqiao.loganlyzer.main.FileScaner;

public class Condition {

	private CompareType type;
	private String prop;
	private Object pattern;
	private Object pattern2;
	private AttPreProcessor processor;
	
	private Integer propIndex;
	
	private List<Condition> child;
	private RelationType relationType = RelationType.and;
	private boolean isBag;
	
	public Condition(String prop,Object pattern,CompareType type){
		this.prop = prop;
		this.pattern = pattern;
		this.type = type;
		this.propIndex = Integer.parseInt(prop);
	}
	public Condition(String prop,Object pattern,CompareType type,AttPreProcessor processor){
		this.prop = prop;
		this.pattern = pattern;
		this.type = type;
		this.processor = processor;
		this.propIndex = Integer.parseInt(prop);
	}
	public Condition(String prop,Object pattern,CompareType type,Object pattern2){
		this.prop = prop;
		this.pattern = pattern;
		this.pattern2 = pattern2;
		this.type = type;
		this.propIndex = Integer.parseInt(prop);
	}
	
	private Condition(){
		
	}
	
	public static Condition getInstanceForAnd(){
		Condition c = new Condition();
		c.setChild(new ArrayList<Condition>());
		c.setRelationType(RelationType.and);
		c.setBag(true);
		return c;
	}
	public static Condition getInstanceForOr(){
		Condition c = new Condition();
		c.setChild(new ArrayList<Condition>());
		c.setRelationType(RelationType.or);
		c.setBag(true);
		return c;
	}
	
	public CompareType getType() {
		return type;
	}
	public void setType(CompareType type) {
		this.type = type;
	}
	public String getProp() {
		return prop;
	}
	public void setProp(String prop) {
		this.prop = prop;
	}

	public Object getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public List<Condition> getChild() {
		return child;
	}

	public void setChild(List<Condition> child) {
		this.child = child;
	}

	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}
	public boolean check(String line,FileScaner fileScaner) {
		if(isBag()){
			boolean totalResult = isAndRelation();
			if(child!=null){
				for(Condition c : child){
					boolean cResult = c.check(line,fileScaner);
					if(isAndRelation()){
						totalResult = totalResult && cResult;
					}else{
						totalResult = totalResult || cResult;
					}
				}
				return totalResult;
			}else{
				return true;
			}
		}else{
			String value = getPropValue(line);
			value = fileScaner.preProcess(value, propIndex);
			if(processor!=null){
				value = processor.process(value);
			}
			ConditionChecker checker = ConditionCheckers.getChecker(this.getType());
			return checker.check(value, pattern, pattern2);
		}
	}
	
	private String getPropValue(String line){
		String[] atts = line.split(FileScaner.spider);
		String value = atts[propIndex];
		return value;
	}
	
	public boolean isBag() {
		return isBag;
	}
	public void setBag(boolean isBag) {
		this.isBag = isBag;
	}
	
	private boolean isAndRelation(){
		return relationType == RelationType.and;
	}
}
