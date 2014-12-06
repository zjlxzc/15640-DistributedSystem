/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to facilitate the calculation of character frequencies on each cluster.
*/

import java.io.Serializable;

public class DNASum implements Serializable{

	private static final long serialVersionUID = -553582776442070453L;
	public DNA[] sums;
	
	public DNASum(int length) {
		sums = new DNA[length];
		for (int i = 0; i < length; i++) {
			sums[i] = new DNA();
		}
	}
	
	// add the character frequency from a cluster
	public void add(DNASum sum) {
		DNA[] oneSum = sum.sums;
		for (int i = 0; i < oneSum.length; i++) {
			sums[i].putA(oneSum[i].map.get('A'));
			sums[i].putC(oneSum[i].map.get('C'));
			sums[i].putG(oneSum[i].map.get('G'));
			sums[i].putT(oneSum[i].map.get('T'));
		}
	}
	
	// add the character frequency of a strand
	public void addString(String strand) {
		for (int i = 0; i < strand.length(); i++) {
			char c = strand.charAt(i);
			sums[i].map.put(c, sums[i].map.get(c) + 1);
		}
	}
}
