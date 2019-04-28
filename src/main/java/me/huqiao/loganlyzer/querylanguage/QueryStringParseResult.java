package me.huqiao.loganlyzer.querylanguage;

import java.util.ArrayList;
import java.util.List;

import me.huqiao.loganlyzer.condition.Condition;
import me.huqiao.loganlyzer.condition.Conditions;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.util.StringUtil;

public class QueryStringParseResult {
	
	private Condition condition;
	private List<OrderBy> orderBy = new ArrayList<OrderBy>();
	private Integer limit;
	private String groupBy;
	private String select;
	private String from;
	private String splitBy;

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<OrderBy> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<OrderBy> orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSplitBy() {
		return splitBy;
	}

	public void setSplitBy(String splitBy) {
		this.splitBy = splitBy;
	}

	public boolean isSaturated(){
		return !StringUtil.isEmpty(select) && !StringUtil.isEmpty(from);//仅select和from是必填项
	}
}
