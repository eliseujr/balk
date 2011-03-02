package com.balk.bovespatracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import java.net.URL;
import com.balk.bovespatracker.StockDataHelper;
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
		
		mainTableLayout.addView(stockRow);
      
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "Inicio BovespaTracker");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        LayoutInflater inflater = getLayoutInflater();
        
        StockData stock1 = new StockData("IBOV", "( Indice IBovespa )", "60570", "+1,25%");
        
        addStock(inflater, stock1);
        addStock(inflater, stock1);
                
        
        try {
        	//textView.setText("Inicio HTTP Connection");
        	
        	URL url = StockDataHelper.createYQLUrl("MMI");
        	
        	if (url != null) {
        		String symbol = StockDataHelper.parseYQLData(StockDataHelper.getUrlContent(url));
        		Log.i(TAG, "symbol from YQL = " + symbol);
                //textView.setText(symbol);
            }
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        
    }

}

