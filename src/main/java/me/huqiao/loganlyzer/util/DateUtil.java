package me.huqiao.loganlyzer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DateUtil {
	static Map<String,SimpleDateFormat>sdfs = new LinkedHashMap<String,SimpleDateFormat>();
	static{
		sdfs.put("yyyy-MM-dd HH:mm:ss",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		sdfs.put("yyyy-MM-dd HH:mm",new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		sdfs.put("yyyy-MM-dd HH",new SimpleDateFormat("yyyy-MM-dd HH"));
		sdfs.put("yyyy-MM-dd",new SimpleDateFormat("yyyy-MM-dd"));
		
		sdfs.put("dd/MM/yyyy HH:mm:ss",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
		sdfs.put("dd/MM/yyyy:HH:mm:ss",new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss"));
	}
	
	public static Date parse(String dateStr){
		for(Map.Entry<String,SimpleDateFormat> entry : sdfs.entrySet()){
			try{
				SimpleDateFormat sdf = entry.getValue();
				return sdf.parse(dateStr);
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		return null;
	}

	public static String format(Date date, String pattern) {
		return sdfs.get(pattern).format(date);
	}
	
}