package mergeSort;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is the structure of a single key-value pair
 */

public class SingleRecord implements Comparable<SingleRecord>{
	private String key;
	private String value;
	
	public SingleRecord(String k, String v) {
		key = k;
		value = v;
	}
	
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
	
	// the hash code will be used to do calculation to find corresponding reducer
	public int hashCode() {
		int len = key.length();
        int[] array = new int[26];
        for (int i = 0; i < len; i++) {
            array[key.charAt(i) - 'a']++;
        }
        
        int a = 378551;
        int b = 63689;
        int hash = 0;
        
        for (int value : array) {
            hash = hash * a + value;
            a = a * b;
        }
        
        return hash;
	}
}
