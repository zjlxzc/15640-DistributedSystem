/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to K-Means algorithm for DNA Strands.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class KMeansDNASeq {

	private static String[] centroids; // an array to store centroid of each cluster
	private static ArrayList<String>[] strandsOnEach; // store strands for each cluster
	private static final ArrayList<String> allStrands = new ArrayList<String>(); // store all input strands
	private static final char[] bases = {'A', 'C', 'G', 'T'}; // the DNA string will be generated from this finite set
	private static final int baseLength = 4; // the number of available characters
	private static int length = 0; // the number of characters per DNA strand has
	private static int clusterNum = 0;
	
	public KMeansDNASeq() {	
		
	}
	
	/**
	 * @param strands - the original set of strands
	 * @param output - the filename of output file
	 * @param number - the number of clusters
	 * @return
	 */
	
	public KMeansDNASeq(int cluster, String inputFileName, int len) {	
		clusterNum = cluster;
		length = len;
		File file = new File(inputFileName);
		
		try {
			Scanner scan = new Scanner(file);			
			while (scan.hasNext()) { // get all strands
				allStrands.add(scan.nextLine().trim());
			}
			scan.close();			
		} catch (FileNotFoundException e) {
			System.out.println("The file does not exist");
		}
		
		strandsOnEach = new ArrayList[clusterNum];
		for (int i = 0; i < strandsOnEach.length; i++) {
			strandsOnEach[i] = new ArrayList<String>(); // initialize ArrayList for each cluster
		}
	}
	
	public static void main(String[] args) {
		int clusterNum = Integer.parseInt(args[0]);
		int len = Integer.parseInt(args[1]);
		String inputFileName = args[2];
		
		KMeansDNASeq dna = new KMeansDNASeq(clusterNum, inputFileName, len); // set up input
		centroids = randomPick(dna.allStrands); // pick initial centroid randomly
		
		boolean finish = false; // a variable to decide when to stop calculation
		firstCalculation(centroids, dna.allStrands, strandsOnEach); // group all strands to each cluster
		
		while (!finish) {
			centroids = reCalculateCentroid(); // recalculate centroid for each cluster
			finish = reCalculate(); // regroup all strands to each cluster until they are stable
		}
		
		System.out.println(Arrays.toString(centroids));
	}
	
	// Randomly pick up one strand from all strands for each cluster.
	private static String[] randomPick(ArrayList<String> DNAList) {
		Random random = new Random();
		String[] centroids = new String[clusterNum]; // an array to store each centroid
		
		for (int i = 0; i < clusterNum; i++) {
			centroids[i] = DNAList.get(random.nextInt(DNAList.size())); // pick up one strand randomly
		}
		
		return centroids;
	}
	
	// Initial separate all strands to each cluster.
	private static void firstCalculation(String[] centroids,
			ArrayList<String> allStrands, ArrayList<String>[] strandsOnEach) {
		
		for (String strand : allStrands) {
			int minIndex = Integer.MAX_VALUE;
			int minDis = Integer.MAX_VALUE;
			
			for (int i = 0; i < centroids.length; i++) { // compare each strand with all centroids to get the minimum value
				int dis = calculateSimilarity(centroids[i], strand);
				if (dis < minDis) {
					minIndex = i;
					minDis = dis;
				}
			}
			strandsOnEach[minIndex].add(strand); // add strand to this group
		}		
		
		for (int i = 0; i < strandsOnEach.length; i++) {
			strandsOnEach[i].add(centroids[i]); // add centroid to array list of this cluster
		}
	}

	// Calculate new centroid
	private static String[] reCalculateCentroid() {
		String[] newCentroids = new String[strandsOnEach.length];
		int clusterNum = strandsOnEach.length;
		
		for (int i = 0; i < clusterNum; i++) {	
			newCentroids[i] = newCentroid(strandsOnEach[i]); // get new centroid
		}
		
		return newCentroids;
	}
	
	private static String newCentroid(ArrayList<String> strands) {
		StringBuilder newCen = new StringBuilder();
		int len = strands.get(0).length();
		
		for (int i = 0; i < length; i++) {
			int[] times = new int[baseLength];
			for (String strand : strands) { // get the frequency of each character from all strands
				char character = strand.charAt(i);
				switch (character) {
					case 'A':
						times[0]++;
						break;
					case 'C':
						times[1]++;
						break;
					case 'G':
						times[2]++;
						break;
					case 'T':
						times[3]++;
						break;
				}
			}
			
			int max = Integer.MIN_VALUE;
			int index = -1;
			
			for (int j = 0; j < times.length; j++) {
				if (times[j] > max) {
					max = times[j];
					index = j;
				}
			}
			
			newCen.append(bases[index]); // add the most frequent character
		}
		
		return newCen.toString();
	}
	
	// Based on new centroid, recalculate the group
	private static boolean reCalculate() {
		ArrayList<String>[] newSimilarity = new ArrayList[clusterNum];
		for (int i = 0; i < clusterNum; i++) {
			newSimilarity[i] = new ArrayList<String>(); // initialize the array
		}
		
		for (String strand : allStrands) {
			int minIndex = Integer.MAX_VALUE;
			int minSimilarity = Integer.MAX_VALUE;
			
			for (int j = 0; j < clusterNum; j++) { // 
				int similarity = calculateSimilarity(centroids[j], strand);
				if (similarity < minSimilarity) {
					minIndex = j;
					minSimilarity = similarity;
				}
			}
			
			newSimilarity[minIndex].add(strand);			
		}		
		
		for (int i = 0; i < strandsOnEach.length; i++) {
			newSimilarity[i].add(centroids[i]);
		}
		
		return compareEqual(reCalculateCentroid());
	}
	
	// calculate similarity of two strands
	private static int calculateSimilarity(String cen, String strand) {
		int difference = 0;
		int index = 0;
		int length = cen.length();
		
		while (index < length) {
			if (cen.charAt(index) != strand.charAt(index)) { // if there are different characters
				difference++;
			}
			index++;
		}
		return difference;
	}
	
	// compare old centroids and new centroids
	public static boolean compareEqual(String[] newCentral) {
		int clusterNum = centroids.length;
		
		for (int i = 0; i < clusterNum; i++) {
			int j = 0;
			for (j = 0; j < clusterNum; j++) {
				if (centroids[i].equals(newCentral[j])) {
					break;
				}
			}
			if (j == clusterNum) {
				return false;
			}
		}
		return true;
	}
}
