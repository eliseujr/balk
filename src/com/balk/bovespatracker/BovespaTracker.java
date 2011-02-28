package com.balk.bovespatracker;

import com.balk.bovespatracker.StockDataHelper.ApiException;
import com.balk.bovespatracker.StockDataHelper.ParseException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BovespaTracker extends Activity {
	
	private static final String TAG = "BovespaTracker";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("teste Eliseu");

        String pageContent = "";
        
        try {
        	textView.setText("Inicio HTTP Connection");
        	
            StockDataHelper.prepareUserAgent(this);
            pageContent = StockDataHelper.getPageContent();
/*  
            // A Simple JSONObject Creation
            JSONObject mJson = new JSONObject(pageContent);
            Log.i("Praeda","<jsonobject>\n"+ mJson.toString()+"\n</jsonobject>");

            // A Simple JSONObject Parsing
            JSONArray nameArray = mJson.names();
            JSONArray valArray = mJson.toJSONArray(nameArray);
            for(int i=0;i<valArray.length();i++) {
                Log.i("Praeda","<jsonname"+i+">\n"+nameArray.getString(i)+"\n</jsonname"+i+">\n"
                        +"<jsonvalue"+i+">\n"+valArray.getString(i)+"\n</jsonvalue"+i+">");
            }
  */          
            textView.setText("Fim HTTP Connection");
        	//textView.setText((CharSequence) pageContent);
        } catch (ApiException e) {
            Log.e(TAG, "Couldn't contact API", e);
        } catch (ParseException e) {
            Log.e(TAG, "Couldn't parse API response", e);
        } catch (Exception e) {
        	Log.e(TAG, "Final exception");
        	Log.e(TAG, e.getMessage());
        }        	
        
        
    }

}

