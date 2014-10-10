/**
 * File name: TextScraperServant.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This is class is a servant of the example - "textScraper",
 * it is a remote object class that implements TextScraper.
 * This function of it is to do the query for given "product name".
 */

package exampleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TextScraperServant implements TextScraper {
	public TextScraperServant() {
	}
	
	public int query(String param) {
		System.out.println("TextScraperServant: Got request to query " + param);
		
		String[] words = param.split(" ");
		StringBuilder key = new StringBuilder();
		StringBuilder searchB = new StringBuilder();
		// concatenate the input word to meet with url format
		for (String str : words) {
			key.append(str).append("-");
			searchB.append(str).append("%20");
		}
		key.deleteCharAt(key.length() - 1); // remove last character
		String search = searchB.substring(0, searchB.length() - 2);
		
		URL shopping = null;
		try { // generate URL
			shopping = new URL("http://www.shopping.com/" + key +"/products?CLT=SCH&KW=" + search);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String response = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)shopping.openConnection(); // connect to URL
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader in = null;
		try { // get input
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        String str = "";
        try {
			while ((str = in.readLine()) != null) {
				response += str; // get response
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println("TextScraperServant: get the result of query \"" + param + "\"");
		try {
			return query1(response, param); // return the result of query
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int query1(String response, String keyword) throws UnsupportedEncodingException {
		String totalNumber = "0";
		if(response.indexOf("numTotalResults") != -1){ // get result by key word and remove unrelated string
			int index = response.indexOf("numTotalResults");
            int startIndex = response.indexOf("of", index);
            int endIndex = response.indexOf("</span>", index);
            
            totalNumber = response.substring(startIndex + 3, endIndex); 
        }
		if (totalNumber.indexOf("&") == -1) {
			return Integer.parseInt(totalNumber);
		}
		
		return Integer.parseInt(totalNumber.substring(0, totalNumber.indexOf("&")));
	}
}
