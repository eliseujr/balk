package com.balk.bovespatracker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import java.net.URL;
import com.balk.bovespatracker.StockYQLHelper;
import com.balk.bovespatracker.StockData;

public class BovespaTracker extends Activity {

	private static final String TAG = "BovespaTracker";
	
	public void addStock(LayoutInflater inflater, StockData stockData) {
		
		TableLayout mainTableLayout = (TableLayout) findViewById(R.id.mainTableLayout);
		View stockRow = inflater.inflate(R.layout.stockrow, null);
		
		TextView stockNameView = (TextView) stockRow.findViewById(R.id.stockName);
		stockNameView.setText(stockData.getStockName());
		TextView stockSymbolView = (TextView) stockRow.findViewById(R.id.stockSymbol);
		stockSymbolView.setText(stockData.getStockSymbol());
		TextView stockPriceView = (TextView) stockRow.findViewById(R.id.stockPrice);
		stockPriceView.setText(stockData.getStockPrice());
		TextView stockVariationView = (TextView) stockRow.findViewById(R.id.stockVariation);
		stockVariationView.setText(stockData.getStockVariation());
		
		if(stockData.getStockVariation().startsWith("-")) {
			stockNameView.setTextColor(Color.RED);
			stockSymbolView.setTextColor(Color.RED);
			stockPriceView.setTextColor(Color.RED);
			stockVariationView.setTextColor(Color.RED);
		}
		
		mainTableLayout.addView(stockRow);
      
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "Inicio BovespaTracker");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        LayoutInflater inflater = getLayoutInflater();
        
        try {        	
        	URL url1 = StockYQLHelper.createYQLUrl("MMI");
        	URL url2 = StockYQLHelper.createYQLUrl("PETR4.SA");
        	URL url3 = StockYQLHelper.createYQLUrl("VALE5.SA");
        	
        	if (url1 != null && url2 != null && url3 != null) {
        		StockData stock1 = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url1));
        		StockData stock2 = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url2));
        		StockData stock3 = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url3));
        		
                addStock(inflater, stock1);
                addStock(inflater, stock2);
                addStock(inflater, stock3);        		
            }
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }        
        
    }
}

