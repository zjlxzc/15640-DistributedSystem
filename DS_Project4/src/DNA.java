import java.io.Serializable;
import java.util.HashMap;


public class DNA implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4521365327173723180L;
	public HashMap<Character, Integer> map = new HashMap<Character, Integer>();
	
	public DNA() {
		map.put('A', 0);
		map.put('C', 0);
		map.put('G', 0);
		map.put('T', 0);
	}
	
	public void putA(int value) {
		map.put('A', map.get('A') + value);
	}
	
	public void putC(int value) {
		map.put('C', map.get('C') + value);
	}
	public void putG(int value) {
		map.put('G', map.get('G') + value);
	}
	public void putT(int value) {
		map.put('T', map.get('T') + value);
	}
}
