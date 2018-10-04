package com.bala.bitcoin.webserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import com.cloudera.sparkts.models.ARIMA;
import com.cloudera.sparkts.models.ARIMAModel;

public class SparkMLService implements ApplicationInterface {

	CryptoData cryptData;

	private ARIMAModel model;

	private SparkSession sparkSession;

	private Vector predicted;

	@Override
	public void initializeServices() {
		SparkConf sparkConf = new SparkConf();
		sparkConf.setMaster("local[2]");
		sparkSession = SparkSession.builder().appName("Machine Learning Model").config(sparkConf).enableHiveSupport().getOrCreate();
	}

	@Override
	public void startProcess() {
		// Empty Implementation
	}

	@Override
	public void stop() {
		sparkSession.stop();
	}

	public void doMachineLearning(CryptoData cryptoData) {

		List<PriceModel> priceModelList = Arrays.asList(cryptoData.getPrices());
		List<CryptoPrice> cryptoPriceList = priceModelList.stream()
				.map(mapper -> new CryptoPrice(mapper.getPrice(), mapper.getEquivalentDate())).collect(Collectors.toList());

		Dataset<CryptoPrice> cryptoPriceDF = sparkSession.createDataset(cryptoPriceList, Encoders.bean(CryptoPrice.class));

		List<Double> priceList = new ArrayList<>();

		cryptoPriceDF.select(new Column("price")).collectAsList().stream().forEach(action -> priceList.add(action.getDouble(0)));

		double[] trainingPrice = new double[priceList.size()];
		for (int i = 0; i < priceList.size(); i++) {
			trainingPrice[i] = priceList.get(i).doubleValue();
		}

		DenseVector actual = new DenseVector(trainingPrice);
		this.model = ARIMA.autoFit(actual, 1, 1, 1);
		this.predicted = model.forecast(actual, 15);

	}

	public double[] getForcastedPrice() {
		double[] values = predicted.toArray();
		return Arrays.copyOf(values, 15);
	}

}