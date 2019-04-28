package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.LogQueryLanguageParserStateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.AbstractNode;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;

public class Start extends AbstractNode<LogQueryLanguageParserStateMachine> {

    public Start() {
        super("start");
    }

    public void feed(String word, StateMachine<LogQueryLanguageParserStateMachine> stateMachine){

    }

    @Override
    public boolean isSaturated() {
        return true;
    }
}

/*
           select                  where                   *** by
  start ----------> (select)   ------------> (where) -------------------------
                    need 0,1,2                need conditions                |
                       |            from                   *** by           |------------>(order/grop/limit/split)
                       |-------------------> (from)  -----------------------|                  need by
                                               need files
*/