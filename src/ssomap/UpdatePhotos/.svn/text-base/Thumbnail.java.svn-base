package ssomap.UpdatePhotos;

import java.io.IOException;

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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;

//import 

@Path("/thumbnail")
public class Thumbnail {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;
	
	private Authenication auth = new Authenication();
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	public final String USER = "Users";
	public final String OWNERID = "ownerId";
	public final String PHOTO = "Photos";
	public final String OWNER_THUMB_URL = "ownerThumURL";
	private User user = null;

	@GET
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response PostPhotos(@QueryParam("ownerID") String ownerId,
			@QueryParam("ownerThumURL") String ownerThumURL)
			throws IOException, EntityNotFoundException {

		/*
		 * if((user=auth.getAuthentication()) == null) return
		 * Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
		 */

		if (UpdateThumbnail(ownerId, ownerThumURL) == true)
			return Response.status(HttpServletResponse.SC_OK).build();
		return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
	}

	private boolean UpdateThumbnail(String id, String thumbURL) {
		Filter filter = new FilterPredicate(OWNERID, FilterOperator.EQUAL, id);
		Query q = new Query(PHOTO).setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {
			result.setProperty(OWNER_THUMB_URL, thumbURL);
			datastore.put(result);
		}
		return true;
	}

}
