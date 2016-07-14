package com.yahoo.ecdata.tiffany;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.context.ContextLoaderListener;

import com.yahoo.ecdata.tiffany.util.ConfigConstants;
import com.yahoo.ecdata.tiffany.util.Configuration;

/**
 * Hello world!
 *
 */
public class TiffanyServer {
	
	 
	private static Logger LOG = Logger.getLogger(TiffanyServer.class);
	private static String HTTP_PORT = new Configuration().getProperty(ConfigConstants.HTTP_PORT);

	public static void main(String args[]) throws Exception {
		new TiffanyServer().start();
	}

	public void start() throws Exception {
		try {

			ServletContextHandler wsContext = new ServletContextHandler();
			wsContext.setContextPath("/ws");
			wsContext.setInitParameter("contextConfigLocation", "classpath*:**/*webServiceAppContext.xml");
			wsContext.addServlet(new ServletHolder(new CXFServlet()), "/*");
			wsContext.addEventListener(new ContextLoaderListener());
			
			WebAppContext healthCheckContext = new WebAppContext();
			healthCheckContext.setContextPath("/");
			healthCheckContext.setResourceBase("healthcheck");
			
			ContextHandlerCollection contexts = new ContextHandlerCollection();
			contexts.setHandlers(new Handler[] { healthCheckContext, wsContext });
			
			Server server = new Server();
			
			addConnector(server, HTTP_PORT);
			server.setHandler(contexts);
			server.setStopAtShutdown(true);
	        
			server.start();
			LOG.info("Tiffany Server Started");
		} catch (Exception e) {
			throw e;
		}
	}

	private static void addConnector(Server server, String port) {
		try {
			HttpConfiguration http_config = new HttpConfiguration();
			http_config.setSecureScheme("http");
			http_config.setSecurePort(Integer.parseInt(port));
			http_config.setOutputBufferSize(32768);
	
			ServerConnector http = new ServerConnector(server,
					new HttpConnectionFactory(http_config));
			http.setPort(Integer.parseInt(port));
			
			server.setConnectors(new Connector[] { http });
			
			LOG.info("Adding HttpConnector on port "+port);
		} catch (Exception e) {
			throw new RuntimeException("Error adding connector", e);
		}
	}
}
