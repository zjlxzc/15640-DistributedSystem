package textScraper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Chun Xu - chunx@andrew.cmu.edu
 * 
 *         This assignment is to design and build a robust text scraper that
 *         will connect to a page on www.shopping.com and return results about
 *         a given keyword.
 *         
 *         This is the entry of this assignment (main part).
 * 
 */

public class TextScraper {

	public static void main(String[] args) {
	
		if (!checkArguments(args)) { // check input first
			System.out.println("Input is not valid!");
			System.out.println("You should use one of the following two formats: ");
			System.out.println("single argument format: java -jar Assignment.jar \"baby strollers\"");
			System.out.println("two arguments format: java -jar Assignment.jar \"baby strollers\" 2");
			System.out.println();
			System.exit(1);
		}
		
		String keyword = args[0]; // get the first input argument
		int pageNumber = 0;

		// composing search parts for url
		String concatenatekey = connectWords(keyword, true);
		String searchString = connectWords(keyword, false);
		
		// create request url
		String request = "http://www.shopping.com/" + concatenatekey + "/products?CLT=SCH&KW=" + searchString;
		Document doc = null;
		try {
			doc = Jsoup.connect(request).get(); // get response
		} catch (IOException e1) {
			System.out.println("In main function, line 38: IOException");
			e1.printStackTrace();
		} 

		// create a QueryResult instance to store query results
		QueryResult queryResult = new QueryResult();
		
		if (args.length == 1) { // for query with only one argument
			// get elements that have the result
			Elements products = doc.getElementsByClass("numTotalResults"); 
			String s = products.toString();
			
			// "3" is the number of characters from "of " to real result
			queryResult.setTotalNumber(s.substring(s.indexOf("of") + 3, s.indexOf("</")));
			System.out.println("Total number of results: " + queryResult.getTotalNumber());
			
		} else { // for query with two arguments
			// if the input page number is not the first page, I need to get the parent directory
			if (Integer.parseInt(args[1]) != 1) { 
				String directory = "";
				
				// according to the structure of the web page, I have the following parts
				Elements products = doc.getElementsByClass("breadCrumb");
				for (Element e : products) {
					for (Element a : e.getElementsByAttribute("itemtype")) {
						for (Element b : a.getElementsByAttribute("class")) {
							if (b.toString().contains("category")
									&& !b.toString().contains("itemtype")) {
								// get the parent directory of arguments
								directory = b.getElementsByTag("span").text(); 
							}
						}
					}
				}
			
				directory = connectWords(directory, true);
				pageNumber = Integer.parseInt(args[1]); // get input page number
				
				// create a new request url
				request = "http://www.shopping.com/" + directory + "/" + concatenatekey
					+ "/products~PG-" + pageNumber + "?KW=" + searchString; 
				
				try {
					doc = Jsoup.connect(request).get(); // get new response
				} catch (IOException e1) {
					System.out.println("In main function, line 77: IOException");
					e1.printStackTrace();
				} 
			}
			
			queryResult.setResultObjects(query(doc)); // execute query
			queryResult.printObjects(); // print result
		} 
	}
	
	/** This method is used to get detail information of each product
	 * 
	 * @param doc - source Document
	 * @return - result set
	 */
	public static ArrayList<ResultObject> query(Document doc) {
		// create an array to store result objects
		ArrayList<ResultObject> resultObjects = new ArrayList<ResultObject>();
		Elements products = doc.getElementsByClass("gridItemBtm"); // get products section
		ResultObject resultObject = null; // a single result object to store detail information

		for (Element product : products) {
			resultObject = new ResultObject();

			// get Product Name information
			for (Element productName : product.getElementsByClass("productName")) {
				// "title" information could be in different element, so I need to use if-else condition
				if (productName.attr("title").length() != 0) {
					resultObject.setTitle(productName.attr("title"));
				} else {
					resultObject.setTitle(productName.getElementsByTag("span")
							.attr("title"));
				}
			}

			// get Price of the product
			for (Element productPrice : product
					.getElementsByClass("productPrice")) {
				resultObject.setProductPrice(productPrice.text());
			}

			// get Shipping Price information
			Elements shipInfo = product.getElementsByClass("freeShip");
			shipInfo.addAll(product.getElementsByClass("calc")); // merge two categories 
			for (Element shipIn : shipInfo) {
				resultObject.setShippingPrice(shipIn.text());
			}

			// get Vendor information
			for (Element productVendor : product.getElementsByClass("newMerchantName")) {
				// "vendor" information could be in different element, so I need to use if-else condition
				if (productVendor.text().length() != 0) {
					resultObject.setVendor(productVendor.text());
				} else {
					resultObject.setVendor(productVendor.getElementsByTag("a").attr("class"));
				}
			}

			resultObjects.add(resultObject); // add single result object to result set
		}
		
		return resultObjects;
	}
	

	/** This method is used to concatenate search parts.
	 * 
	 * @param keyword - the input string
	 * @param sign - to indicate the symbol that is needed to connect each part
	 * 				 if sign is true, it needs "-" to do connection,
	 * 				 if sign is false, it needs "+" to do connection
	 * @return concatenated string
	 */
	
	public static String connectWords(String keyword, boolean sign) {
		String[] words = keyword.toLowerCase().split(" "); // split input by empty space
		StringBuilder concatenatekey = new StringBuilder(); // this variable is used to store result
		for (String str : words) {
			concatenatekey.append(str);
			if (sign) {
				concatenatekey.append("-");
			} else {
				concatenatekey.append("+");
			}
		}
		concatenatekey.deleteCharAt(concatenatekey.length() - 1); // remove the last symbol
		return concatenatekey.toString();
	}
	
	/** This method is used to check if input is valid.
	 * 
	 * @param args - user input
	 * @return a boolean value to indicate result status
	 */
	public static boolean checkArguments(String[] args) {
		if (args == null || args.length == 0) { // if no input
			return false;
		}
		
		if (args.length == 2) { // if the page number is not integer
			try {
		        Integer.valueOf(args[1]);
		        return true;
		    } catch (NumberFormatException e) {
		    	System.out.println("The second argument should be a positive integer.");
		        return false;
		    }
		}
		if (args.length > 2) { // if there are more than one arguments
			return false;
		}
		
		return true;
	}
}
