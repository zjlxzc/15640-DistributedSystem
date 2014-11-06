package mapReduce;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.PriorityQueue;

import mergeSort.ResultPair;

public class MRContext {
	private PriorityQueue<ResultPair<String, String>> context;
	private Set<String> allKey = new TreeSet<String>();
	
	public MRContext() {
		context = new PriorityQueue<ResultPair<String, String>>(30, new Comparator<ResultPair<String, String>>() {
			public int compare(ResultPair<String, String> rp1, ResultPair<String, String> rp2) {
				int value = rp1.getKey().compareTo(rp2.getKey());
				if (value == 0) {
					return rp1.getValue().compareTo(rp2.getValue());
				}
				return value;
			}
		});
	}
	
	public void context(String key, String value) {
		ResultPair<String, String> newPair = new ResultPair<String, String>(key, value);
		context.add(newPair);
		allKey.add(key);
	}
	
	public Iterator<ResultPair<String, String>> getIterator() {
		return context.iterator();
	}
}
