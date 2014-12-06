/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to calculate parallel KMeans on OpenMPI.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;

public class DNAKMeanMPI {

	private static String[] centroids; // an array to store centroid of each cluster
	private static final ArrayList<String> allStrands = new ArrayList<String>(); // store all input strands
	private static final char[] bases = {'A', 'C', 'G', 'T'}; // the DNA string will be generated from this finite set
	private static final int baseLength = 4; // the number of available characters
	private static int length = 0; // the number of characters per DNA strand has
	private static int clusterNum = 0;

	public DNAKMeanMPI(int clusterNumber, String inputFileName) {
		clusterNum = clusterNumber; // set cluster number
		centroids = new String[clusterNum]; // initialize string array to store centroid of each cluster
		
		try {
			int myRank = MPI.COMM_WORLD.Rank(); // get current rank
			int[] lenArr = new int[1];
			int times = 0;
			
			if (myRank == 0) { // if it is master
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
					MPI.COMM_WORLD.Send(listToCluster, 0, len, MPI.OBJECT, i, 1); // send strands to slaves				
				}	
				
				boolean finish = false;
				DNASum[] sums = new DNASum[clusterNum];
				String[] newCentroids = new String[clusterNum];
				
				while (!finish) {
					
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new DNASum(length); // initialize DNASum array
					}	
					
					for (int i = 1; i < mpiNum; i++) {
						MPI.COMM_WORLD.Send(centroids, 0, clusterNum, MPI.OBJECT, i, 2); // send centroids to slaves
					}
					
					for (int i = 1; i < mpiNum; i++) {
						DNASum[] localSum = new DNASum[clusterNum]; // get character sum from each process
						MPI.COMM_WORLD.Recv(localSum, 0, clusterNum, MPI.OBJECT, i, 3);
						
						for (int j = 0; j < clusterNum; j++) {
							sums[j].add(localSum[j]); // collect result from each cluster
						}
					}
					
					for (int k = 0; k < clusterNum; k++) {
						char[] frequency = getCharArray(sums[k]); 
						newCentroids[k] = new String(frequency); // calculate new centroids
					}
					
					if (times < 500) { // run 500 times
						finish = false;
						centroids = newCentroids;
						times++;
					} else {
						finish = true;
					}
				}
				
				System.out.println("Final Result: " + Arrays.toString(centroids));
				long endTime = System.currentTimeMillis();
				System.out.println("MPI end: " + endTime);
				System.out.println("Time Taken: " + (endTime - startTime)); // print out running time
				
			} else {
				MPI.COMM_WORLD.Recv(lenArr, 0, 1, MPI.INT, 0, 0);
				int len = lenArr[0];
				String[] localList = new String[len];
				
				MPI.COMM_WORLD.Recv(localList, 0, len, MPI.OBJECT, 0, 1); // receive data set from master
				String[] localCentroids = new String[clusterNum];
				
				while (true) {
					MPI.COMM_WORLD.Recv(localCentroids, 0, clusterNum, MPI.OBJECT, 0, 2); // receive centroids from master
					DNASum[] sums = new DNASum[clusterNum];
					
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new DNASum(localList[0].length());
					}
					
					calculate(localCentroids, localList, sums); // do calculation of each cluster
					MPI.COMM_WORLD.Send(sums, 0, sums.length, MPI.OBJECT, 0, 3); // send result back to master
				}				
			}				
		} catch (MPIException e) {
			System.out.println("In DNAKMeanMPI Calculation: " + e.getMessage());
		}
	}
	
	// return new centroid character array based on the frequency of each character on each position
	public char[] getCharArray(DNASum sum) {
		DNA[] sums = sum.sums;
		char[] result = new char[length];
		
		for (int i = 0; i < length; i++) {
			DNA dna = sums[i];
			int max = -1;
			char ch = 'A';
			
			for (Character c : dna.map.keySet()) {
				if (dna.map.get(c) > max) { // get the character with the max frequency
					max = dna.map.get(c);
					ch = c;
				}
			}
			result[i] = ch;
		}
		
		return result;
	}

	// read data from input file and store the data to an array list
	public static void KMeansDNASeq(int cluster, String inputFileName) {	
		clusterNum = cluster;
		File file = new File(inputFileName);
		
		try {
			Scanner scan = new Scanner(file);			
			while (scan.hasNext()) {
				allStrands.add(scan.nextLine().trim());
			}
			
			length = allStrands.get(0).length(); // get the length of a strand
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
			int minIndex = 10;
			int minDis = 11;
			
			for (int i = 0; i < clusterNum; i++) { // compare each strand with all centroids to get the minimum value
				int dis = calculateSimilarity(localCentroids[i], strand);
				if ((dis - minDis) < 0) {
					minIndex = i;
					minDis = dis;
				}
			}
			strandsOnEach[minIndex].addString(strand);
		}
	}

	// calculate the similarity of two strands
	private static int calculateSimilarity(String cen, String strand) {
		int difference = 0;
		int index = 0;

		while (index < 10) {
			if (cen.charAt(index) != strand.charAt(index)) { // if there are different characters
				difference++;
			}
			index++;
		}
		return difference;
	}
}
