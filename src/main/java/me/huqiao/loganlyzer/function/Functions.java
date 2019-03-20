package me.huqiao.loganlyzer.function;


public abstract class Functions {

	public static FunctionExecutor getInstance(String functionName){
		FunctionExecutor res = null;
		if(functionName.equals("avg")){
			return new AvgFunction();
		}else if(functionName.equals("count")){
			return new CountFunction();
		}else  if(functionName.equals("sum")){
			return new SumFunction();
		}else  if(functionName.equals("max")){
			return new MaxFunction();
		}else  if(functionName.equals("min")){
			return new MinFunction();
		}
		return res;
	}
	
}
