package me.huqiao.loganlyzer.main;

import java.util.ArrayList;
import java.util.List;

import me.huqiao.loganlyzer.enumtype.OrderByDirection;
import me.huqiao.loganlyzer.orderby.OrderBy;

public class LineResultBean implements Comparable<LineResultBean> {

	private Integer lineNo;
	private List<Comparable<?>> lineProps = new ArrayList<Comparable<?>>();
	private List<OrderBy> orderBys;
	
	public LineResultBean(Integer lineNo, List<OrderBy> orderBys){
		this.lineNo = lineNo;
		this.orderBys = orderBys;
 	}
	
	@Override
	public int compareTo(LineResultBean o) {
		int compareRes = 0;
		if(orderBys!=null){
			for(OrderBy orderBy : orderBys){
				Comparable thisProp = this.getProp(orderBy.getPropIndex());
				Comparable otherProp = o.getProp(orderBy.getPropIndex());
				if(thisProp == null || otherProp == null){
					break;
				}
				compareRes = thisProp.compareTo(otherProp);
				if(compareRes!=0){
					compareRes = orderBy.getDirection() == OrderByDirection.asc ? compareRes : -compareRes;
					break;
				}
			}
		}
		
		return compareRes == 0 ? lineNo.compareTo(o.lineNo) : compareRes;
	}
	
	public void addProp(Comparable<?> prop){
		lineProps.add(prop);
	}
	
	public Comparable<?> getProp(Integer index){
		return lineProps.get(index);
	}
	
	public List<Object> getLineProps(){
		List<Object> list = new ArrayList<Object>(lineProps.size());
		for(Comparable x : lineProps){
			list.add(x);
		}
		return list;
	}
	
}
