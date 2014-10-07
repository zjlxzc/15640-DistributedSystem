package textScraper;

/**
 * @author Chun Xu - chunx@andrew.cmu.edu
 * 
 * This class is used to facilitate class "QueryResult" to store result details.
 * 
 */

public class ResultObject {

	private String title; // this variable is used to store title/product name
	private String productPrice; // this variable is used to store price of the product
	private String shippingPrice; // this variable is used to store shipping price
	private String vendor; // this variable is used to store vendor information
	
	public ResultObject() {
	}
	
	public ResultObject(String title, String productPrice, String shippingPrice, String vendor) {
		this.title = title;
		this.productPrice = productPrice;
		this.shippingPrice = shippingPrice;
		this.vendor = vendor;
	}
	
	// the following are Getters and Setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getShippingPrice() {
		return shippingPrice;
	}

	public void setShippingPrice(String shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	/* This function is used to result object details on screen. */
	@Override
	public String toString() {
		if (shippingPrice == null) { // if there is no shipping information
			shippingPrice = "No Shipping Info";
		}
		
		String output = "Title/Product Name: " + title + "\n"
						+ "Price of Product: " + productPrice + "\n"
						+ "Shipping Price: " + shippingPrice + "\n"
						+ "Vendor: " + vendor + "\n";
		
		return output;
	}	
}
