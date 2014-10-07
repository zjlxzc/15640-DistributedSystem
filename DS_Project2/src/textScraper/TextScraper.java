package textScraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TextScraper {

	private String totalNumber;
	private ArrayList<ResultObject> resultObjects;
	
	public TextScraper() {
		resultObjects = new ArrayList<ResultObject>();
	}
	
	public static void main(String[] args) throws IOException {
		TextScraper textScraper = new TextScraper();
		
		String keyword = args[0];
		int pageNumber = 0;
		
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
        
		if (args.length == 1) {
			textScraper.query1(response, keyword);
		} else {
			pageNumber = Integer.parseInt(args[1]);
			textScraper.query2(response, keyword, pageNumber);
		}
		
		textScraper.printObjects();
	}
	
	public void query1(String response, String keyword) throws UnsupportedEncodingException {
		if(response.indexOf("numTotalResults") != -1){
			int index = response.indexOf("numTotalResults");
            int startIndex = response.indexOf("of", index);
            int endIndex = response.indexOf("</span>", index);
           
            totalNumber = response.substring(startIndex + 3, endIndex); 
            System.out.println("totalNumber: " + totalNumber);
        }
        else {
            totalNumber = "0";
        }
	}
	
	public void query2(String response, String keyword, int pageNumber) {
		if(response.indexOf("selected" + pageNumber) != -1){
			ResultObject tempObject = new ResultObject();
			int signalIndex = 0;
			
			while (true) {
				int nameIndex = response.indexOf("productName", signalIndex);
				if (nameIndex == -1) {
					break;
				}
				
				tempObject = new ResultObject();
				int titleIndex = response.indexOf("title", nameIndex);
				int startIndex = response.indexOf("\"", titleIndex);
				int endIndex = response.indexOf("\">", startIndex);
				tempObject.setTitle(response.substring(startIndex + 1, endIndex));
				
				int priceIndex = response.indexOf("productPrice", nameIndex);
				startIndex = response.indexOf("$", priceIndex);
				endIndex = response.indexOf("</", startIndex);
				tempObject.setProductPrice(response.substring(startIndex, endIndex));
				
				int shipIndex = (response.indexOf("freeShip", nameIndex) != -1) ?
						(response.indexOf("freeShip", nameIndex)) : (response.indexOf("calc", nameIndex));
				startIndex = response.indexOf(">", shipIndex);
				endIndex = response.indexOf("</", startIndex);
				tempObject.setShippingPrice(response.substring(startIndex + 1, endIndex));
				
				int merchantIndex = response.indexOf("newMerchantName", nameIndex);
				startIndex = response.indexOf(">", merchantIndex);
				endIndex = response.indexOf("</", startIndex);
				tempObject.setVendor(response.substring(startIndex + 1, endIndex));
				
				signalIndex = endIndex;
				resultObjects.add(tempObject);
			}
        }
        else {
        	resultObjects = new ArrayList<ResultObject>();
        }
	}
	
	public void printObjects() {
		for (ResultObject object : resultObjects) {
			System.out.println(object.toString());
		}
	}
}





