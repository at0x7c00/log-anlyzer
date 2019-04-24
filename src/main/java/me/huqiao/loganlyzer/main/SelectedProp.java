package me.huqiao.loganlyzer.main;

public class SelectedProp {

	private Integer index;
	private String function;//暂时只支持一级函数调用
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public SelectedProp(Integer index, String function) {
		super();
		this.index = index;
		this.function = function;
	}
	public boolean hasFunction(){
		return function != null;
	}
	//private Function function;
}
