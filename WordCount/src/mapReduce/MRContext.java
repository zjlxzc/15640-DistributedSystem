package mapReduce;


/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to store key - value pair
 */

import java.util.Comparator;
import java.util.PriorityQueue;

public class MRContext {
	private PriorityQueue<SingleRecord> context; // to get key-value pair according to key order
	
	public MRContext() {
		context = new PriorityQueue<SingleRecord>(1, new Comparator<SingleRecord>() {
			public int compare(SingleRecord sr1, SingleRecord sr2) { // sort record by key first, then by value
				return sr1.getKey().compareTo(sr2.getKey());
			}
		});
	}
	
	public void context(String key, String value) {
		SingleRecord newPair = new SingleRecord(key, value);
		context.add(newPair); // store all key-value pair
	}
	
	public PriorityQueue<SingleRecord> getQueue() {
		return context; // return an iterator to go through all pairs
	}
}
