package me.huqiao.loganlyzer.querylanguage.statemachine;

import me.huqiao.loganlyzer.querylanguage.Constants;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.StatusNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.*;

public abstract class AbstractNode<T> implements StatusNode<T> {

    protected String name;
    protected Map<String,StatusNode> lines = new HashMap<String,StatusNode>();
    public AbstractNode(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    protected boolean saturated = false;

    public StatusNode next(String word) {
        StatusNode node = lines.get(word.toLowerCase());
        if(node==null){
            node = lines.get("*");
            if(node!=null){
                //System.out.println("get next node by *:" + node);
            }
        }else{
            //System.out.println("get next node by '" + word + "':" + node);
        }
        return node;
    }

    @Override
    public abstract void feed(String word, StateMachine<T> stateMachine)throws FeedException;

    public List<String> getAvailableLines() {
        List<String> x = new ArrayList<String>();
        x.addAll(lines.keySet());
        return x;
    }

    public void addLine(String lineName, StatusNode node){
        lines.put(lineName,node);
    }


    @Override
    public String toString() {
        return "["+getName()+"]";
    }

    @Override
    public boolean isSaturated() {
        return saturated;
    }

    protected void assertNotKeyWord(String word) throws FeedException {
        if(Constants.keyWords.contains(word)){
            throw new FeedException("invalid value for from, '"+word+"' is a key word!");
        }
    }

    /**
     * @param str 类似"abc"或者,'abc'
     * @return 类似 abc或者abc
     */
    protected String getInnerString(String str){
        if(isString(str)){
            return str.length() > 2 ? str.substring(1,str.length() - 1) : "";
        }
        return str;
    }

    protected boolean isKeyWord(String word) throws FeedException {
       return Constants.keyWords.contains(word);
    }

    protected boolean isString(String word){
        return (word.startsWith("'") && word.endsWith("'")
                || (word.startsWith("\"") && word.endsWith("\"")));
    }

    protected void assertNumber(String word) throws FeedException {
        try{
            Integer.parseInt(word);//判断是否都是数字
        }catch(Exception e){
            throw new FeedException("invalid value for group by,expect for a number.");
        }
    }

    protected void assertEquals(String word,String expect) throws FeedException {
        if(!word.toLowerCase().equals(expect)){
            throw new FeedException("invalid value for group by,expect for '"+expect+"'.");
        }
    }

    protected void assertNotEmpty(String word) throws FeedException {
        if(word == null || word.equals("")){
            throw new FeedException("params for this can not be empty!");
        }
    }


    /**
     * 多数情况不会出现“喂多”的情况
     * 只有当可能出现喂多的节点才需要覆盖此方法
     */
    public boolean isWordOverflow(){
        return false;
    }

    public void endup(StateMachine<T> stateMachine) throws FeedException{

    }

}
