package com.bala.bitcoin.webserver;

public class CryptoData {

	private String base;

	private String currency;

	private PriceModel[] prices;

	public CryptoData() {

	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public PriceModel[] getPrices() {
		return prices;
	}

	public void setPricest(PriceModel[] prices) {
		this.prices = prices;
	}

}