package me.huqiao.loganlyzer.querylanguage.statusnode;

import me.huqiao.loganlyzer.querylanguage.statemachine.StatusNode;

import java.util.HashMap;
import java.util.Map;

public class StatusNodeFactory {
    public Map<String,StatusNode> statusNodeMap;
    public StatusNodeFactory(){
        statusNodeMap = new HashMap<String,StatusNode>();
        StatusNode start = new Start();
        StatusNode select = new SelectNode();
        StatusNode from = new FromNode();
        StatusNode where = new WhereNode();
        StatusNode limit = new LimitNode();
        StatusNode splitBy = new SplitNode();
        StatusNode groupBy = new GroupByNode();
        StatusNode orderBy = new OrderByNode();

        //start-->select
        start.addLine("select",select);

        //select->where,from,group by,order by,limit,split by
        select.addLine("where",where);
        select.addLine("from",from);
        select.addLine("group",from);
        select.addLine("order",orderBy);
        select.addLine("limit",limit);
        select.addLine("split",splitBy);

        //from->where,group by,order by,limit,split by
        from.addLine("where",where);
        from.addLine("group",groupBy);
        from.addLine("order",orderBy);
        from.addLine("limit",limit);
        from.addLine("split",splitBy);

        //where->group by,order by,limit,split by
        where.addLine("group",groupBy);
        where.addLine("order",orderBy);
        where.addLine("limit",limit);
        where.addLine("split",splitBy);

        //group->order by,limit,split by
        groupBy.addLine("order",orderBy);
        groupBy.addLine("limit",limit);
        groupBy.addLine("split",splitBy);
        //order->group by,limit,split by
        orderBy.addLine("group",groupBy);
        orderBy.addLine("limit",limit);
        orderBy.addLine("split",splitBy);

        //limit->order by,group by,split by
        limit.addLine("group",groupBy);
        limit.addLine("split",splitBy);
        limit.addLine("order",orderBy);


        //split->order by,limit,group by


        statusNodeMap.put("start",start);
        statusNodeMap.put("select",select);
        statusNodeMap.put("from",from);
        statusNodeMap.put("where",where);
        statusNodeMap.put("group",groupBy);
        statusNodeMap.put("limit",limit);
        statusNodeMap.put("split",splitBy);
    }

    public StatusNode start(){
        return statusNodeMap.get("start");
    }

    public StatusNode select(){
        return statusNodeMap.get("select");
    }

    public StatusNode from(){
        return statusNodeMap.get("from");
    }

    public StatusNode where(){
        return statusNodeMap.get("where");
    }

}
