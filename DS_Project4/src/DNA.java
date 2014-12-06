import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to aggregate the frequency of each character.
*/

public class DNA implements Serializable {

	private static final long serialVersionUID = -4521365327173723180L;
	public HashMap<Character, Integer> map = new HashMap<Character, Integer>();
	
	public DNA() {
		map.put('A', 0);
		map.put('C', 0);
		map.put('G', 0);
		map.put('T', 0);
	}
	
	// increase the frequency of character 'A'
	public void putA(int value) {
		map.put('A', map.get('A') + value);
	}
	
	// increase the frequency of character 'C'
	public void putC(int value) {
		map.put('C', map.get('C') + value);
	}
	
	// increase the frequency of character 'G'
	public void putG(int value) {
		map.put('G', map.get('G') + value);
	}
	
	// increase the frequency of character 'T'
	public void putT(int value) {
		map.put('T', map.get('T') + value);
	}
}
