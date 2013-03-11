package com.balk.bovespatracker;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "BovespaTracker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BOVESPA_TRACKER_TABLE_NAME = "stocks";
    private static final String BOVESPA_TRACKER_TABLE_CREATE =
                "CREATE TABLE " + BOVESPA_TRACKER_TABLE_NAME + " (" +
                "symbol" + " TEXT, " +
                "name" + " TEXT, " +
                "price" + " TEXT, " +
                "variation" + " TEXT);";

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
	
	public void saveStockRecord(SQLiteDatabase db, StockData stockData) {
		String query = "INSERT INTO " + BOVESPA_TRACKER_TABLE_NAME + " VALUES (" + 
			"'" + stockData.getStockSymbol() + "', " + 
			"'" + stockData.getStockName() + "', " + 
			"'" + stockData.getStockPrice() + "', " +
			"'" + stockData.getStockVariation() + "' );";
		
		Log.i(TAG, "query = " + query);
		
		db.execSQL(query);
		
		Log.i(TAG, "getNumberOfStocks = " + getNumberOfStocks(db));
		
		getAllStocks(db);
	}
	
	public int getNumberOfStocks(SQLiteDatabase db) {		
		return (int) DatabaseUtils.queryNumEntries(db, "stocks");
	}	
	
	public StockData[] getAllStocks(SQLiteDatabase db) {
		String query = "SELECT * FROM " + BOVESPA_TRACKER_TABLE_NAME + ";";
		
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
		    		result.getString(result.getColumnIndex("variation"))
		    		);
		    	i++;
		    } while (result.moveToNext());
		    
		    result.close();
		}
		
		return StockDataArray;
	}
	
	
}