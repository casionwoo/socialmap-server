package ssomap.cron;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ssomap.GetTime.GetNowTime;
import ssomap.hotQueue.EventQueue;
import ssomap.hotQueue.EventQueue.Counter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@Path("/make")
public class HotAndCoolCron {

	private static DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private final String GEOCELL = "geocell";
	private final String TIME = "makeTimeS";
	private final String HOTPLACE = "HOTPLACE";
	private final String COOLPLACE = "COOLPLACE";
	private final String COOLCOUNT = "Count";
	private final String COOLCELL = "CoolCell";
	private final static int COOLTABLESIZE = 50;

	static HotAndCoolCron a = new HotAndCoolCron();

	public class coolCounter{
		String geocell = null;
		int count = 0;
		public String getGeocell() {
			return geocell;
		}
		public void setGeocell(String geocell) {
			this.geocell = geocell;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public coolCounter(String geocell, int count) {
			super();
			this.geocell = geocell;
			this.count = count;
		}

		public void increase(int num){
			count = count+num;
		}
	}

	public class hotItem{
		String geocell=null;
		String time= null;
		public String getGeocell() {
			return geocell;
		}
		public void setGeocell(String geocell) {
			this.geocell = geocell;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public hotItem(String geocell, String time) {
			super();
			this.geocell = geocell;
			this.time = time;
		}

	}

	private static Map<String, coolCounter> coolTable = new HashMap<String, coolCounter>();
	private static List<hotItem> hotTable = new ArrayList<HotAndCoolCron.hotItem>();
	private static ArrayList<hotItem> b = new ArrayList<hotItem>() ;
	@GET
	@Path("/hotplace")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clear() throws IOException, ParseException {
		if (ResisterEntities())
			return Response.status(HttpServletResponse.SC_OK).build();
		else
			return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
	}

	private void RotateQueue() {
		EventQueue.get().doRotate();
	}

	public static List<hotItem> getHotTable() {
		return hotTable;
	}

	public static Map<String, coolCounter> getCoolTable() {
		return coolTable;
	}

	public static void putHotTable() throws ParseException {
		Query hotQuery = new Query("HOTPLACE");
		PreparedQuery pq = datastore.prepare(hotQuery);
		for (Entity result : pq.asIterable()) {
			String hotCell = (String) result.getProperty("geocell");
			String date = (String) result.getProperty("makeTimeS");
			if(hotCell == null)
				continue;

			hotItem temp = a.new hotItem(hotCell , date);
			hotTable.add(temp);
		}

		/*		String topCell = EventQueue.get().getTopCell();
		if(topCell != null)
		{
			String now = GetNowTime.CurrentTime();
			hotItem temp = a.new hotItem(topCell, now);
			hotTable.add(temp);
		}*/
	}

	public static boolean isContains(hotItem a){
		for(hotItem hotitem : hotTable){
			if(hotitem.getGeocell().equals(a.getGeocell()))
				return true;
		}
		return false;
	}

	public boolean ResisterEntities() throws ParseException {
		RotateQueue();

		String topCell = EventQueue.get().getTopCell();
		String time = GetNowTime.CurrentTime();

		Entity entity = new Entity(HOTPLACE);
		entity.setUnindexedProperty(GEOCELL, topCell);
		entity.setProperty(TIME, time);
		datastore.put(entity);
		if (hotTable.isEmpty()) {
			putHotTable();
		}
		else if(topCell == null)
			return DeleteEntities();
		else {
			hotItem temp = a.new hotItem(topCell, time);
			hotTable.add(temp);
		}

		return DeleteEntities();
	}

	public boolean DeleteEntities() throws ParseException {

		Filter TwelveHoursAgoEntity = new FilterPredicate(TIME,
				FilterOperator.LESS_THAN, GetNowTime.NHourAgo(10));

		Query q = new Query(HOTPLACE).setFilter(TwelveHoursAgoEntity);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {

			String topCell = (String) result.getProperty(GEOCELL);
			
/*			Iterator<hotItem> iterator = hotTable.iterator();
			while(iterator.hasNext()){
				hotItem hotitem = iterator.next();
				if(hotitem.getGeocell() == topCell){
					int index_tmp = hotTable.indexOf(hotitem);
					hotTable.remove(index_tmp);
				}
			}*/
			for(hotItem a : hotTable){
				if(a.getGeocell() == topCell){
					int index_tmp = hotTable.indexOf(a);
					hotTable.remove(index_tmp);
				}
			}
			datastore.delete(result.getKey());
		}
		return true;
	}

	@GET
	@Path("/coolplace")
	@Produces(MediaType.APPLICATION_JSON)
	public Response makeCoolPlace() throws IOException {

		if (addToCoolDB() == true)
			return Response.status(HttpServletResponse.SC_OK).build();
		return Response.status(HttpServletResponse.SC_ACCEPTED).build();
	}

	public static void puTCoolTable() throws ParseException{
		if(!coolTable.isEmpty())
			coolTable.clear();
		Query hotQuery = new Query("COOLPLACE");
		PreparedQuery hotQeury_pq = datastore.prepare(hotQuery);
		for (Entity result : hotQeury_pq.asIterable()) 
		{
			String hotCell = (String) result.getProperty("CoolCell");
			Long temp_ = (Long) result.getProperty("Count");
			int count = temp_.intValue();
			if(count >= 10)
			{
				if(coolTable.size() > COOLTABLESIZE)
					break;
				
				HotAndCoolCron a = new HotAndCoolCron();
				coolCounter temp = a.new coolCounter(hotCell, count);
				//System.out.println("puTCoolTabel() : " + temp.getGeocell() + " " + temp.getCount());
				if(isExit(temp , coolTable))
					continue;
				coolTable.put(hotCell, temp);
			}
		}
	}

	public static boolean isExit(coolCounter temp , Map<String, coolCounter> cooltable){
		if(cooltable.containsKey(temp.getGeocell()))
			return true;
		return false;
	}

	private boolean addToCoolDB() {

		System.out.println("addToCoolDB()");
		boolean isExist = false;
		List<Counter> counters = EventQueue.get().getCoolCounter();

		if (counters == null) 
			return false;

		for (Counter counter : counters) {

			isExist = false;

			Filter filter = new FilterPredicate(COOLCELL, FilterOperator.EQUAL,
					counter.getGeocell());
			Query q = new Query(COOLPLACE).setFilter(filter);
			PreparedQuery pq = datastore.prepare(q);

			for (Entity result : pq.asIterable()) {
				isExist = true;

				Long temp = (Long) result.getProperty(COOLCOUNT);
				int count = temp.intValue();
				result.setProperty(COOLCOUNT, count + counter.getCount());
				datastore.put(result);

				coolCounter a = coolTable.get(GEOCELL);
				a.increase(count);
				coolTable.remove(GEOCELL);
				coolTable.put(GEOCELL, a);
			}

			if (isExist == false) {
				Entity entity = new Entity(COOLPLACE);
				entity.setUnindexedProperty(COOLCELL, counter.getGeocell());
				entity.setProperty(COOLCOUNT, counter.getCount());
				datastore.put(entity);
				coolCounter a = new coolCounter(GEOCELL, counter.getCount());
				coolTable.put(GEOCELL, a);
			}
		}
		EventQueue.get().CoolCounterClear();
		return true;
	}
}
