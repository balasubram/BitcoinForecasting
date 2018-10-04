package com.bala.bitcoin.webserver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MovingPriceResponseModel {

	private String startDate;

	private String endDate;

	private BigDecimal movingPrice;

	private transient DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public MovingPriceResponseModel(LocalDate startDate, LocalDate endDate, BigDecimal movingPrice) {
		this.startDate = startDate.format(dataFormatter);
		this.endDate = endDate.format(dataFormatter);
		this.movingPrice = movingPrice;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getSEndDate() {
		return endDate;
	}

	public BigDecimal getDate() {
		return movingPrice;
	}

}