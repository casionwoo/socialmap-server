package ssomap.TotalPhotos;

import java.io.UnsupportedEncodingException;

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
import javax.ws.rs.core.UriInfo;

import ssomap.Authenication.Authenication;
import ssomap.HoTpLace.HotPlace;
import ssomap.Photos.PhotosDao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Path("/totalphotos")
public class TotalPhotos {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;
	@Context
	UriInfo info;

	private Authenication auth = new Authenication();
	private PhotosDao dao = new PhotosDao();

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTotalPhotos(@QueryParam("latS") double latS,
			@QueryParam("latN") double latN, @QueryParam("lonW") double lonW,
			@QueryParam("lonE") double lonE,
			@QueryParam("Picture_ID") String PictureID)
			throws EntityNotFoundException, UnsupportedEncodingException {

		if (auth.getAuthentication() == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();

		if (PictureID == null) {
			System.out.println("Picture_ID == null");
			return Response.status(200).entity(dao.get(latS, latN, lonE, lonW))
					.build();
		} else {
			System.out.println("Picture_ID != null");
			return Response.status(200)
					.entity(dao.get(latS, latN, lonE, lonW, PictureID)).build();
		}

	}
}
