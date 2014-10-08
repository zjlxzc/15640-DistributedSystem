package exampleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import exception.RemoteException;

public class TextScraperServant implements TextScraper {
	public TextScraperServant() {
	}
	
	public int query(String param) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("TextScraperServant: Got request to query \"" + param + "\"");
		
		String[] words = param.split(" ");
		StringBuilder key = new StringBuilder();
		StringBuilder searchB = new StringBuilder();
		for (String str : words) {
			key.append(str).append("-");
			searchB.append(str).append("%20");
		}
		key.deleteCharAt(key.length() - 1);
		String search = searchB.substring(0, searchB.length() - 2);
		URL shopping = null;
		try {
			shopping = new URL("http://www.shopping.com/" + key +"/products?CLT=SCH&KW=" + search);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String response = "";
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)shopping.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        String str = "";
        try {
			while ((str = in.readLine()) != null) {
				response += str;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("TextScraperServant: get the result of query \"" + param + "\"");
		try {
			return query1(response, param);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int query1(String response, String keyword) throws UnsupportedEncodingException {
		String totalNumber = "0";
		if(response.indexOf("numTotalResults") != -1){
			int index = response.indexOf("numTotalResults");
            int startIndex = response.indexOf("of", index);
            int endIndex = response.indexOf("</span>", index);
            System.out.println("response: " + response);
            totalNumber = response.substring(startIndex + 3, endIndex); 
        }
		System.out.println("number: " + totalNumber);
		return Integer.parseInt(totalNumber.substring(0, totalNumber.indexOf("&")));
	}
}
