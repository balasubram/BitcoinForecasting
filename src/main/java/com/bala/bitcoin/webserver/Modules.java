package com.bala.bitcoin.webserver;

import com.bala.bitcoin.servlets.ForecastingServlet;
import com.bala.bitcoin.servlets.MovingAveragesServlet;
import com.bala.bitcoin.servlets.PriceMovementServlet;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class Modules extends AbstractModule {

	public Modules() {

	}

	@Override
	protected void configure() {
		bind(SparkMLService.class).in(Singleton.class);
		bind(CryptoCurrencyService.class).in(Singleton.class);
		bind(PriceMovementServlet.class).in(Singleton.class);
		bind(ForecastingServlet.class).in(Singleton.class);
		bind(MovingAveragesServlet.class).in(Singleton.class);
		bind(ApplicationWebServer.class).in(Singleton.class);
		
	}

}