package example;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is an example to do map-reduce processing.
 */

import java.util.Iterator;
import mapReduce.MRContext;
import mapReduce.MapReduce;

public class WordCount implements MapReduce{
	 	    
	@Override
	public void map(String key, String value, MRContext context){
		String[] token = value.split("^[a-zA-Z]+$");
		for (String word : token) {
			context.context(word, "1"); // indicate the occurrence of a single word is 1
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
