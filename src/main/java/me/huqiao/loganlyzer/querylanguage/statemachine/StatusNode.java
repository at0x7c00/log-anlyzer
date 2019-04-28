package me.huqiao.loganlyzer.querylanguage.statemachine;

import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;

import java.util.List;

public interface StatusNode<T> {

    public String getName();
    public StatusNode next(String word);
    public void feed(String word, StateMachine<T> stateMachine)throws FeedException;
    public List<String> getAvailableLines();

    public void addLine(String line, StatusNode node);
    public boolean isSaturated();

    /**
     * 是否溢出了
     * 有的节点需要的参数可能是动态的，比如order by 0,1,2,...n
     * 此时不知道何时结束，只有当遇到其他关键字的时候才知道该结束了
     * 因此可能出现“喂多”的情况
     * 当出现“喂多”时，在尝试切换到下一个状态时，不能再从word组中取新的，而是用溢出的这个词
     * @return
     */
    public boolean isWordOverflow();

    /**
     * 比如where或order by
     * 当整个语句以他们结尾时，后端没有更多的其他语句，所以无法依赖关键字切换到其他节点，
     * 并且他们的参数不是固定的，于是他们将无从知道是否该标记为饱和。
     * 此时需要在遍历完所有word之后，调用节点的endup方法，以触发“结束”
     * 节点需要在该方法中判断节点的业务状态是否完整
     *
     * @return
     */
    public void endup(StateMachine<T> stateMachine)throws FeedException;
}
