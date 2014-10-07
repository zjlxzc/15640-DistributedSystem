package textScraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextScraper {
	
	public TextScraper() {
	}
	
	public static void main(String[] args) throws IOException {
		TextScraper textScraper = new TextScraper();
		
		String keyword = args[0];
		
		String[] words = keyword.split(" ");
		StringBuilder key = new StringBuilder();
		StringBuilder searchB = new StringBuilder();
		for (String str : words) {
			key.append(str).append("-");
			searchB.append(str).append("%20");
		}
		key.deleteCharAt(key.length() - 1);
		String search = searchB.substring(0, searchB.length() - 2);
		URL shopping = new URL("http://www.shopping.com/" + key +"/products?CLT=SCH&KW=" + search);

		String response = "";
		HttpURLConnection connection = (HttpURLConnection)shopping.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		
        String str = "";
        while ((str = in.readLine()) != null) {
        	response += str;
        }
        in.close();
        
		textScraper.query1(response, keyword);
	}
	
	public void query1(String response, String keyword) throws UnsupportedEncodingException {
		String totalNumber = "0";
		if(response.indexOf("numTotalResults") != -1){
			int index = response.indexOf("numTotalResults");
            int startIndex = response.indexOf("of", index);
            int endIndex = response.indexOf("</span>", index);
           
            totalNumber = response.substring(startIndex + 3, endIndex); 
        }
		
		System.out.println("totalNumber: " + totalNumber);
	}
}





