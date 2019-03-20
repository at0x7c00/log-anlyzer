package me.huqiao.loganlyzer.util;

public class NumberUtil {
	public static final int ROUND_DEFAULT_DIGIT = 2;
	/**
	 * 四舍五入value�?
	 * 当n>0时，保留小数点后n位，当n<0时将整数部分从左�?��第n位以后变�?
	 * n小于直接返回value
	 * @param value
	 * @param n(n>0)
	 * @return
	 */
	public static double round(double value,int n){
		/*if(n<0){
			return value;
		}*/
		double d = Math.pow(10, n);
		return  Math.round(value*d)/d;
	}
	/**
	 * 
	 * SalaryRound:
	 * 百分位四舍五入（位数设定：NumberUtil.ROUND_DEFAULT_DIGIT=2�?
	 * @param value
	 * @return double   
	 * @throws 
	 * @author   cp
	 * @since    Ver 1.1
	 * @Date	 2011-6-24	下午04:35:28
	 */
	public static double round2Digit(double value){
		return  round(value,NumberUtil.ROUND_DEFAULT_DIGIT);
	}
	
	/**
	 * double类型如果没有小数部分则只显示整数，不显示小数点及小数
	 * @param value
	 * @return
	 */
	public static String round2DigitToString(double value){
		return  NumberUtil.doubleToString(round(value,NumberUtil.ROUND_DEFAULT_DIGIT));
	}
	
	public static String doubleToString(double value){
		String strValue = "";
		if( Math.floor(value) == value ){
			strValue = strValue + (int)value ;
		}else{
			strValue = strValue + value;
		}
		return strValue;
	}
	/**
	 * �?.5�?
	 * 当n>0时，保留小数点后n位，当n<0时将整数部分从左�?��第n位以后变�?
	 * @param value
	 * @param n(n>0)
	 * @return
	 */
	public static Double BinaryCeil(Double value,int n){
		Double retVal=Double.valueOf(0);
		double d = Math.pow(10, n-1);
		if (value!=null) {
			retVal=Math.ceil(value*d*2)/(d*2);
		}
		return retVal;
	}
	
}
