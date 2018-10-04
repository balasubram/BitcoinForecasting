package com.bala.bitcoin.webserver;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bala.bitcoin.servlets.ForecastingServlet;
import com.bala.bitcoin.servlets.MovingAveragesServlet;
import com.bala.bitcoin.servlets.PriceMovementServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationWebServer implements ApplicationInterface {

	private Server server;
	
	private Context context;

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationWebServer.class);
	
	@Inject
	PriceMovementServlet priceMovementServlet;
	
	@Inject
	ForecastingServlet forecastingServlet;
	
	@Inject 
	MovingAveragesServlet movingAveragesServlet;

	public ApplicationWebServer() {
		this.server = new Server();
		this.context = new Context(server, "/", Context.SESSIONS);
		context.setContextPath("/");
	    server.setHandler(context);
	}

	@Override
	public void stop() {
		if (server != null) {
			try {
				server.stop();
				server = null;
			} catch (Exception ex) {
				LOG.error("Exception while stopping HTTP Server {} .", ex);
			}
		}
	}

	@Override
	public void initializeServices() {
        Connector[] connectors = new Connector[1];
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setReuseAddress(true);
        connectors[0] = connector;
        connectors[0].setHost("0.0.0.0");
        connectors[0].setPort(8900);
        server.setConnectors(connectors);
        context.addServlet(new ServletHolder(priceMovementServlet), "/priceMovement");
        context.addServlet(new ServletHolder(forecastingServlet), "/forecasting");
        context.addServlet(new ServletHolder(movingAveragesServlet), "/movingAverage");

        
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			LOG.error("Exception occurred while starting the embedded jetty server, {}", e);
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}

	@Override
	public void startProcess() {
		// Empty Implementation
	}

}