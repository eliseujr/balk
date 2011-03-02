package com.balk.bovespatracker;

public class StockData {

	String mStockSymbol;
	String mStockName;
	String mStockPrice;
	String mStockVariation;
	
	public StockData(String stockSymbol, String stockName, String stockPrice, String stockVariation) {
		mStockSymbol = stockSymbol;
		mStockName = stockName;
		mStockPrice = stockPrice;
		mStockVariation = stockVariation;		
	}
	
	public String getStockSymbol() {
		return mStockSymbol;
	}
	
	public String getStockName() {
		return mStockName;
	}
	
	public String getStockPrice() {
		return mStockPrice;
	}
	
	public String getStockVariation() {
		return mStockVariation;
	}
	
	public void setStockSymbol(String stockSymbol) {
		mStockSymbol = stockSymbol;
	}
	
	public void setStockName(String stockName) {
		mStockSymbol = stockName;
	}
	
	public void setStockPrice(String stockPrice) {
		mStockSymbol = stockPrice;
	}
	
	public void setStockVariation(String stockVariation) {
		mStockSymbol = stockVariation;
	}
	
}
