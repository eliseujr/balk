package com.balk.bovespatracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.EditTextPreference;
import java.net.URL;

import org.json.JSONException;

import com.balk.bovespatracker.StockYQLHelper;
import com.balk.bovespatracker.StockData;

public class BovespaTracker extends Activity {

	private static final String TAG = "BovespaTracker";
	EditText mStockFromDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "Inicio BovespaTracker");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
            
        try {
        	addStockFromSymbol(new String[]{"MMI", "VALE5.SA", "PETR4.SA", "ITUB4.SA"});
        } catch (Exception e) {
        	e.printStackTrace();
        }    
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.add_stock:
            Log.i(TAG, "Clicked on menu Add stock");
            getStockNameDialog();
            return true;
        case R.id.refresh:
        	Log.i(TAG, "Clicked on menu Refresh");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void getStockNameDialog() {
        //Mapping UI
        View stockDialog = LayoutInflater.from(this).inflate(R.layout.stock_name_dialog, null);
        mStockFromDialog = (EditText) stockDialog.findViewById(R.id.stockEditText);

        // Creating and shows the new channel dialog
        Builder newStockDialog = new AlertDialog.Builder(this);
        newStockDialog.setTitle(getString(R.string.enter_stock_symbol))
            .setView(stockDialog)
            .setPositiveButton(getString(R.string.ok), saveNewStockListener )
            .setNegativeButton(getString(R.string.cancel),cancelNewStockListener )
            .show();
    }
    
    //Called when OK button is clicked on dialog
    DialogInterface.OnClickListener saveNewStockListener = new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String stockSymbol = mStockFromDialog.getText().toString();
    		Log.i(TAG, "onClick Save, stockSymbol = " + stockSymbol);
    		
    		try {
				addStockFromSymbol(stockSymbol);
		
				Toast.makeText(getApplicationContext(),
	    				"Added stock " + stockSymbol + " to watch list",
	    				Toast.LENGTH_LONG).show();
			
    		} catch (Exception e) {
				e.printStackTrace();
			}

    	}
    };

    //Called when Cancel button is clicked on dialog
    DialogInterface.OnClickListener cancelNewStockListener = new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		Log.i(TAG, "onClick Cancel" );
    	}
    };

	public void addStockFromSymbol(String... stockSymbols) throws Exception {
		// Checking if stockSymbols is empty
		if (stockSymbols.length == 0)
			return;
		
		LayoutInflater inflater = getLayoutInflater();
	    DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    
        for (String stockSymbol : stockSymbols) {
			URL url = StockYQLHelper.createYQLUrl(stockSymbol);

			if (url != null) {
				StockData stockData = StockYQLHelper.getStockDataFromYQL(StockYQLHelper.getUrlContent(url));
				addStockToLayout(inflater, stockData);
	            
	            dbHelper.saveStockRecord(db, stockData);
	    	}
        }
        
        db.close();
	}
	
	public void addStockToLayout(LayoutInflater inflater, StockData stockData) {
		
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
    
}
