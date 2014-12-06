import java.io.Serializable;


public class DNASum implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -553582776442070453L;
	public DNA[] sums;
	
	public DNASum(int length) {
		sums = new DNA[length];
		for (int i = 0; i < length; i++) {
			sums[i] = new DNA();
		}
	}
	
	public void add(DNASum sum) {
		DNA[] oneSum = sums;
		for (int i = 0; i < oneSum.length; i++) {
			sums[i].putA(oneSum[i].map.get('A'));
			sums[i].putC(oneSum[i].map.get('C'));
			sums[i].putG(oneSum[i].map.get('G'));
			sums[i].putT(oneSum[i].map.get('T'));
		}
	}
	
	public void addString(String strand) {
		
		for (int i = 0; i < strand.length(); i++) {
			
			if (strand.charAt(i) == 'A') {
				sums[i].putA(1);
			} else if (strand.charAt(i) == 'C') {
				sums[i].putC(1);
			} else if (strand.charAt(i) == 'G') {
				sums[i].putG(1);
			} else {
				sums[i].putT(1);
			}
		}
	}
}
