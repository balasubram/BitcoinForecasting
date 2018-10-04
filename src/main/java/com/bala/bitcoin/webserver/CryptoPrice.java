package com.bala.bitcoin.webserver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CryptoPrice {

	private Double price;

	private Date date;


	public CryptoPrice(BigDecimal price, LocalDate date) {
		this.price = price.doubleValue();
		this.date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}