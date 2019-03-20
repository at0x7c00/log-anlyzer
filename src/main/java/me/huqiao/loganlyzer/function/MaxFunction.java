package me.huqiao.loganlyzer.function;



public class MaxFunction implements FunctionExecutor{

	double max;
	
	@Override
	public boolean accumulation(String lineValue) {
		try{
			double x = Double.parseDouble(lineValue);
			if(x>max){
				max = x;
				return true;
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		return false;
	}

	@Override
	public Double getResult() {
		return max;
	}

}
