package me.huqiao.loganlyzer.querylanguage.statemachine;

import java.util.Iterator;


/**
 * 状态机
 * @param <T> 状态机内置的业务对象
 */
public interface StateMachine<T> {

    /**
     * @return 当状态机满足所有条件时返回true，否则返回false
     */
    public boolean isSaturated();

    /**
     *
     * @return 返回内置的业务对象
     */
    public T get();

    /**
     * 开启状态机
     */
    public void start()  throws Exception;

    /**
     * 重置状态机
     */
    public void reset();

    /**
     * 获取word组
     * @return
     */
    public Iterator<String> getWords();


}
