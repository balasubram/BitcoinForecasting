package com.bala.bitcoin.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bala.bitcoin.webserver.CryptoCurrencyService;
import com.bala.bitcoin.webserver.MovingPriceResponseModel;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MovingAveragesServlet extends HttpServlet {

	enum PriceMovementTypeParam {
		MONTH("month"),
		WEEK("week"),
		DATE("date"),
		NONE("NONE");

		private String parameterType;

		PriceMovementTypeParam(String parameterType) {
			this.parameterType = parameterType;
		}

		public static PriceMovementTypeParam getParameter(String param) {
			for (PriceMovementTypeParam parameterType : PriceMovementTypeParam.values()) {
				if (parameterType.parameterType.equals(param)) {
					return parameterType;
				}
			}
			return NONE;
		}

	}

	private Gson gson = new Gson();

	private static final long serialVersionUID = -2329068105061073783L;

	private static final Logger LOG = LoggerFactory.getLogger(MovingAveragesServlet.class);

	@Inject
	private CryptoCurrencyService cryptoCurrencyService;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("Request received to process MovingAverage for Bitcoin");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String movingAverageDays = request.getParameter("movingAverageDays");
		int movingDays = -1;
		try {
			movingDays = Integer.parseInt(movingAverageDays);
		} catch (NumberFormatException nfe) {

		}
		List<MovingPriceResponseModel> movingAverageResponse = cryptoCurrencyService.getMovingAverage(startDate, endDate, movingDays);
		response.getOutputStream().write(gson.toJson(movingAverageResponse).getBytes());

	}

}