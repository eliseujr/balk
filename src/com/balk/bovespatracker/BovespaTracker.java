package com.balk.bovespatracker;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.balk.bovespatracker.StockData;
import com.balk.bovespatracker.DBHelper;

public class BovespaTracker extends ListActivity {

	private static final String TAG = "BovespaTracker";
	EditText mStockFromDialog;
	Context mContext;
	ActionMode mActionMode;
	SimpleAdapter mAdapter;
    static final ArrayList<HashMap<String,String>> stockList = new ArrayList<HashMap<String,String>>();
    private ListView listView = null;
    ActionMode.Callback mCallback;
    ActionMode mMode;
    String mLastUpdatedDate;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "Inicio BovespaTracker");
    	super.onCreate(savedInstanceState);
    	mContext = this;
    	
    	
        mAdapter = new SimpleAdapter(
        	this,
        	stockList,
        	R.layout.stockrow,
        	new String[] {"stockSymbol","stockPrice","stockName", "stockVariation"},
        	new int[] {R.id.stockSymbol,R.id.stockPrice, R.id.stockName, R.id.stockVariation}
       	);
    	
        setContentView(R.layout.activity_main);
        
        new addStocktoLayoutFromDBTask().execute();

        setListAdapter(mAdapter);

        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
            	mode.setTitle(listView.getCheckedItemCount() + " selected");
            }

            
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                case R.id.remove_stock:                	
                    SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                    int numberOfRemovedStocks = 0;
                    for (int i = 0; i < checkedItems.size(); i++) {
                        if (checkedItems.valueAt(i)) {
                        	removeStock(checkedItems.keyAt(i-numberOfRemovedStocks));
                        	numberOfRemovedStocks++;
                        }
                    }
                	mode.finish();
                	return true;
                default:
                    return false;
                }
            }

            
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        
        // This will be used to call Activity StockDetails
        listView.setOnItemClickListener(new OnItemClickListener(){    
        	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
				Toast.makeText(getApplicationContext(),	"OnClickListener(), pos = " + pos + ", id = " + id, Toast.LENGTH_SHORT).show();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        	new refreshStockDataTask().execute();
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
    		
    		// Removing new line that might be entered by user
    		stockSymbol = stockSymbol.replaceAll("\\\\n", ""); 

    		// Check if stock symbol seems valid
    		Log.i(TAG, "stockSymbol length = " + stockSymbol.length());
    		if(4 > stockSymbol.length() || stockSymbol.length() > 8) {
				Toast.makeText(getApplicationContext(),
	    				"Stock " + stockSymbol + " seems invalid",
	    				Toast.LENGTH_LONG).show();   			
    			return;
    		}
    		
    		Log.i(TAG, "onClick Save, stockSymbol = " + stockSymbol);
    		
    		try {
    			new addStocktoLayoutTask().execute(stockSymbol);

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

	public void addStockToLayout(StockData stockData) {
		Log.i(TAG, "addStockToLayout(" + stockData.getStockSymbol() + ")");
    	HashMap<String,String> stockRow = new HashMap<String,String>();
    	stockRow.put("stockSymbol",stockData.getStockSymbol());
    	stockRow.put("stockPrice", stockData.getStockPrice());
    	stockRow.put("stockName", stockData.getStockName());
    	stockRow.put("stockVariation", stockData.getStockVariation());
    	stockList.add(stockRow);
	}
	
	public void removeStock(int pos) {
	    DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
		Log.i(TAG, "stockList DBG");
		for(int i=0; i<stockList.size(); i++) {
			Log.i(TAG, "stockList[" + i + "] = " + stockList.get(i).get("stockSymbol"));
		}
		
		Log.i(TAG, "removeStock(" + pos + ")");
		Log.i(TAG, "Removing stock " + stockList.get(pos).get("stockSymbol"));
		try {
			Toast.makeText(getApplicationContext(),	"Removed stock " + stockList.get(pos).get("stockSymbol"), Toast.LENGTH_SHORT).show();
			dbHelper.removeStock(db, stockList.get(pos).get("stockSymbol"));
			stockList.remove(pos);
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}

	private class addStocktoLayoutTask extends AsyncTask<String, Integer, StockData> {

		@Override
		protected StockData doInBackground(String... stockSymbols) {
			Log.i(TAG, "addStocktoLayoutTask() - doInBackground()");
			StockData stockData = new StockData();
			
	        try {
	        	stockData = stockData.getStockDataFromSymbolFromBovespa(stockSymbols[0]);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        
			return stockData;
		}		
		
		protected void onPostExecute(StockData stockData) {
			Log.i(TAG, "addStocktoLayoutTask() - onPostExecute()");
		    DBHelper dbHelper = new DBHelper(mContext);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        
			// DO stuff here ( it's UI thread )
			if(stockData != null) {
				//stockData.debugStockDataObj();
				addStockToLayout(stockData);
				mAdapter.notifyDataSetChanged();
				dbHelper.addStockToDB(db, stockData);

				Toast.makeText(getApplicationContext(),
	    				"Added stock " + stockData.getStockSymbol() + " to watch list",
	    				Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
	    				"Couldn't add stock to watch list",
	    				Toast.LENGTH_LONG).show();
			}
			
			db.close();
		}
		
	}
	
	private class addStocktoLayoutFromDBTask extends AsyncTask<Void, Void, StockData[]> {

		@Override
		protected StockData[] doInBackground(Void... params) {
			Log.i(TAG, "addStocktoLayoutFromDBTask() - doInBackground()");
			StockData[] stockDataArray = null;			
		    DBHelper dbHelper = new DBHelper(mContext);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        
	        try {
	        	stockDataArray = dbHelper.getAllStocks(db);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        
	        db.close();
			return stockDataArray;
		}
		
		protected void onPostExecute(StockData[] stockDataArray) {
			Log.i(TAG, "addStocktoLayoutFromDBTask() - onPostExecute()");
			stockList.clear();
			// DO stuff here ( it's UI thread )
			for(StockData stockData : stockDataArray) {
				if(stockData != null) {
					addStockToLayout(stockData);
					// Update mLastUpdatedDate	
		    		mLastUpdatedDate = stockData.getStockLastUpdated();
				}
			}

			TextView textViewLastUpdated = (TextView) findViewById(R.id.lastUpdated);
    		textViewLastUpdated.setText(mLastUpdatedDate);
	
			mAdapter.notifyDataSetChanged();
		}

	}

	private class refreshStockDataTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			Log.i(TAG, "refreshStockDataTask() - doInBackground()");
		    DBHelper dbHelper = new DBHelper(mContext);
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	    	StockData[] allStocks = dbHelper.getAllStocks(db);
	    	StockData stockUpdated = new StockData();
	    	
	    	// Check if we have at least one stock on allStocks[]
	    	if(allStocks.length == 0) { 
	    		db.close();
				return -1;
	    	}
	    	
	    	for(StockData stock : allStocks) {
	    		try {
					stockUpdated = stockUpdated.getStockDataFromSymbolFromBovespa(stock.getStockSymbol());
					dbHelper.updateStockData(db, stockUpdated);
				} catch (Exception e) {
					Log.e(TAG, "Could not adquire StockData from YQL for stock " + stock.getStockSymbol());
					e.printStackTrace();
					db.close();
					return -2;					
				}
	    		// Update mLastUpdatedDate	
	    		mLastUpdatedDate = stockUpdated.getStockLastUpdated();
	    	}
	    	
	    	db.close();
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer param) {
			Log.i(TAG, "refreshStockDataTask() - onPostExecute()");
			// DO stuff here ( it's UI thread )
			
			// Check for errors
			switch(param) {
			// No stock to update
			case -1:
				Toast.makeText(getApplicationContext(), "No stock to update", Toast.LENGTH_LONG).show();
				return;
			case -2:
				Toast.makeText(getApplicationContext(), "Could not refresh stock data for all stocks. Please try again", Toast.LENGTH_LONG).show();
			default:
	    		TextView textViewLastUpdated = (TextView) findViewById(R.id.lastUpdated);
	    		textViewLastUpdated.setText(mLastUpdatedDate);
				Toast.makeText(getApplicationContext(), "Stock prices updated", Toast.LENGTH_LONG).show();
			}
			
			Log.i(TAG, "Calling addStocktoLayoutFromDBTask() from refreshStockDataTask()");
			new addStocktoLayoutFromDBTask().execute();
		}

	}
	
}
