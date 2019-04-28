package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;
import org.apache.logging.log4j.core.config.Order;

import java.util.logging.Logger;

public class OrderByNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(OrderByNode.class.getName());

    private boolean byAccepted = false;
    private String prop;
    private String directory;
    private boolean isWordOverflow;

    public OrderByNode() {
        super("orderBy");
    }

    /**
     * eg:
     *    order by a desc,b asc,c,d
     * while be split as:
     * order
     * by
     * a
     * desc,b
     * asc,c,c
     */
    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine)
            throws FeedException {
           if(!byAccepted){
               assertEquals(word,"by");
               byAccepted = true;
           }else {

               //始终保持每次只“处理”一个字符，如果包含逗号，则分开单独处理
               if(word.contains(",")){
                    for(String w : word.split(",")){
                        process(w,stateMachine);
                    }
               }else{
                    process(word,stateMachine);
               }
           }
    }

    private void process(String word,StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException {
        if(prop == null){
            if(isKeyWord(word)){
                if(stateMachine.get().getResult().getOrderBy().isEmpty()){
                    throw new FeedException("invalid value for order by.");
                }else{
                    isWordOverflow = true;
                    saturated = true;
                }
            }else {
                assertNumber(word);
                prop = word;
            }
        }else {
            //可能是：1)方向字符,2)下一个排序字段,3)下一个关键字
            if(isDirection(word)){
                OrderBy orderBy = null;
                if(word.toLowerCase().equals("asc")){
                    orderBy = OrderBy.asc(Integer.parseInt(prop));
                }else{
                    orderBy = OrderBy.desc(Integer.parseInt(prop));
                }
                stateMachine.get().getResult().getOrderBy().add(orderBy);
                log.info("add ORDER_BY :" + orderBy);
                prop = null;//清空，以备下一组order by使用
            }else if(isKeyWord(word)){
                //Order by这种节点只可能从这里退出，否则就得等待调用endup了
                isWordOverflow = true;
                saturated = true;
            }else{
                assertNumber(word);
                stateMachine.get().getResult().getOrderBy().add(OrderBy.asc(Integer.parseInt(prop)));
                stateMachine.get().getResult().getOrderBy().add(OrderBy.asc(Integer.parseInt(word)));
                prop = null;//同上
            }
        }
    }

    private boolean isDirection(String word) {
        return word.toLowerCase().equals("asc")
                || word.toLowerCase().equals("desc");
    }

    @Override
    public boolean isWordOverflow() {
        return isWordOverflow;
    }

    public void endup(StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException{
        //只有一种情况，即prop可能不为空（缺省asc和desc）
        if(prop!=null){
            assertNumber(prop);
            stateMachine.get().getResult().getOrderBy().add(OrderBy.asc(Integer.parseInt(prop)));
            isWordOverflow = false;
            saturated = true;
        }
    }
}
