package ssomap.UpdatePhotos;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import ssomap.Authenication.Authenication;
import ssomap.Photos.PhotosDao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.restfb.json.JsonObject;

@Path("/delete")
public class DeletePhotos {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;

	private final String ENTITY = "Photos";
	private final String PHOTOID = "pictureID";
	private final String OWNERID = "ownerId";

	private Authenication auth = new Authenication();
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	private User user = null;

	@POST
	@Path("/photo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clear() throws IOException {

		if ((user = auth.getAuthentication()) == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();

		String contents = getContents();
		System.out.println(contents);
		JsonObject json = new JsonObject(contents);
		if (deletePhoto(json) == true)
			return Response.status(HttpServletResponse.SC_OK).build();
		else {
			System.err.println("Internal Server Error");
			return Response
					.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.build();
		}
	}

	public String getContents() throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream(), writer, "UTF-8");
		return writer.toString();
	}

	public boolean deletePhoto(JsonObject json) {
		for (int i = 0; i < json.getJsonArray("sdata").length(); i++) {
			Filter delete = new FilterPredicate(PHOTOID, FilterOperator.EQUAL,
					json.getJsonArray("sdata").get(i).toString());

			Query q = new Query(ENTITY).setFilter(delete);
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {
				datastore.delete(result.getKey());
			}
		}
		return true;
	}

	private boolean isExistPhoto(String ownerId) {
		Filter filter = new FilterPredicate(OWNERID, FilterOperator.EQUAL,
				ownerId);
		Query q = new Query(ENTITY).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity entity : pq.asIterable()) {
			return true;
		}
		return false;
	}

	@GET
	@Path("/photos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response DelPhotos(@QueryParam("ownerId") String ownerId)
			throws IOException {

		if ((user = auth.getAuthentication()) == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();

		System.out.println(ownerId);
		Filter filter = new FilterPredicate(OWNERID, FilterOperator.EQUAL,
				ownerId);
		Query q = new Query(ENTITY).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);

		for (Entity result : pq.asIterable()) {
			datastore.delete(result.getKey());
			System.out.println("in for loop");
		}

		if (isExistPhoto(ownerId) == true)
			return Response
					.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.build();

		return Response.status(HttpServletResponse.SC_OK).build();
	}
}
