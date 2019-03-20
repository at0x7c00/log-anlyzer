package me.huqiao.loganlyzer.function;

import me.huqiao.loganlyzer.util.NumberUtil;


public class CountFunction implements FunctionExecutor{

	int total;
	
	@Override
	public boolean accumulation(String lineValue) {
		total++;
		return false;
	}

	@Override
	public Double getResult() {
		return NumberUtil.round2Digit(total*1.0);
	}

}
