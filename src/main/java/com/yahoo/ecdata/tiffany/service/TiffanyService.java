package com.yahoo.ecdata.tiffany.service;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@WebService
@Path("/")
@Consumes("application/json")
@Produces("application/json")
public interface TiffanyService {
	
	@POST
	@Path("{topicId}/write/")
	@Consumes("text/plain")
	public Response writeFromPost(
			@PathParam("topicId")String topicId, 
			@QueryParam("data")String data);
	
	@GET
	@Path("{topicId}/write/")
	@Consumes("text/plain")
	public Response writeFromGet(
			@PathParam("topicId")String topicId, 
			@QueryParam("data")String data);

	@GET
	@Path("{topicId}/read/")
	@Produces("text/plain")
	public Response read(
			@PathParam("topicId") String topicId, 
			@QueryParam("start") String start,
			@QueryParam("stop") String stop,
			@QueryParam("utc") String utc,
			@QueryParam("sessionId") String sessionId);
	
}
