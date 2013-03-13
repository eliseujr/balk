package com.balk.bovespatracker;

import java.net.URL;
import android.util.Log;

public class StockData {
	
	private static final String TAG = "StockData";

	private String mStockSymbol;
	private String mStockName;
	private String mStockPrice;
	private String mStockVariation;
	
	public StockData() {}
	
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
		mStockName = "( " + stockName + " )";
	}
	
	public void setStockPrice(String stockPrice) {
		mStockPrice = stockPrice;
	}
	
	public void setStockVariation(String stockVariation) {
		mStockVariation = stockVariation;
	}
	
	public void debugStockDataObj( ) {
        Log.i(TAG, "-------------------------------");
        Log.i(TAG, "-------------------------------");
        Log.i(TAG, "Symbol = " + this.getStockSymbol());
        Log.i(TAG, "Name = " + this.getStockName());
        Log.i(TAG, "Price = " + this.getStockPrice());
        Log.i(TAG, "variation = " + this.getStockVariation());
        Log.i(TAG, "-------------------------------");
        Log.i(TAG, "-------------------------------");
	}

	public StockData getStockDataFromSymbol(String stockSymbol) throws Exception {

		URL url = StockYQLHelper.createYQLUrl(stockSymbol);
		StockData stockData = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url));

		return stockData;
	}
	
	public StockData[] getStockDataFromSymbols(String... stockSymbols) throws Exception {
		// Checking if stockSymbols is empty
		if (stockSymbols.length == 0)
			return null;
		
		int i = 0;
		StockData[] stockDataArray = new StockData[stockSymbols.length];
    
        for (String stockSymbol : stockSymbols) {
			URL url = StockYQLHelper.createYQLUrl(stockSymbol);

			if (url != null) {
				stockDataArray[i++] = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url));
	    	}
        }

        return stockDataArray;
	}
	
}
