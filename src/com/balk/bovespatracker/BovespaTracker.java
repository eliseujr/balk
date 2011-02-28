package com.balk.bovespatracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.net.URL;
import com.balk.bovespatracker.StockDataHelper;

public class BovespaTracker extends Activity {

	private static final String TAG = "BovespaTracker";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "Inicio BovespaTracker");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView textView = (TextView) findViewById(R.id.textView);
        
        try {
        	textView.setText("Inicio HTTP Connection");
        	
        	URL url = StockDataHelper.createYQLUrl("MMI");
        	
        	if (url != null) {
        		String symbol = StockDataHelper.parseYQLData(StockDataHelper.getUrlContent(url));
        		
                textView.setText(symbol);
            }
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }

}

