package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.Constants;
import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.StatusNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.logging.Logger;

public class GroupByNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(GroupByNode.class.getName());
    boolean byAccepted = false;
    public GroupByNode() {
        super("groupBy");
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine)
            throws FeedException {

        if(!byAccepted){
            assertEquals(word,"by");
            byAccepted = true;
        }else{
            word = getInnerString(word);
            assertNumber(word);
            stateMachine.get().getResult().setGroupBy(word);
            log.info("set GROUP_BY as :" + word);
            saturated = true;
        }
    }
}
