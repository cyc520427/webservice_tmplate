package com.yahoo.ecdata.tiffany.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("tiffanyService")
public class TiffanyServiceImpl implements TiffanyService {

	private static Logger LOG = Logger.getLogger(TiffanyServiceImpl.class);

	public TiffanyServiceImpl() {
		LOG.info("In Constructor.");
	}

	@Override
	public Response read(String topicId, String start, String stop, String utc, String sessionId) {

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				try {
				} catch (Exception e) {
				}
				;
			}
		};
		Response response = Response.ok(stream).status(200).build();
		return response;

	}

	@Override
	public Response writeFromPost(String topicId, String data) {
		return this.write(topicId, data);
	}

	@Override
	public Response writeFromGet(String topicId, String data) {
		return this.write(topicId, data);
	}
	
	private Response write(String topicId, String data) {
			LOG.info("Write data.");
			return Response.ok(data).build();
	}
	
}
