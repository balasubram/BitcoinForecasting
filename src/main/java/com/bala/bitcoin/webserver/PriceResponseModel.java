package com.bala.bitcoin.webserver;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class PriceResponseModel {
	
	private BigDecimal price;

	private String date;
	
	
	private transient DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public PriceResponseModel(PriceModel priceModel) {
		this.price = priceModel.getPrice();
		this.date = priceModel.getEquivalentDate().format(dataFormatter);
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getDate() {
		return date;
	}

}