package ssomap.cron;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ssomap.BlobStore.BlobStore;
import ssomap.hotQueue.EventQueue;


@Path("/clean")
public class CleanBlobStore {
	@GET
	@Path("/blobstore")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clear() throws IOException {
		
		BlobStore.get().switchBlob();
		return Response.status(HttpServletResponse.SC_OK).build();
	}
}
