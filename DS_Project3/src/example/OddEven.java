package example;

import java.util.Iterator;

import mapReduce.MRContext;
import mapReduce.MapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is an example to do map-reduce processing.
 */

public class OddEven implements MapReduce {
	@Override
	public void map(String key, String value, MRContext context){
		String[] token = value.split("^[0-9]+$");
		for (String word : token) {
			if (Integer.valueOf(word) % 2 == 0) {
				context.context("even", "1"); // indicate the this number is even
			} else {
				context.context("odd", "1");
			}
		}
	}

	@Override
	public void reduce (String key, Iterator<String> values, MRContext context) {
		int sum = 0;
		while (values.hasNext()) {
			sum++; // aggregate the occurrence
			values.next();
		}
		context.context(key, String.valueOf(sum));
	}

}
