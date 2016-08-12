package ssomap.Facebook;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import ssomap.Authenication.Authenication;
import ssomap.Photos.Photos;
import ssomap.Photos.PhotosDao;
import ssomap.hotQueue.EventQueue;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

//import 

@Path("/facebook")
public class FacebookPhotos {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;

	private Authenication auth = new Authenication();
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	private PhotosDao dao = new PhotosDao();
	public final String USER = "Users";
	public final String PHOTOID = "pictureID";
	public final String PHOTO = "Photos";
	@POST
	@Path("/postphotos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response PostPhotos(@QueryParam("isFirst") boolean isFirst)
			throws IOException, EntityNotFoundException {

/*		if ((auth.getAuthentication()) == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();*/

		String contents = getContents();
		JsonObject json = new JsonObject(contents);
		
		if (isFirst == false) {
			posting(json);
			return Response.status(HttpServletResponse.SC_OK).build();
		} else {
			FirstPosting(json);
			return Response.status(HttpServletResponse.SC_OK).build();
		}
	}

	private void FirstPosting(JsonObject photos) {
		// TODO Auto-generated method stub
		double lat = 0.0;
		double lon = 0.0;
		List<Photos> list = new ArrayList<Photos>();
		JsonArray json = photos.getJsonArray("data"); 
		for (int i = 0; i < json.length(); i++) {
			JsonObject photoJson = json.getJsonObject(i);

			lat = photoJson.getDouble("latitude");
			lon = photoJson.getDouble("longitude");
			Point p = new Point(lat, lon);
			List<String> cells = GeocellManager.generateGeoCell(p);
			if (i == 0) {
				updateTime(photoJson.getString("makeTimeS"));
			}

			// setting for photo class members
			Photos photo = new Photos();
			photo.setGeocell(cells);
			photo.setLatitude(lat);
			photo.setLongitude(lon);
			photo.setMakeTimeS(photoJson.getString("makeTimeS"));
			photo.setCommentURL(photoJson.getString("commentURL"));
			photo.setImageURL(photoJson.getString("imageURL"));
			photo.setSmallImageURL(photoJson.getString("smallImageURL"));
			photo.setPictureID(photoJson.getString("pictureID"));
			photo.setRecord(photoJson.getString("record"));
			photo.setOwnerName(photoJson.getString("ownerName"));
			photo.setOwnerId(photoJson.getString("ownerID"));
			photo.setOwnerThumURL(photoJson.getString("ownerThumURL"));
			photo.setPlaceName(photoJson.getString("placeName"));
			// setting for photo class members

			if (!isExistPhoto(photo)) {
				//dao.create(photo);
				list.add(photo);
				EventQueue.get().push(photo);
			}
			if(! list.isEmpty())
				dao.create(list);
		}
	}

	private boolean isExistPhoto(Photos photo) {

		Filter filter = new FilterPredicate(PHOTOID, FilterOperator.EQUAL,
				photo.getPictureID());
		Query q = new Query(PHOTO).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity entity : pq.asIterable()) {
			return true;
		}
		return false;
	}

	public String getContents() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream(), writer, "EUC-KR");
		return writer.toString();
	}

	private void updateTime(String updatetime) {
		Entity entity = new Entity(USER);
		
		entity.setProperty("last_update", updatetime);
		datastore.put(entity);
	}

	private void posting(JsonObject photos) throws EntityNotFoundException,
			IOException {

		double lat = 0.0;
		double lon = 0.0;
		List<Photos> list = new ArrayList<Photos>();
		JsonArray json = photos.getJsonArray("data"); 
		
		for (int i = 0; i < json.length(); i++) {
			JsonObject photoJson = json.getJsonObject(i);

			lat = photoJson.getDouble("latitude");
			lon = photoJson.getDouble("longitude");
			Point p = new Point(lat, lon);
			List<String> cells = GeocellManager.generateGeoCell(p);
/*			if (i == 0) {
				updateTime(photoJson.getString("makeTimeS"));
			}*/

			// setting for photo class members
			Photos photo = new Photos();

			photo.setGeocell(cells);
			photo.setLatitude(lat);
			photo.setLongitude(lon);
			photo.setMakeTimeS(photoJson.getString("makeTimeS"));
			photo.setCommentURL(photoJson.getString("commentURL"));
			photo.setImageURL(photoJson.getString("imageURL"));
			photo.setSmallImageURL(photoJson.getString("smallImageURL"));
			photo.setPictureID(photoJson.getString("pictureID"));
			photo.setRecord(photoJson.getString("record"));
			photo.setOwnerName(photoJson.getString("ownerName"));
			photo.setOwnerId(photoJson.getString("ownerID"));
			photo.setOwnerThumURL(photoJson.getString("ownerThumURL"));
			photo.setPlaceName(photoJson.getString("placeName"));
			// setting for photo class members

			list.add(photo);
			EventQueue.get().push(photo);
		}
		if(! list.isEmpty())
			dao.create(list);
	}
}
