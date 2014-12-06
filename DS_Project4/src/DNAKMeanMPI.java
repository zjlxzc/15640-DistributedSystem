import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;

public class DNAKMeanMPI {

	private static String[] centroids; // an array to store centroid of each cluster
	//private static ArrayList<String>[] strandsOnEach; // store strands for each cluster
	private static final ArrayList<String> allStrands = new ArrayList<String>(); // store all input strands
	//private static final char[] bases = { 'A', 'C', 'G', 'T' }; // the DNA string will be generated from this finite set
	//private static final int baseLength = 4; // the number of available characters
	private static int length = 0; // the number of characters per DNA strand has
	private static int clusterNum = 0;

	public DNAKMeanMPI(int clusterNumber, String inputFileName) {
		clusterNum = clusterNumber;
		centroids = new String[clusterNum];
		try {
			int myRank = MPI.COMM_WORLD.Rank(); // get current rank
			int[] lenArr = new int[1];
			
			if (myRank == 0) { // if it is node0
				long startTime = System.currentTimeMillis();
				System.out.println("MPI start to run: " + startTime);
				KMeansDNASeq(clusterNum, inputFileName); // store all input strands to an array list 	
				centroids = randomPick(allStrands); // pick centroid randomly
				
				int mpiNum = MPI.COMM_WORLD.Size(); // total number of processes
				int len = allStrands.size() / (mpiNum - 1);	// the input size of each process	
				lenArr[0] = len;
				
				for (int i = 1; i < mpiNum; i++) {
					String[] listToCluster = new String[len];
					System.arraycopy(allStrands.toArray(), (i - 1) * len, listToCluster, 0, len);	
					MPI.COMM_WORLD.Send(lenArr, 0, 1, MPI.INT, i, 0);
					MPI.COMM_WORLD.Send(listToCluster, 0, len, MPI.OBJECT, i, 1);					
				}	
				
				boolean finish = false;
				DNASum[] sums = new DNASum[clusterNum];

				while (!finish) {
					
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new DNASum(length); // initialize DNASum array
					}	
					
					for (int i = 1; i < mpiNum; i++) {
						MPI.COMM_WORLD.Send(centroids, 0, clusterNum, MPI.OBJECT, i, 2); // send centroid
					}
					
					String[] newCentroids = new String[clusterNum];
					for (int i = 1; i < mpiNum; i++) {
						DNASum[] localSum = new DNASum[clusterNum]; // get character sum from each process
						MPI.COMM_WORLD.Recv(localSum, 0, clusterNum, MPI.OBJECT, i, 3);
					
						for (int j = 0; j < clusterNum; j++) {
							sums[j].add(localSum[j]);
						}
					}
					
					for (int k = 0; k < clusterNum; k++) {
						char[] frequency = getCharArray(sums[k]);
						newCentroids[k] = new String(frequency);
					}
					
					if (!compareEqual(newCentroids)) {
						finish = false;
						centroids = newCentroids;
					} else {
						finish = true;
					}
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
						sums[i] = new DNASum(10);
					}
					
					calculate(localCentroids, localList, sums);
					
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
			int max = -1;
			char ch = 'A';
			
			for (Character c : dna.map.keySet()) {
				if (dna.map.get(c) > max) {
					max = dna.map.get(c);
					ch = c;
				}
			}
			result[i] = ch;
		}
		
		return result;
	}

	public static void KMeansDNASeq(int cluster, String inputFileName) {	
		clusterNum = cluster;
		File file = new File(inputFileName);
		
		try {
			Scanner scan = new Scanner(file);			
			while (scan.hasNext()) {
				allStrands.add(scan.nextLine().trim());
			}
			length = allStrands.get(0).length();
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
	
	private static void calculate(String[] localCentroids, String[] allStrands, DNASum[] strandsOnEach) {

		for (String strand : allStrands) {
			int minIndex = Integer.MAX_VALUE;
			int minDis = Integer.MAX_VALUE;
			
			for (int i = 0; i < clusterNum; i++) { // compare each strand with all centroids to get the minimum value
				int dis = calculateSimilarity(centroids[i], strand);
				if (dis < minDis) {
					minIndex = i;
					minDis = dis;
				}
			}
			strandsOnEach[minIndex].addString(strand);
		}
	}

	private static int calculateSimilarity(String cen, String strand) {
		int difference = 0;
		int index = 0;

		while (index < length) {
			if (cen.charAt(index) != strand.charAt(index)) {
				difference++;
			}
			index++;
		}

		return difference;
	}

	public static boolean compareEqual(String[] newCentral) {
		
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
