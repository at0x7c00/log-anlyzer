package me.huqiao.loganlyzer.orderby;

import me.huqiao.loganlyzer.enumtype.OrderByDirection;

public class OrderBy {

	private Integer propIndex;
	private OrderByDirection direction;
	private Convertor convertor;
	
	public OrderBy(Integer propIndex, OrderByDirection direction) {
		this.propIndex = propIndex;
		this.direction = direction;
	}
	public Integer getPropIndex() {
		return propIndex;
	}
	public void setPropIndex(Integer propIndex) {
		this.propIndex = propIndex;
	}
	public OrderByDirection getDirection() {
		return direction;
	}
	public void setDirection(OrderByDirection direction) {
		this.direction = direction;
	}
	
	public static OrderBy desc(Integer index){
		return new OrderBy(index,OrderByDirection.desc);
	}
	
	public static OrderBy asc(Integer index){
		return new OrderBy(index,OrderByDirection.asc);
	}
	
	public OrderBy asInteger(){
		convertor = new IntegerConvertor();
		return this;
	}
	
	public OrderBy asDouble(){
		convertor = new DoubleConvertor();
		return this;
	}
	public void as(Convertor convertor){
		this.convertor = convertor;
	}
	@Override
	public String toString() {
		return propIndex + " " + direction;
	}
	public Convertor getConvertor() {
		return convertor;
	}
	
}
