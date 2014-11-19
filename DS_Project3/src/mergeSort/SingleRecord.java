package mergeSort;

import java.io.Serializable;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is the structure of a single key-value pair.
 */

public class SingleRecord implements Serializable, Comparable<SingleRecord>{

	private static final long serialVersionUID = -15136372946432285L; // Auto-generated serialVersionUID
	private String key;
	private String value;
	
	public SingleRecord() {	
	}
	
	// specify key and value for a new record
	public SingleRecord(String k, String v) {
		key = k;
		value = v;
	}
	
	// getters and setters
	public String getKey() {
		return key;
	}

	public void setKey(String k) {
		this.key = k;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String v) {
		this.value = v;
	}
	
	@Override
	public int compareTo(SingleRecord o) {
		return this.key.compareTo(o.key);
	}
	
	// This hash code function will be used to do calculation for a key,
	// so that this key-value pair can find its corresponding reducer
	public int hashCode() {
		int len = key.length();
        int[] array = new int[256];
        
        for (int i = 0; i < len; i++) { // get the number of each character
            array[key.charAt(i)]++;
        }
        
        long a = 378551;
        long b = 63689;
        long hash = 0;
        
        for (int value : array) { // do some calculation to get hash value
            hash = hash * a + value;
            a = a * b;
        }
        
        return (int)hash;
	}
}
