package mapReduce;
/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is an example to do map-reduce processing.
 * It will get the total number of odd numbers and the total number of even numbers.
 */

import java.util.Iterator;
import mapReduce.MRContext;
import mapReduce.MapReduce;

public class OddEven implements MapReduce {
	
	@Override  // implement map method
	public void map(String key, String value, MRContext context){
		String[] token = value.split(" "); // separate each integer
		for (String word : token) {
			if (Integer.valueOf(word) % 2 == 0) {
				context.context("even", "1"); // indicate the this number is even
			} else {
				context.context("odd", "1"); // use context object to store result
			}
		}
	}

	@Override  // implement reduce method
	public void reduce (String key, Iterator<String> values, MRContext context) {
		int sum = 0;
		while (values.hasNext()) {
			sum++; // aggregate the occurrence
			values.next();
		}
		context.context(key, String.valueOf(sum)); // use context object to store result
	}
}
