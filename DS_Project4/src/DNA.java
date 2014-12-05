import java.util.HashMap;


public class DNA {

	public static HashMap<Character, Integer> map = new HashMap<Character, Integer>();
	
	public DNA() {
		map.put('A', 0);
		map.put('C', 0);
		map.put('G', 0);
		map.put('T', 0);
	}
	
	public static void putA(int value) {
		map.put('A', map.get('A') + value);
	}
	
	public static void putC(int value) {
		map.put('C', map.get('C') + value);
	}
	public static void putG(int value) {
		map.put('G', map.get('G') + value);
	}
	public static void putT(int value) {
		map.put('T', map.get('T') + value);
	}
}
