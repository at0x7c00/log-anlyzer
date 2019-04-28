package me.huqiao.loganlyzer.condition;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import me.huqiao.loganlyzer.condition.checker.ConditionChecker;
import me.huqiao.loganlyzer.condition.checker.ConditionCheckers;
import me.huqiao.loganlyzer.enumtype.CompareType;
import me.huqiao.loganlyzer.enumtype.RelationType;
import me.huqiao.loganlyzer.main.AttPreProcessor;
import me.huqiao.loganlyzer.main.FileScaner;

/**
 * where (...) 所有的条件构成一个condition,relationType为and，isBag为true <br/>
 * and a = 1 构成一个condition,relationType为and，isBag为false<br/>
 * condition与condition之间的结果都以relationType标志进行&&或者||运算<br/>
 * 当isBag是true时，check的时候，需要看child中的条件<br/>
 * 从LQL(log query language)角度来看，所有的括弧表示一个bag，第一级也都是bag。
 * where
 * a = 0
 * and b = 1
 * or (
 *    c = 2
 *    and d = 3
 * )
 */
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
			if(child!=null && !child.isEmpty()){
                Condition child0 = child.get(0);
                boolean res = child0.check(line,fileScaner);
                for(int i = 1;i<child.size();i++){
                    Condition childX = child.get(i);
                    boolean childXRes = childX.check(line,fileScaner);
                    if(childX.isAndRelation()){
                        res = res && childXRes;
                    }else{
                        res = res || childXRes;
                    }
                }
                return res;
			}else{
				return true;
			}
		}else{
			String value = getPropValue(line,fileScaner);
			value = fileScaner.preProcess(value, propIndex);
			if(processor!=null){
				value = processor.process(value);
			}
			ConditionChecker checker = ConditionCheckers.getChecker(this.getType());
			return checker.check(value, pattern, pattern2);
		}
	}
	
	private String getPropValue(String line, FileScaner fileScaner){
		String[] atts = line.split(fileScaner.getSpider());
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

	public void and(Condition condition){
        condition.setRelationType(RelationType.and);
        child.add(condition);
    }

    public void or(Condition condition){
        condition.setRelationType(RelationType.or);
        child.add(condition);
    }

    @Override
    public String toString() {

        if(isBag()){
            StringBuffer sb = new StringBuffer();
            for(Condition con : getChild()){
                sb.append(" ").append(con.getRelationType()).append(" (").append(con.toString()).append(")");
            }
            return sb.toString();
        }else{
            return propIndex  + " " + getType() + " " + pattern +   (pattern2 == null ? "" : "," + pattern2);
        }
    }
}
