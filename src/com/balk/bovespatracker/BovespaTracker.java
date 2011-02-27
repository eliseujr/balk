package com.balk.bovespatracker;

import com.balk.bovespatracker.StockDataHelper.ApiException;
import com.balk.bovespatracker.StockDataHelper.ParseException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class BovespaTracker extends Activity {
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
  
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            spfactory.setValidating(false);
            SAXParser saxParser = spfactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new StockXmlReader());
            xmlReader.setErrorHandler(new StockXmlReader());
            InputSource source = new InputSource(pageContent);
            xmlReader.parse(source);
            
            textView.setText("Fim HTTP Connection");
        	textView.setText((CharSequence) pageContent);
        } catch (ApiException e) {
            Log.e("WordWidget", "Couldn't contact API", e);
        } catch (ParseException e) {
            Log.e("WordWidget", "Couldn't parse API response", e);
        } catch (Exception e) {
        	Log.e("WordWidget", "Final exception");
        }        	
        
        
    }

}

