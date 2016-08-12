package ssomap.CoolPlace;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ssomap.Authenication.Authenication;
import ssomap.Photos.PhotosDao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@Path("/coolplace")
public class CoolPlace {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;

	private final int FIRST = 0;
	private final String GEOCELL = "geocell";
	private final String PHOTOS = "Photos";
	private final String COOLPLACE = "COOLPLACE";
	private final String COOLCOUNT = "Count";
	private final String COOLCELL = "CoolCell";
	private final int SIZEOFPHOTO = 5;
	private PhotosDao dao = new PhotosDao();
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	private Authenication auth = new Authenication();
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCoolPlace() throws EntityNotFoundException,
			UnsupportedEncodingException {

		if (auth.getAuthentication() == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
		
		List<String> cellList = getCoolCells();
		JsonObject data = getCools(FIRST, cellList);
		if(data == null)
			return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
		String outer = new String(data.toString().getBytes(), "EUC-KR");
		return Response.status(HttpServletResponse.SC_OK).entity(outer).build();
	}

	@GET
	@Path("/reget")
	@Produces(MediaType.APPLICATION_JSON)
	public Response regetCoolPlace(@QueryParam("location") String location,
			@QueryParam("index") int index)
			throws UnsupportedEncodingException, EntityNotFoundException {

		List<String> cellList = strToList(location);
		JsonObject data = getCools(index, cellList);
		String outer = new String(data.toString().getBytes(), "EUC-KR");
		if (data == null)
			return Response.status(HttpServletResponse.SC_NO_CONTENT).build();

		return Response.status(HttpServletResponse.SC_OK).entity(outer).build();
	}

	private JsonObject getCools(int index, List<String> coolCellList)
			throws EntityNotFoundException, UnsupportedEncodingException {

		List<String> coolCells = coolCellList;
		
		if (coolCells.isEmpty())
			return null;
		
		JsonArray jsonArray = null;
		JsonArray outer = new JsonArray();
		boolean isEmpty = true;
		for (String coolcell : coolCells) {
			
			isEmpty = true;
			jsonArray = new JsonArray();
			Filter filter = new FilterPredicate(GEOCELL, FilterOperator.IN,
					strToList(coolcell));
			Query q = new Query(PHOTOS).setFilter(filter);
			PreparedQuery pq = datastore.prepare(q);

			for (Entity result : pq.asList(FetchOptions.Builder.withOffset(
					index).limit(SIZEOFPHOTO))) {
				jsonArray.put(dao.getJsonObject(result));
				isEmpty = false;
			}
			if(isEmpty == true)
				continue;
			JsonObject place = new JsonObject();
			place.put("photos", jsonArray);
			place.put("location", coolcell);
			outer.put(place);
		}
		JsonObject data = new JsonObject();
		data.put("data", outer);
		return data;
	}

	private List<String> strToList(String str) {
		List<String> list = new ArrayList<String>();
		list.add(str);
		return list;
	}

	private List<String> getCoolCells() {
		List<String> outer = new ArrayList<String>();

		Filter moreThanCount = new FilterPredicate(COOLCOUNT,
				FilterOperator.GREATER_THAN_OR_EQUAL, 8);
		Query q = new Query(COOLPLACE).setFilter(moreThanCount);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			String coolCell = (String) result.getProperty(COOLCELL);
			if(!outer.contains(coolCell))
				outer.add(coolCell);
		}
		return outer;
	}
}
