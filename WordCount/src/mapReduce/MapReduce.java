package mapReduce;


/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This is an interface of map reduce.
 * User should implement these two required methods.
 */

import java.util.Iterator;

public interface MapReduce {
	public void map(String key, String value, MRContext context);
	public void reduce(String key, Iterator<String> values, MRContext context);
}
