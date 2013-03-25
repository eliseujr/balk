package com.balk.bovespatracker;

import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StockWebHelper {

    private static final String TAG = "StockWebHelper";
    
   	private static final String URL_PREFIX = "http://www.bmfbovespa.com.br/cotacoes2000/FormConsultaCotacoes.asp?strListaCodigos=";
       
    public static StockData getStockDataFromWEB(String stockSymbol) {
    	StockData stockData = new StockData();
    	
    	// Creating URL to pull stock data from
    	String URL = URL_PREFIX + stockSymbol;
    	
    	Log.i(TAG, "Fetching URL : " + URL);
    	
    	// XML element names
    	String KEY_ITEM = "Papel";
    	String KEY_SYMBOL = "Codigo";
    	String KEY_NAME = "Nome";
    	String KEY_PRICE = "Ultimo";
    	String KEY_CHANGE = "Oscilacao";
    	String KEY_DATE = "Data";
    	
    	XMLParser parser = new XMLParser();
    	String xml = parser.getXmlFromUrl(URL); // getting XML
    	Document doc = parser.getDomElement(xml); // getting DOM element
    	 
    	NodeList nl = doc.getElementsByTagName(KEY_ITEM);
    	
    	// looping through all item nodes <item>
    	for (int i = 0; i < nl.getLength(); i++) {
    		Element e = (Element) nl.item(i);
    	    //String name = parser.getValue(e, KEY_NAME); // name child value
    		Log.i(TAG, "Codigo = " + e.getAttribute(KEY_SYMBOL));
    		Log.i(TAG, "Nome = " + e.getAttribute(KEY_NAME));
    		Log.i(TAG, "Ultimo = " + e.getAttribute(KEY_PRICE));
    		Log.i(TAG, "Oscilacao = " + e.getAttribute(KEY_CHANGE));
    		Log.i(TAG, "Data = " + e.getAttribute(KEY_DATE));
    		stockData.setStockSymbol(e.getAttribute(KEY_SYMBOL));
    		stockData.setStockName(e.getAttribute(KEY_NAME));
    		stockData.setStockPrice(e.getAttribute(KEY_PRICE));
    		stockData.setStockVariation(e.getAttribute(KEY_CHANGE));
    		stockData.setStockLastUpdated("Last updated " + e.getAttribute(KEY_DATE).substring(e.getAttribute(KEY_DATE).indexOf(' ')));
    	}
        
        return stockData;
    }
    
}



final class XMLParser {
	
    public String getXmlFromUrl(String url) {
        String xml = null;
 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
    
    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
        
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
     
    public final String getElementValue( Node elem ) {
             Node child;
             if( elem != null){
                 if (elem.hasChildNodes()){
                     for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                         if( child.getNodeType() == Node.TEXT_NODE  ){
                             return child.getNodeValue();
                         }
                     }
                 }
             }
             return "";
    }
	
}
