import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;

public class DNAKMeanMPI {

	private static String[] centroids; // an array to store centroid of each cluster
	private static ArrayList<String>[] strandsOnEach; // store strands for each cluster
	private static final ArrayList<String> allStrands = new ArrayList<String>(); // store all input strands
	private static final char[] bases = { 'A', 'C', 'G', 'T' }; // the DNA string will be generated from this finite set
	private static final int baseLength = 4; // the number of available characters
	private static int length = 0; // the number of characters per DNA strand has
	private static int clusterNum = 0;

	public DNAKMeanMPI(int clusterNumber, String inputFileName) {
		clusterNum = clusterNumber;
		try {
			int myRank = MPI.COMM_WORLD.Rank(); // get current rand
			int[] lenArr = new int[1];
			int length = 20;
			
			if (myRank == 0) {
				long startTime = System.currentTimeMillis();
				System.out.println("MPI start to run: " + startTime);
				KMeansDNASeq(clusterNum, inputFileName, length);				
				centroids = randomPick(allStrands);			
				
				int mpiNum = MPI.COMM_WORLD.Size();
				int len = allStrands.size() / (mpiNum - 1);			
				lenArr[0] = len;
				int index = 0;
				
				for (int i = 1; i < mpiNum; i++) {
					String[] listToCluster = new String[len];
					System.arraycopy((String[])allStrands.toArray(), (i - 1) * len, listToCluster, 0, len);	
					MPI.COMM_WORLD.Send(lenArr, 0, 1, MPI.INT, i, 0);
					MPI.COMM_WORLD.Send(listToCluster, 0, len, MPI.OBJECT, i, 1);					
				}	
				
				boolean finish = false;
				DNASum[] sums = new DNASum[clusterNum];

				while (!finish) {
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new DNASum(length);
					}					
					for (int i = 1; i < mpiNum; i++) {
						MPI.COMM_WORLD.Send(centroids, 0, clusterNum, MPI.OBJECT, i, 2);
					}
					
					for (int i = 1; i < mpiNum; i++) {
						DNASum[] localSum = new DNASum[clusterNum];
						MPI.COMM_WORLD.Recv(localSum, 0, clusterNum, MPI.OBJECT, i, 3);
						
						for (int j = 0; j < clusterNum; j++) {
							sums[j].add(localSum[j]);
						}
						
						for (int k = 0; k < clusterNum; k++) {
							char[] frequency = getCharArray(sums[i]);
							centroids[i] = createNewString(frequency);
						}
					}										
					finish = reCalculate();
				}
				
				System.out.println(Arrays.toString(centroids));
				long endTime = System.currentTimeMillis();
				System.out.println("MPI end: " + endTime);
				System.out.println("Time Taken: " + (endTime - startTime));
				
			} else {
				
				MPI.COMM_WORLD.Recv(lenArr, 0, 1, MPI.INT, 0, 0);
				int len = lenArr[0];
				String[] localList = new String[len];
				MPI.COMM_WORLD.Recv(localList, 0, len, MPI.OBJECT, 0, 1);
				String[] localCentroids = new String[clusterNum];
				
				while (true) {
					MPI.COMM_WORLD.Recv(localCentroids, 0, clusterNum, MPI.OBJECT, 0, 2);
					
					DNASum[] sums = new DNASum[clusterNum];
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new DNASum(length);
					}
					firstCalculation(localCentroids, localList, sums);
					MPI.COMM_WORLD.Send(sums, 0, sums.length, MPI.OBJECT, 0, 3);
				}				
			}				
		} catch (MPIException e) {
			e.printStackTrace();
		}
	}
	
	public char[] getCharArray(DNASum sum) {
		DNA[] sums = sum.sums;
		char[] result = new char[length];
		
		for (int i = 0; i < length; i++) {
			DNA dna = sums[i];
			int max = 0;
			char ch = ' ';
			
			for (Character c : dna.map.keySet()) {
				if (dna.map.get(c) > max) {
					ch = c;
				}
			}
			result[i] = ch;
		}
		
		return result;
	}
	
	public static String createNewString(char[] sum) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i< sum.length; i++) {
			if (sum[i] == 'A') {
				result.append('A');
			} else if (sum[i] == 'C') {
				result.append('C');
			} else if (sum[i] == 'G') {
				result.append('G');
			} else {
				result.append('T');
			}
		}
		
		return result.toString();
	}
	public static void KMeansDNASeq(int cluster, String inputFileName, int len) {	
		clusterNum = cluster;
		length = len;
		File file = new File(inputFileName);
		
		try {
			Scanner scan = new Scanner(file);			
			while (scan.hasNext()) {
				allStrands.add(scan.nextLine().trim());
			}
			scan.close();			
		} catch (FileNotFoundException e) {
			System.out.println("The file does not exist");
		}
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

	/* Initial separate all strands to each cluster.
	private static void firstCalculation(String[] centroids,
			String[] allStrands, DNASum[] strandsOnEach) {

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
			DNA dna = new DNA();
			for (int i = 0; i < strand.length(); i++) {
				dna.map.put(strand.charAt(i), dna.map.get(strand.charAt(i)));
			}
			strandsOnEach[minIndex].add(dna);
		}

		for (int i = 0; i < strandsOnEach.length; i++) {
			strandsOnEach[i].add(centroids[i]); // add centroid to array list of this cluster
		}
	}
	*/
	
	private static void firstCalculation(String[] centroids,
			String[] allStrands, DNASum[] strandsOnEach) {

		DNA dna = new DNA();
		for (String strand : allStrands) {
			for (int i = 0; i < strand.length(); i++) {
				char c = strand.charAt(i);
				dna.map.put(c, dna.map.get(c));
			}
		}
		strandsOnEach[0].sums[0] = dna;
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

	private static int calculateSimilarity(String cen, String strand) {
		int difference = 0;
		int index = 0;
		int length = cen.length();

		while (index < length) {
			if (cen.charAt(index) != strand.charAt(index)) {
				difference++;
			}
			index++;
		}

		return difference;
	}

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
	
	public void writeToTile(String output) throws IOException {
		FileWriter write = new FileWriter(output);
		for (int i = 0; i < clusterNum; i++) {
			for (String strand : strandsOnEach[i]) {
				write.append(strand + "\n");
				write.flush();
			}
		}
		write.close();
	}
}
