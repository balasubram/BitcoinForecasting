package com.bala.bitcoin.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bala.bitcoin.webserver.CryptoCurrencyService;
import com.bala.bitcoin.webserver.PriceResponseModel;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PriceMovementServlet extends HttpServlet {

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

	private static final Logger LOG = LoggerFactory.getLogger(PriceMovementServlet.class);

	@Inject
	private CryptoCurrencyService cryptoCurrencyService;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("Request received to process PriceMovement for Bitcoin");
		String param = request.getParameter("type");
		PriceMovementTypeParam parameterType = PriceMovementTypeParam.getParameter(param);
		switch (parameterType) {
		case MONTH:
			List<PriceResponseModel> priceModelList = cryptoCurrencyService.getPriceMovementForLastMonth();
			response.getOutputStream().write(gson.toJson(priceModelList).getBytes());
			break;
		case WEEK:
			priceModelList = cryptoCurrencyService.getPriceMovementForLastWeek();
			response.getOutputStream().write(gson.toJson(priceModelList).getBytes());
			break;
		case DATE:
			String date = request.getParameter("date");
			PriceResponseModel priceModelResponse = cryptoCurrencyService.getPriceMovementForDate(date);
			response.getOutputStream().write(gson.toJson(priceModelResponse).getBytes());
			break;
		default:
			LOG.error("Invalid input");
			response.getOutputStream().write("Invalid input".getBytes());
			break;
		}

	}

}