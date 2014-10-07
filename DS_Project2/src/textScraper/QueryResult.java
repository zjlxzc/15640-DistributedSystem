package textScraper;

import java.util.ArrayList;

/**
 * @author Chun Xu - chunx@andrew.cmu.edu
 * 
 * This class is used to facilitate main class "TextScraper" to store results.
 * 
 */

public class QueryResult {
	
	private String totalNumber; // this variable is used to store the total number of results
	private ArrayList<ResultObject> resultObjects; // this variable is used to store the result objects
	
	public QueryResult() {
		resultObjects = new ArrayList<ResultObject>(); // a constructor to initialize variables
	}

	// the following are Getters and Setters
	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public ArrayList<ResultObject> getResultObjects() {
		return resultObjects;
	}

	public void setResultObjects(ArrayList<ResultObject> resultObjects) {
		this.resultObjects = resultObjects;
	}

	/* This function is used to print result objects on screen. */
	public void printObjects() {
		for (ResultObject object : resultObjects) {
			System.out.println(object.toString());
		}
	}
}
