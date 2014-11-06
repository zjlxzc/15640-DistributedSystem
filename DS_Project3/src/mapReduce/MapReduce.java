package mapReduce;

import java.io.Serializable;
import java.util.Iterator;

public interface MapReduce extends Serializable {
	public void map(String key, String value, MRContext context);
	public void reduce(String key, Iterator<String> values, MRContext context);
}
