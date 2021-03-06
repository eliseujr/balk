package com.balk.bovespatracker;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class StockYQLHelper {
	
    private static final String TAG = "StockYQLHelper";
    
   	private static final String BASE_YQL_URL_BEGINING = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22";
   	private static final String BASE_YQL_URL_END = ".SA%22)%0A%09%09&format=json&env=http%3A%2F%2Fdatatables.org%2Falltables.env&callback=";
    
    public static URL createYQLUrl(String stockSymbol) {
    	URL yqlUrl = null;
    	
    	Log.i(TAG, "Symbol = " + stockSymbol);
    	
    	try {
    		yqlUrl = new URL(BASE_YQL_URL_BEGINING + stockSymbol + BASE_YQL_URL_END);
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	}
    	
    	Log.i(TAG, "YQL URL = " + yqlUrl);
    		
    	return yqlUrl;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    protected static JSONObject getUrlContent(URL url) throws Exception {
    	URLConnection connection;
    	int responseCode;
    	JSONObject json = null;
    	int retry = 0;
    	
    	// Try at least 3 times to get stock data from Yahoo before failing
    	while(retry++ < 3) {
    		Log.i(TAG, "retry = " + retry);

        	connection = url.openConnection();
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	responseCode = httpConnection.getResponseCode();
        	
        	if (responseCode == HttpURLConnection.HTTP_OK) {    	    	  
        		InputStream inputStream = httpConnection.getInputStream();	
        		String result= convertStreamToString(inputStream);
        		json = new JSONObject(result);
        	}

        	// Could not get stock data from Yahoo
        	if(json == null) {
        		Log.e(TAG, "HttpURLConnection responseCode = " + responseCode);
        		Log.e(TAG, "Could not get content from " + url);
        	} 
        	// Data was successfully adquired from Yahoo, exiting retry loop
        	else {
        		break;
        	}
    		
    	}
    	
    	connection = url.openConnection();
    	HttpURLConnection httpConnection = (HttpURLConnection) connection;
    	responseCode = httpConnection.getResponseCode();
    	
    	if (responseCode == HttpURLConnection.HTTP_OK) {    	    	  
    		InputStream inputStream = httpConnection.getInputStream();	
    		String result= convertStreamToString(inputStream);
    		json = new JSONObject(result);
    	}

    	if(json == null) {
    		Log.e(TAG, "HttpURLConnection responseCode = " + responseCode);
    		Log.e(TAG, "Could not get content from " + url);
    	}
    	
    	return json;
    }
    
    public static String parseYQLData(JSONObject json) throws JSONException {
        StockData stockData = getStockDataFromYQL(json);
        
        if(stockData == null) {
    		return null;
    	}
        
        stockData.debugStockDataObj();
    	
    	return stockData.getStockSymbol();
    }
    
    public static StockData getStockDataFromYQL(JSONObject json) throws JSONException {
    	StockData stockData = new StockData();
    	
    	if(json == null) {    		
    		return null;
    	}
    	
    	//debugJSONObj(json);
    	
    	JSONObject stockYQL = json.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
        
    	// Check if stock info is valid
    	Log.i(TAG, "ErrorIndicationreturnedforsymbolchangedinvalid = " + stockYQL.getString("ErrorIndicationreturnedforsymbolchangedinvalid"));
    	if(stockYQL.getString("ErrorIndicationreturnedforsymbolchangedinvalid").contains("No such ticker symbol")) {
    		Log.e(TAG, "Stock data from YQL is invalid.");
    		return null;
    	} 
    	
		stockData.setStockName(stockYQL.getString("Name"));
		stockData.setStockPrice(stockYQL.getString("LastTradePriceOnly"));		
		stockData.setStockSymbol(stockYQL.getString("Symbol").substring(0, stockYQL.getString("Symbol").indexOf('.')));
		stockData.setStockVariation(stockYQL.getString("ChangeinPercent"));
        
        return stockData;
    }
    
    public static void debugJSONObj(JSONObject json) throws JSONException {
    	Log.i(TAG, json.toString());
    }
}