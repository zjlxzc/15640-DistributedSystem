package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to store key - value pair
 */

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import mergeSort.SingleRecord;

public class MRContext {
	private PriorityQueue<SingleRecord> context; // to get key-value pair according to key order
	
	public MRContext() {
		context = new PriorityQueue<SingleRecord>(30, new Comparator<SingleRecord>() {
			public int compare(SingleRecord sr1, SingleRecord sr2) {
				int value = sr1.getKey().compareTo(sr2.getKey());
				if (value == 0) {
					return sr1.getValue().compareTo(sr2.getValue());
				}
				return value;
			}
		});
	}
	
	public void context(String key, String value) {
		SingleRecord newPair = new SingleRecord(key, value);
		context.add(newPair); // store all key-value pair
	}
	
	public Iterator<SingleRecord> getIterator() {
		return context.iterator(); // return an iterator to go through all pairs
	}
}
