package me.huqiao.loganlyzer.function;

import me.huqiao.loganlyzer.util.NumberUtil;


public class AvgFunction implements FunctionExecutor{

	double total;
	int lineCount;
	
	@Override
	public boolean accumulation(String lineValue) {
		try{
			total+=Double.parseDouble(lineValue);
			lineCount++;
		}catch(Exception e){
			//e.printStackTrace();
		}
		return false;
	}

	@Override
	public Double getResult() {
		if(lineCount>0){
			return NumberUtil.round2Digit((total*1.0)/lineCount);
		}
		return 0d;
	}

}
