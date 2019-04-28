package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.Constants;
import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.logging.Logger;

public class FromNode extends AbstractNode<LogQueryLanguageParserStateMachine> {

    static Logger log = Logger.getLogger(FromNode.class.getName());

    public FromNode(){
        super("from");
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException {
        assertNotKeyWord(word);
        word = getInnerString(word);
        stateMachine.get().getResult().setFrom(word);
        log.info("set FROM as :" + word);
        saturated = true;
    }


}
