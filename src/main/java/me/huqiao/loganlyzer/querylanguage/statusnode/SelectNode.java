package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.exception.ExceptionUtil;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;
import sun.org.mozilla.javascript.internal.EcmaError;

import java.util.logging.Logger;

public class SelectNode extends AbstractNode<LogQueryLanguageParserStateMachine> {
    static Logger log = Logger.getLogger(SelectNode.class.getName());

    public SelectNode() {
        super("select");
    }

    @Override
    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine) throws FeedException {
        LogQueryLanguageParserStateMachine machine = stateMachine.get();
        word = getInnerString(word);
        for(String prop : word.split(",")){
            prop = getInnerString(prop);
            assertNumber(prop);
        }
        //格式OK，将select的字段设置到解析结果中
        machine.getResult().setSelect(word);
        saturated = true;
        log.info("set SELECT as " + word);
    }

}
