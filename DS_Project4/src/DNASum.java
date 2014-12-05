
public class DNASum {

	public static DNA[] sums;
	
	public DNASum(int length) {
		sums = new DNA[length];
		for (int i = 0; i < length; i++) {
			sums[i] = new DNA();
		}
	}
	
	public static void add(DNASum sum) {
		DNA[] oneSum = sums;
		for (int i = 0; i < oneSum.length; i++) {
			sums[i].putA(oneSum[i].map.get('A'));
			sums[i].putC(oneSum[i].map.get('C'));
			sums[i].putG(oneSum[i].map.get('G'));
			sums[i].putT(oneSum[i].map.get('T'));
		}
	}
}
