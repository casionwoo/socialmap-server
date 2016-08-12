package ssomap.Notice;

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

@Path("/notice")
public class NoticeOuter {
	
	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNotice(){
		return Notice.get().getNotice();
	}
	
	@GET
	@Path("/set")
	public Response setNotice(@QueryParam("subject") String subject, 
							@QueryParam("content") String content) throws UnsupportedEncodingException {
		return Notice.get().setNotice(subject , content);
	}
}
