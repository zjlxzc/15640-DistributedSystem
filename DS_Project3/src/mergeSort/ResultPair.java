package mergeSort;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to represent the data structure of a pair.
 */

import java.io.Serializable;

public class ResultPair<K, V> implements Serializable, Comparable<ResultPair<K, V>>{
	
	//generated serial version id
	private static final long serialVersionUID = 6877195955715125141L;
	private K key;
	private V value;
	
	public ResultPair() {
	}
	
	public ResultPair(K k, V v) {
		key = k;
		value = v;
	}
	
	public K getKey() {
		return key;
	}

	public void setKey(K k) {
		key = k;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V v) {
		value = v;
	}
	
	@Override
	public int compareTo(ResultPair<K, V> o) {
		int keyHash = key.hashCode();
		int valueHash = value.hashCode();
		int oKeyHash = o.key.hashCode();
		int oValueHash = o.value.hashCode();
		
		// if the two pairs have the same hash code of keys, then comparing hash code of values
		return keyHash == valueHash ? valueHash - oValueHash : keyHash - oKeyHash;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return key.equals(((ResultPair<K, V>)o).key) && value.equals(((ResultPair<K, V>)o).value);
	}
}
