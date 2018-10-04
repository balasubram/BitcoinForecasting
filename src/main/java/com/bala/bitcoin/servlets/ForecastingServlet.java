package com.bala.bitcoin.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bala.bitcoin.webserver.CryptoCurrencyService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ForecastingServlet extends HttpServlet {

	private static final long serialVersionUID = -2329068105061073783L;

	private static final Logger LOG = LoggerFactory.getLogger(ForecastingServlet.class);

	@Inject
	CryptoCurrencyService cryptoCurrencyService;

	private Gson gson = new Gson();

	public ForecastingServlet() {

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("Request received to predict forecasting for Bitcoin");
		double[] predictionValues = cryptoCurrencyService.getForecastingPrice();
		response.getOutputStream().write(gson.toJson(predictionValues).getBytes());
	}

}