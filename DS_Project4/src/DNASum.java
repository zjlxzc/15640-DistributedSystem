
public class DNASum {

	public static DNA[] sums;
	
	public DNASum(int length) {
		sums = new DNA[length];
		for (int i = 0; i < length; i++) {
			sums[i] = new DNA();
		}
	}
	
	public static void add(DNASum sum) {
		for (int i = 0; i < sums.length; i++) {
			sums[i].map.put('A', sum.sums[i].map.get('A'));
			sums[i].map.put('C', sum.sums[i].map.get('C'));
			sums[i].map.put('G', sum.sums[i].map.get('G'));
			sums[i].map.put('T', sum.sums[i].map.get('T'));
		}
	}
}
