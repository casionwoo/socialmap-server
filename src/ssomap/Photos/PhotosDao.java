package ssomap.Photos;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ssomap.Clustering.GridBasedClustring;
import ssomap.cron.HotAndCoolCron;
import ssomap.cron.HotAndCoolCron.coolCounter;
import ssomap.cron.HotAndCoolCron.hotItem;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

public class PhotosDao {

	public final int ENTITY_SIZE = 30;
	public static final String ENTITY = "Photos";
	public final String ENTITY_HOTPLACE = "HotPlace_Photo";
	public final String PICTUREID = "pictureID";
	public final String MAKETIMES = "makeTimeS";
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	private static final String GEOCELL = "geocell";
	private FetchOptions fetchOptions = FetchOptions.Builder
			.withLimit(ENTITY_SIZE);

	public String get(double latS, double latN, double lonE, double lonW)
			throws UnsupportedEncodingException {
		BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);
		List<String> cells = getCells(bb);
		String outer = new String(getClusteredPhoto(cells).toString()
				.getBytes(), "EUC-KR");
		return outer;
	}

	private boolean isInHotCell(String cell) {
		List<hotItem> hotTable = HotAndCoolCron.getHotTable();
		for (hotItem keys : hotTable) {
			String hotCell = keys.getGeocell();
			if (hotCell == null)
				continue;
			if (hotCell.contains(cell))
				return true;
		}
		return false;
	}

	private boolean isInCoolCell(String cell) {
		Map<String, coolCounter> coolTable = HotAndCoolCron.getCoolTable();
		Iterator<String> keys = coolTable.keySet().iterator();
		while (keys.hasNext()) {
			String coolCell = keys.next();
			if (coolCell == null)
				continue;
			if (coolCell.contains(cell))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String get(double latS, double latN, double lonE, double lonW,
			String PictureID) throws EntityNotFoundException,
			UnsupportedEncodingException {

		BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);
		List<String> cells = getCells(bb);

		System.out.println(cells);
		int sizeOfCell = 0;
		System.out.println("sizeOfCell : " + sizeOfCell);

		if (cells.get(0).length() <= 5)
			sizeOfCell = cells.get(0).length();
		else
			sizeOfCell = cells.get(0).length() - 1;

		@SuppressWarnings("deprecation")
		Query q = new Query(ENTITY).addFilter(PICTUREID, FilterOperator.EQUAL,
				PictureID);
		PreparedQuery pq = datastore.prepare(q);
		List<String> list = null;
		for (Entity entity : pq.asIterable()) {
			list = (List<String>) entity.getProperty(GEOCELL);
			System.out.println(entity.getProperty(GEOCELL).toString());
		}

		String geocell = list.get(sizeOfCell);
		Filter filter = new FilterPredicate(GEOCELL, FilterOperator.IN,
				convertToList(geocell));
		Query query = new Query(ENTITY).setFilter(filter).addSort(MAKETIMES,
				SortDirection.DESCENDING);
		List<Entity> entities = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(20));

		JsonArray outer = new JsonArray();

		List<String> listJson = new ArrayList<String>();
		for (Entity entity1 : entities) {

			String pictureid = (String) entity1.getProperty(PICTUREID);
			if (listJson.contains(pictureid))
				continue;
			listJson.add(pictureid);
			JsonObject picture = getJsonObject(entity1, isInHotCell(geocell),
					isInCoolCell(geocell));
			if (picture == null)
				continue;

			outer.put(picture);
		}
		JsonObject json = new JsonObject();
		json.put("data", outer);
		String out = new String(json.toString().getBytes(), "EUC-KR");

		return out;
	}

	public JsonObject getJsonObject(Entity entity1, boolean isHotCell,
			boolean isCoolCell) throws UnsupportedEncodingException {
		JsonObject picture = new JsonObject();

		picture.put("pictureID", (String) entity1.getProperty("pictureID"));
		picture.put("latitude", (Double) entity1.getProperty("latitude"));
		picture.put("longitude", (Double) entity1.getProperty("longitude"));
		picture.put("commentURL", (String) entity1.getProperty("commentURL"));
		picture.put("imageURL", (String) entity1.getProperty("imageURL"));
		picture.put("smallImageURL",
				(String) entity1.getProperty("smallImageURL"));
		picture.put("makeTimeS", (String) entity1.getProperty("makeTimeS"));
		picture.put("record", (String) entity1.getProperty("record"));
		picture.put("ownerName", (String) entity1.getProperty("ownerName"));
		picture.put("ownerID", (String) entity1.getProperty("ownerId"));
		picture.put("ownerThumURL",
				(String) entity1.getProperty("ownerThumURL"));
		picture.put("placeName", (String) entity1.getProperty("placeName"));

		if (isHotCell == false && isCoolCell == false)
			picture.put("isPublic", 6);

		else if (isHotCell == true && isCoolCell == false)
			picture.put("isPublic", 7);

		else if (isHotCell == false && isCoolCell == true)
			picture.put("isPublic", 8);

		else if (isHotCell == true && isCoolCell == true)
			picture.put("isPublic", 9);

		return picture;
	}

	public JsonObject getJsonObject(Entity entity1)
			throws UnsupportedEncodingException {
		JsonObject picture = new JsonObject();

		picture.put("pictureID", (String) entity1.getProperty("pictureID"));
		picture.put("latitude", (Double) entity1.getProperty("latitude"));
		picture.put("longitude", (Double) entity1.getProperty("longitude"));
		picture.put("commentURL", (String) entity1.getProperty("commentURL"));
		picture.put("imageURL", (String) entity1.getProperty("imageURL"));
		picture.put("smallImageURL",
				(String) entity1.getProperty("smallImageURL"));
		picture.put("makeTimeS", (String) entity1.getProperty("makeTimeS"));
		picture.put("record", (String) entity1.getProperty("record"));
		picture.put("ownerName", (String) entity1.getProperty("ownerName"));
		picture.put("ownerID", (String) entity1.getProperty("ownerId"));
		picture.put("ownerThumURL",
				(String) entity1.getProperty("ownerThumURL"));
		picture.put("placeName", (String) entity1.getProperty("placeName"));
		return picture;
	}

	@SuppressWarnings("unused")
	private List<String> convertToList(Photos photo) {
		List<String> geocell = new ArrayList<String>();
		geocell.add(photo.getGeocell().get(9));

		return geocell;
	}

	private List<String> convertToList(String val) {
		List<String> geocell = new ArrayList<String>();
		geocell.add(val);

		return geocell;
	}

	String getClusteredPhoto(List<String> cells)
			throws UnsupportedEncodingException {
		GridBasedClustring.get().setCells(cells);
		return GridBasedClustring.get().Clustring().toString();
	}

	List<String> getCells(BoundingBox bb) {
		List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);
		System.out.println("cells size : " + cells.size());
		return cells;
	}

	@SuppressWarnings("deprecation")
	public Photos getPhotos(String Picture_ID) throws EntityNotFoundException {
		Query q = new Query(ENTITY).addFilter(PICTUREID, FilterOperator.EQUAL,
				Picture_ID);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {
			return get(result);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Photos get(Entity e) throws EntityNotFoundException {

		Photos c = new Photos();
		c.setPlaceName((String) e.getProperty("placeName"));
		c.setPictureID((String) e.getProperty("pictureID"));
		c.setLatitude((Double) e.getProperty("latitude"));
		c.setLongitude((Double) e.getProperty("longitude"));
		c.setCommentURL((String) e.getProperty("commentURL"));
		c.setImageURL((String) e.getProperty("imageURL"));
		c.setSmallImageURL((String) e.getProperty("smallImageURL"));
		c.setMakeTimeS((String) e.getProperty("makeTimeS"));
		c.setRecord((String) e.getProperty("record"));
		c.setOwnerName((String) e.getProperty("ownerName"));
		c.setIsPublic((String) e.getProperty("isPublic"));
		c.setOwnerThumURL((String) e.getProperty("ownerThumURL"));
		c.setGeocell((List<String>) e.getProperty("geocell"));

		return c;
	}

	public Photos get(long id) throws EntityNotFoundException {
		Entity e = DatastoreServiceFactory.getDatastoreService().get(
				KeyFactory.createKey(ENTITY, id));

		return get(e);
	}

	public boolean create(List<Photos> photos) {

		List<Entity> entities = new ArrayList<Entity>();
		for (Photos request : photos) {
			Entity newer = new Entity(ENTITY);

			newer.setProperty("makeTimeS", request.getMakeTimeS());
			newer.setProperty("ownerId", request.getOwnerId());
			newer.setProperty("pictureID", request.getPictureID());
			newer.setProperty("geocell", request.getGeocell());

			newer.setUnindexedProperty("latitude", request.getLatitude());
			newer.setUnindexedProperty("longitude", request.getLongitude());
			newer.setUnindexedProperty("commentURL", request.getCommentURL());
			newer.setUnindexedProperty("imageURL", request.getImageURL());
			newer.setUnindexedProperty("smallImageURL",
					request.getSmallImageURL());
			newer.setUnindexedProperty("record", request.getRecord());
			newer.setUnindexedProperty("ownerName", request.getOwnerName());
			newer.setUnindexedProperty("ownerThumURL",
					request.getOwnerThumURL());
			newer.setUnindexedProperty("placeName", request.getPlaceName());
			entities.add(newer);
		}
		if (!entities.isEmpty())
			DatastoreServiceFactory.getDatastoreService().put(entities);
		return true;
	}
}
