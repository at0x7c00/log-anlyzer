package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.logging.Logger;

public class LimitNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(LimitNode.class.getName());

    public LimitNode() {
        super("limit");
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine)
            throws FeedException {
        assertNumber(word);
        stateMachine.get().getResult().setLimit(Integer.parseInt(word));
        log.info("set LIMIT as :" + word);
        saturated = true;

    }
}
