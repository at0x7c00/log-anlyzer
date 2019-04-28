package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.condition.Condition;
import me.huqiao.loganlyzer.condition.Conditions;
import me.huqiao.loganlyzer.enumtype.CompareType;
import me.huqiao.loganlyzer.enumtype.RelationType;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;
import sun.misc.Compare;

import javax.management.relation.Relation;
import java.util.*;
import java.util.logging.Logger;

public class WhereNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(WhereNode.class.getName());
    private RelationType relation;
    private String prop;
    private String compare;
    private String pattern1;
    private String pattern2;

   //private Condition currentBag;
    private Condition currentCondition;

    /**
     * 所有where条件和每一对括弧代表一个bagCondition
     */
    private Stack<Condition> bagConditions = new Stack<Condition>();

    private boolean isWordOverflow;


    public WhereNode() {
        super("where");
        bagConditions.push(Conditions.and());//默认的全局condition
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException {

       if(word.equals("(")){
           bagConditions.push(relation==RelationType.or ? Conditions.or() : Conditions.and());
           relation = null;
       }else if(word.equals(")")){
           if(!bagConditions.isEmpty()){
               //出栈，并且追加到前一个condition的child中
               Condition condition = bagConditions.pop();
               if(!bagConditions.isEmpty()){
                   if(condition.getRelationType()==RelationType.and) {
                       bagConditions.peek().and(condition);
                   }else{
                       bagConditions.peek().or(condition);
                   }
               }
           }else{
                throw new FeedException("unexpected ')'");
           }
       }else if(isKeyWord(word)){
           //where结束，word已经是下一个关键字了
           endup(stateMachine);
           isWordOverflow = true;
       }else if(!getCurrentCondition().getChild().isEmpty() && relation == null){//非第一个条件，必须有relation
           assertIsRelationWord(word);
           relation = Enum.valueOf(RelationType.class,word);
       }else if(prop == null){
           assertNumber(word);
           prop = word;
       }else if(compare == null){
           compare = word;
       }else if(pattern1 == null && !compare.toLowerCase().equals("between")){
           pattern1 = getInnerString(word);
           makeCondition();
       }else if(compare.toLowerCase().equals("between") && pattern2 == null){
           pattern2 = getInnerString(word);
           makeCondition();
       }else{

       }
    }

    private void assertIsRelationWord(String word) throws FeedException {
        if(!word.toLowerCase().equals("and") && !word.toLowerCase().equals("or")){
            throw new FeedException("Invalid value for comparision,expect for 'and' or 'or'");
        }
    }

    private void makeCondition() throws FeedException {
        CompareType compareType = compareTypeMap.get(compare);
        System.out.println("get compare by :" + compare + ":" + compareType);
        if(compareType == null){
            throw new FeedException("not supported compare type:" +compare);
        }
        Condition condition = new Condition(prop,pattern1,compareType,pattern2);
        if(relation == null || relation == RelationType.and){
            getCurrentCondition().and(condition);
        }else{
            getCurrentCondition().or(condition);
        }
        log.info("make condition:" + condition);

        clear();
    }

    private void clear(){
        relation = null;
        compare = null;
        prop = null;
        pattern1 = null;
        pattern2 = null;
    }

    private boolean isClear(){
        return relation == null &&
                compare == null &&
        prop == null &&
        pattern1 == null &&
        pattern2 == null;
    }


    @Override
    public boolean isWordOverflow() {
        return isWordOverflow;
    }

    /**
     * 获取当前condition，即栈顶的记录
     * @return
     */
    public Condition getCurrentCondition(){
        return bagConditions.peek();
    }


    public void endup(StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException{
        //只有一种情况，
        if(!isClear()){
            throw new FeedException("uncompleted condition for where.");
        }
        stateMachine.get().getResult().setCondition(getCurrentCondition());
        log.info("set CONDITION:" + getCurrentCondition());
        saturated = true;
        isWordOverflow = false;
    }

    static Map<String,CompareType> compareTypeMap = new HashMap<String, CompareType>();
    static {
        compareTypeMap.put(">=",CompareType.ge);
        compareTypeMap.put("ge",CompareType.ge);
        compareTypeMap.put("=",CompareType.eq);
        compareTypeMap.put("eq",CompareType.eq);

        compareTypeMap.put("!=",CompareType.ne);
        compareTypeMap.put("ne",CompareType.ne);

        compareTypeMap.put("<=",CompareType.le);
        compareTypeMap.put("le",CompareType.le);

        compareTypeMap.put("<",CompareType.lt);
        compareTypeMap.put("lt",CompareType.lt);

        compareTypeMap.put("between",CompareType.between);
        compareTypeMap.put("contains",CompareType.contains);

        compareTypeMap.put("like",CompareType.contains);

        compareTypeMap.put("startwith",CompareType.startwith);

        compareTypeMap.put("notcontains",CompareType.notcontains);

        compareTypeMap.put("regex",CompareType.regex);

    }

}
