package ssomap.Notice;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.restfb.json.JsonObject;

public class Notice {

	private String Subject = null;
	private String Content = null;

	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private final String NOTICE = "Notice";
	private final String CONTENT = "Content";
	private final String SUBJECT = "Subject";

	private static Notice self = null;

	public static Notice get() {
		if (self == null)
			return self = new Notice();
		else
			return self;
	}

	public Response getNotice() {
		JsonObject outer = new JsonObject();
		if (Content == null|| Subject == null) {
			Entity entity = getNoticeFromDB();
			Content = (String) entity.getProperty(CONTENT);
			Subject = (String) entity.getProperty(SUBJECT);
		}
		JsonObject json = new JsonObject();
		json.put(CONTENT, Content);
		json.put(SUBJECT, Subject);
		outer.put("data", json);
		return Response.status(HttpServletResponse.SC_OK)
				.entity(outer.toString()).build();
	}

	public Response setNotice(String subject, String content)
			throws UnsupportedEncodingException {
		Subject = subject;
		Content = content;
		setNoticeToDB(subject, content);
		String out = new String(String.format("SUBJECT : %s\nCONTENT : %s",
				new String(subject.getBytes(), "UTF-8"),
				new String(content.getBytes(), "UTF-8").toString().getBytes(),
				"UTF-8"));
		return Response.status(200).entity(out.toString()).build();
	}

	private void setNoticeToDB(String subject, String content) {
		Query query = new Query(NOTICE);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			entity.setProperty(SUBJECT, subject);
			entity.setProperty(CONTENT, content);
			datastore.put(entity);
			return;
		}

		Entity entity = new Entity(NOTICE);
		entity.setProperty(SUBJECT, subject);
		entity.setProperty(CONTENT, content);
		datastore.put(entity);
	}

	private Entity getNoticeFromDB() {
		Query query = new Query(NOTICE);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()) {
			return entity;
		}
		return null;
	}
}
