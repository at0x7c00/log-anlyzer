package me.huqiao.loganlyzer.function;



public class MinFunction implements FunctionExecutor{

	double min;
	
	@Override
	public boolean accumulation(String lineValue) {
		try{
			double x = Double.parseDouble(lineValue);
			if(x<min){
				min = x;
				return true;
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		return false;
	}

	@Override
	public Double getResult() {
		return min;
	}

}
