package me.huqiao.loganlyzer.querylanguage;

import me.huqiao.loganlyzer.querylanguage.statemachine.StatusNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public static final List<String> keyWords = new ArrayList<String>();

    static{
        String keys = "select,from,where,order,group,split,limit,by";
        for(String key : keys.split(",")){
            keyWords.add(key);
        }
    }
}
