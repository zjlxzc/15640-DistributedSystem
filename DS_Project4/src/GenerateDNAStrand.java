import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to generate a strand of DNA.
*/

public class GenerateDNAStrand {

	private final char[] bases = {'A', 'C', 'G', 'T'}; // the DNA string will be generated from this finite set
	private int number = 0; // the number of DNA strands per cluster
	private int length = 0; // the number of characters per DNA strand has
	
	
	/**
	 * @param n the number of DNA strands per cluster
	 * @param l the number of characters per DNA strand has
	 */
	public GenerateDNAStrand(int n, int l) {
		number = n;
		length = l;
	}
	
	// return a set of DNA strands of each cluster
	public ArrayList<String> getStrand() {
		ArrayList<String> strands = new ArrayList<String>(number);
		StringBuilder strand = null;
		int index = 0; // index of a specific character
		Random random = new Random(); // random object
		
		while (number-- > 0) {
			strand = new StringBuilder();
			
			for (int i = 0; i < length; i++) {
				index = random.nextInt(4); // generate an index randomly
				strand.append(bases[index]); // append a character at the position of index
			}
			strands.add(strand.toString()); // add one DNA strand to result set.
		}
		return strands;
	}
	
	public static void main(String[] args) {
		try {
			int numOfStrands = Integer.parseInt(args[0]);
			int length = Integer.parseInt(args[1]);
			GenerateDNAStrand generate = new GenerateDNAStrand(numOfStrands, length);
			FileWriter fw = new FileWriter("dnaDataSet");
				
			ArrayList<String> strands = generate.getStrand();
			for (int i = 0; i < strands.size(); i++) {
				if (i == strands.size() - 1) {
					fw.write(strands.get(i));
				} else {
					fw.write(strands.get(i) + "\n");
				}
			}
			
			fw.close();			
		} catch (IOException e) {
			System.out.println("In Generate DNAStrand: " + e.getMessage());
		}
	}
}
