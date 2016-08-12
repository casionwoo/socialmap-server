package ssomap.Clustering;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ssomap.Photos.PhotosDao;
import ssomap.cron.HotAndCoolCron;
import ssomap.cron.HotAndCoolCron.coolCounter;
import ssomap.cron.HotAndCoolCron.hotItem;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

public class GridBasedClustring {
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	private List<String> cells = null;
	private List<String> list = null;
	private final String GEOCELL = "geocell";
	private final String ENTITY = "Photos";
	private final String MAKETIMES = "makeTimeS";
	private PhotosDao dao = new PhotosDao();

	public GridBasedClustring() {
	}
	
	public GridBasedClustring(List<String> cells) {

		super();
		this.cells = cells;

	}
	public List<String> getCells() {
		return cells;
	}

	public void setCells(List<String> cells) {
		this.cells = cells;
	}
	private static GridBasedClustring self = null;

	public static GridBasedClustring get() {
		if (self == null) {
			self = new GridBasedClustring();
		}
		return self;
	}

	private String[] makeStringArray(String cell){
		String[] cells_below = { cell + "1", cell + "2", cell + "3",
				cell + "4", cell + "5", cell + "6", cell + "7", cell + "8",
				cell + "9", cell + "a", cell + "b", cell + "c", cell + "d",
				cell + "e", cell + "f" };
		return cells_below; 
	}
	
	@SuppressWarnings("unused")
	private List<String> StrArrayToList(String[] strArray){
		List<String> geocell = new ArrayList<String>();
		for(String cell : strArray){
			geocell.add(cell);
		}
		return geocell;
	}
	
	private boolean isInHotCell(String cell){
		List<hotItem> hotTable = HotAndCoolCron.getHotTable();
		for(hotItem keys : hotTable)
		{
			String hotCell = keys.getGeocell();
			if(hotCell == null)
				continue;
			if(hotCell.contains(cell) || cell.contains(hotCell))
				return true;
		}
		return false;
	}
	private boolean isInCoolCell(String cell){
		Map<String, coolCounter> coolTable = HotAndCoolCron.getCoolTable();
		Iterator<String> keys = coolTable.keySet().iterator();
		while(keys.hasNext()){
			String coolCell = keys.next();
			if(coolCell == null)
				continue;
			if(coolCell.contains(cell) || cell.contains(coolCell))
				return true;
		}
		return false;
	}
	
	
	public JsonObject Clustring() throws UnsupportedEncodingException {
		JsonArray photos = new JsonArray();
		for (String cell : cells) {
			if(cell.length() <= 5){
				String[] cells_below = makeStringArray(cell);
				for (String cell_below : cells_below) {
					JsonObject json = FindPhoto(cell_below);
					if (json != null)
						photos.put(json);
				}
			}
			else{
				JsonObject json = FindPhoto(cell);
				if (json != null)
					photos.put(json);
			}
		}
		JsonObject outer = new JsonObject();
		outer.put("data", photos);
		
		return outer;
	}

	@SuppressWarnings("deprecation")
	private JsonObject FindPhoto(String cell_below) throws UnsupportedEncodingException {
		
		JsonObject photo = null;
		list = Arrays.asList(cell_below);
		boolean isHotCell = isInHotCell(cell_below);
		boolean isCoolCell = isInCoolCell(cell_below);
		Query photoquery = new Query(ENTITY).addFilter(GEOCELL, FilterOperator.IN,
				list).addSort(MAKETIMES, SortDirection.DESCENDING);
		
		List<Entity> entities = datastore.prepare(photoquery).asList(
				FetchOptions.Builder.withLimit(1));
		if(entities.isEmpty())
			return null;
		for (Entity entity : entities) {
			
			photo = dao.getJsonObject(entity , isHotCell , isCoolCell);
			System.out.println("isHotCell : " + isHotCell);
			System.out.println("isCoolCell : " + isCoolCell);
		}
		return photo;
	}
}
