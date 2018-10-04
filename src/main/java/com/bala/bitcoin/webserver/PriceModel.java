package com.bala.bitcoin.webserver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.annotations.SerializedName;

public class PriceModel {

	@SerializedName("price")
	private BigDecimal price;

	@SerializedName("time")
	private String time;

	private LocalDate date;

	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public PriceModel() {

	}

	public PriceModel(LocalDate date, BigDecimal price) {
		this.date = date;
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public LocalDate getEquivalentDate() {
		if (this.date == null) {
			this.date = LocalDate.parse(this.time, dateFormatter);
		}
		return this.date;
	}

}