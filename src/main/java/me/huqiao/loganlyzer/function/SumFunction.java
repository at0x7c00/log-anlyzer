package me.huqiao.loganlyzer.function;



public class SumFunction implements FunctionExecutor{

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
		return total;
	}

}
