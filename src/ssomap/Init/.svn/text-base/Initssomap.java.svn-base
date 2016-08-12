package ssomap.Init;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ssomap.cron.HotAndCoolCron;

public class Initssomap implements ServletContextListener{
	
	public void contextInitialized(ServletContextEvent arg0) 
    {
		//HotKeyWord.get().setExtractor();
		try {
			HotAndCoolCron.putHotTable();
			HotAndCoolCron.puTCoolTable();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }//end contextInitialized method


    public void contextDestroyed(ServletContextEvent arg0) 
    {
    	
    }//end constextDestroyed method
    
}
