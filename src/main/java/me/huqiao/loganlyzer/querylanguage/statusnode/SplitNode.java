package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.logging.Logger;

public class SplitNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(SplitNode.class.getName());
    boolean byAccepted = false;
    public SplitNode() {
        super("split");
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine)
            throws FeedException {
        if(!byAccepted){
            assertEquals(word,"by");
            byAccepted = true;
        }else{
            assertNotEmpty(word);
            stateMachine.get().getResult().setSplitBy(word);
            log.info("set SPLIT as :" + word);
            saturated = true;
        }
    }
}
