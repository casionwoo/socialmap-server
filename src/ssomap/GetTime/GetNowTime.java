package ssomap.GetTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetNowTime {
	
	@SuppressWarnings("deprecation")
	public static String oneHourAgo() {
		Date today = new Date();
		today.setHours(today.getHours()-1);
		today.setSeconds(0);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String nowAsString = df.format(today);
		return nowAsString;
	}
	
	public static String CurrentTime(){
		Date today = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String nowAsString = df.format(today);
		
		return nowAsString;
	}
	
	@SuppressWarnings("deprecation")
	public static String NHourAgo(int n) {
		Date today = new Date();
		today.setHours(today.getHours()-n);
		today.setSeconds(0);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String nowAsString = df.format(today);
		
		return nowAsString;
	}
	
	public static String DateToStr(Date date){
		Date today = date;
		today.setSeconds(0);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String nowAsString = df.format(today);
		
		return nowAsString;
	}
}
