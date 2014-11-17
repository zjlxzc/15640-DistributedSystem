package example;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is a word count example to do map-reduce processing.
 */

import java.util.Iterator;
import mapReduce.MRContext;
import mapReduce.MapReduce;

public class WordCount implements MapReduce{
	 	    
	@Override  // implement map method
	public void map(String key, String value, MRContext context){
		String[] token = value.split("\\W"); // separate a line by unwanted characters
		for (String word : token) {
			if (word.length() != 0) {
				context.context(word, "1"); // indicate the occurrence of a single word is 1
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
