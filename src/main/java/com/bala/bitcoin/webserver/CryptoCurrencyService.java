package com.bala.bitcoin.webserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CryptoCurrencyService implements ApplicationInterface {

	private static final Logger LOG = LoggerFactory.getLogger(CryptoCurrencyService.class);

	private CryptoData cryptoData;

	@Inject
	private SparkMLService sparkMlService;

	@Override
	public void initializeServices() {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet("https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=year");
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			JSONParser jsonParser = new JSONParser();
			Gson gson = new GsonBuilder().create();
			JSONObject jsonString = (JSONObject) jsonParser.parse(content);
			JSONObject json = (JSONObject) jsonString.get("data");
			this.cryptoData = gson.fromJson(json.toJSONString(), CryptoData.class);
			sparkMlService.doMachineLearning(cryptoData);
		} catch (Exception e) {
			LOG.error("Exception Occurred while initializing CryptoCurrency Service");
			throw new RuntimeException(e);
		}

	}

	@Override
	public void startProcess() {
		// Empty Initialization
	}

	@Override
	public void stop() {
		// Empty Initialization
	}

	public List<PriceResponseModel> getPriceMovementForLastMonth() {
		LocalDate localDateNow = LocalDate.now();
		LocalDate startDate = localDateNow.minusMonths(1);
		List<PriceModel> priceModelList = Arrays.asList(cryptoData.getPrices());
		List<PriceResponseModel> priceModelFilteredList = priceModelList.stream().filter(
				predicate -> predicate.getEquivalentDate().isAfter(startDate) && predicate.getEquivalentDate().isBefore(localDateNow))
				.map(mapper -> new PriceResponseModel(mapper)).collect(Collectors.toList());

		return priceModelFilteredList;
	}

	public List<PriceResponseModel> getPriceMovementForLastWeek() {
		LocalDate localDateNow = LocalDate.now();
		LocalDate startDate = localDateNow.minusWeeks(1);
		List<PriceModel> priceModelList = Arrays.asList(cryptoData.getPrices());
		List<PriceResponseModel> priceModelFilteredList = priceModelList.stream().filter(
				predicate -> predicate.getEquivalentDate().isAfter(startDate) && predicate.getEquivalentDate().isBefore(localDateNow))
				.map(mapper -> new PriceResponseModel(mapper)).collect(Collectors.toList());

		return priceModelFilteredList;
	}

	public PriceResponseModel getPriceMovementForDate(String date) {
		LocalDate localDate = date == null ? LocalDate.now() : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		if (localDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Invalid date. The given date is ");
		}
		List<PriceModel> priceModelList = Arrays.asList(cryptoData.getPrices());
		Optional<PriceResponseModel> priceModelOptinalValue = priceModelList.stream()
				.filter(predicate -> predicate.getEquivalentDate().isEqual(localDate)).map(mapper -> new PriceResponseModel(mapper))
				.findFirst();

		return priceModelOptinalValue.orElse(null);
	}

	public List<MovingPriceResponseModel> getMovingAverage(String startDate, String endDate, int movingAverageDays) {
		List<MovingPriceResponseModel> movingPriceResponseList = new ArrayList<>();
		List<PriceModel> priceModelList = new ArrayList<>();
		try {
			LocalDate startAvgDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate endAvgDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			if (startAvgDate.isAfter(endAvgDate)) {
				throw new IllegalArgumentException("Invalid input, Start Date is after end date");
			}

			priceModelList = Arrays.asList(cryptoData.getPrices()).stream()
					.filter(predicate -> (predicate.getEquivalentDate().equals(startAvgDate)
							|| predicate.getEquivalentDate().isAfter(startAvgDate))
							&& (predicate.getEquivalentDate().equals(endAvgDate)
									|| predicate.getEquivalentDate().isBefore(endAvgDate)))
					.sorted((c1, c2) -> c1.getEquivalentDate().compareTo(c2.getEquivalentDate())).collect(Collectors.toList());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date format ", e);
		}

		if (movingAverageDays == -1) {
			BigDecimal movingAverage = priceModelList.stream().map(f -> f.getPrice()).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
			movingPriceResponseList.add(new MovingPriceResponseModel(priceModelList.get(0).getEquivalentDate(),
					priceModelList.get(priceModelList.size() - 1).getEquivalentDate(),
					movingAverage.divide(BigDecimal.valueOf(priceModelList.size()))));
		} else {
			int i = 0;
			while (i <= priceModelList.size() - movingAverageDays) {
				BigDecimal movingAverage = BigDecimal.ZERO;
				LocalDate startDateAvg = null;
				LocalDate endDateAvg = null;
				for (int j = 0; j < movingAverageDays; j++) {
					PriceModel priceModel = priceModelList.get(i + j);
					if (j == 0) {
						startDateAvg = priceModel.getEquivalentDate();
					}
					if (j == movingAverageDays - 1) {
						endDateAvg = priceModel.getEquivalentDate();
					}
					movingAverage = movingAverage.add(priceModel.getPrice());
				}
				movingPriceResponseList.add(new MovingPriceResponseModel(startDateAvg, endDateAvg,
						movingAverage.divide(BigDecimal.valueOf(movingAverageDays), RoundingMode.HALF_UP)));
				i++;
			}
			BigDecimal movingAverage = BigDecimal.ZERO;
			LocalDate startDateAvg = null;
			LocalDate endDateAvg = null;
			for (int j = i; j < priceModelList.size(); j++) {
				PriceModel priceModel = priceModelList.get(j);
				if (j == i) {
					startDateAvg = priceModel.getEquivalentDate();
				}
				if (j == priceModelList.size() - 1) {
					endDateAvg = priceModel.getEquivalentDate();
				}
				movingAverage = movingAverage.add(priceModel.getPrice());
			}
			movingPriceResponseList.add(new MovingPriceResponseModel(startDateAvg, endDateAvg,
					movingAverage.divide(BigDecimal.valueOf(movingAverageDays), RoundingMode.HALF_UP)));
		}
		return movingPriceResponseList;
	}

	public double[] getForecastingPrice() {
		return sparkMlService.getForcastedPrice();
	}

	public void setSparkMlService(SparkMLService sparkMlservice) {
		this.sparkMlService = sparkMlservice;
	}

}