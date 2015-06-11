package com.uoa.acpanalysis.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utilities {
	
	public static List<Date> getDate(List<String> datesInString) throws ParseException{
		List<Date> dates = new ArrayList<Date>();
		String pattern = "yyyy/MM/dd HH:mm";
		for (String date : datesInString){			
		    SimpleDateFormat format = new SimpleDateFormat(pattern);
		      Date newDate = format.parse(date);
		      dates.add(newDate);		    
		}
		return dates;		
	}

}
