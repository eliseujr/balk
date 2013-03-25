package com.balk.bovespatracker;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "BovespaTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BOVESPA_TRACKER_TABLE_NAME = "stocks";
    private static final String BOVESPA_TRACKER_TABLE_CREATE =
                "CREATE TABLE " + BOVESPA_TRACKER_TABLE_NAME + " (" +
                "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "symbol" + " TEXT, " +
                "name" + " TEXT, " +
                "price" + " TEXT, " +
                "variation" + " TEXT, " +
                "last_updated" + " TEXT);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BOVESPA_TRACKER_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void addStockToDB(SQLiteDatabase db, StockData stockData) {
		String query = "INSERT INTO " + BOVESPA_TRACKER_TABLE_NAME + " " +
			" (symbol, name, price, variation, last_updated) VALUES (" + 
			"'" + stockData.getStockSymbol() + "', " + 
			"'" + stockData.getStockName() + "', " + 
			"'" + stockData.getStockPrice() + "', " +
			"'" + stockData.getStockVariation() + "', " +
			"'" + stockData.getStockLastUpdated() + "' );";
		
		Log.i(TAG, "query = " + query);
		
		db.execSQL(query);
	}
	
	public int getNumberOfStocks(SQLiteDatabase db) {		
		return (int) DatabaseUtils.queryNumEntries(db, "stocks");
	}	
	
	public StockData[] getAllStocks(SQLiteDatabase db) {
		String query = "SELECT * FROM " + BOVESPA_TRACKER_TABLE_NAME + ";";
		Log.i(TAG, "getAllStocks() - SELECT query = " + query);
		
		Cursor result = db.rawQuery(query, null);
		StockData StockDataArray[] = new StockData[result.getCount()];
		int i = 0;
		
		if (result.getCount() > 0) {
			result.moveToFirst();
			
		    do {
		    	StockDataArray[i] = new StockData(
		    		result.getString(result.getColumnIndex("symbol")),
		    		result.getString(result.getColumnIndex("name")), 
		    		result.getString(result.getColumnIndex("price")), 
		    		result.getString(result.getColumnIndex("variation")),
		    		result.getString(result.getColumnIndex("last_updated"))
		    		);
		    	i++;
		    } while (result.moveToNext());
		    
		    result.close();
		}
		
		return StockDataArray;
	}
	
	public void removeStock(SQLiteDatabase db, String stockSymbol) {
		String query = "DELETE FROM " + BOVESPA_TRACKER_TABLE_NAME + " WHERE symbol='" + stockSymbol + "'";
		
		Log.i(TAG, "removeStock( " + stockSymbol + " ) - DELETE query = " + query);
		
		db.execSQL(query);
	}
	
	public void updateStockData(SQLiteDatabase db, StockData stockData) {
		String query = "SELECT * FROM " + BOVESPA_TRACKER_TABLE_NAME + " WHERE symbol='" + stockData.getStockSymbol() + "';";
		String updateQueryPrefix = "UPDATE " + BOVESPA_TRACKER_TABLE_NAME + " SET ";
		String updateQuerySufix = "' WHERE _id=";		

		Log.i(TAG, "updateStockData() - SELECT query = " + query);
		
		Cursor result = db.rawQuery(query, null);

		if (result.getCount() > 0) {
			result.moveToFirst();
			
		    do {
		    	String queryUpdatePrice = updateQueryPrefix + "price='" + stockData.getStockPrice() + updateQuerySufix + result.getString(result.getColumnIndex("_id"));
		    	String queryUpdateVariation = updateQueryPrefix + "variation='" + stockData.getStockVariation() + updateQuerySufix + result.getString(result.getColumnIndex("_id"));
		    	String queryUpdateLastUpdated = updateQueryPrefix + "last_updated='" + stockData.getStockLastUpdated() + updateQuerySufix + result.getString(result.getColumnIndex("_id"));

		    	Log.i(TAG, "updateStockData() - queryUpdatePrice query = " + queryUpdatePrice);
		    	Log.i(TAG, "updateStockData() - queryUpdateVariation query = " + queryUpdateVariation);
		    	Log.i(TAG, "updateStockData() - queryUpdateLastUpdated query = " + queryUpdateLastUpdated);
		    	
		    	db.execSQL(queryUpdatePrice);
		    	db.execSQL(queryUpdateVariation);
		    	db.execSQL(queryUpdateLastUpdated);
		    } while (result.moveToNext());
		    
		    result.close();
		}
	}

}