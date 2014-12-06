import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;

/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to calculate k-means of a set of 2D points on OpenMPI.
 * 
*/
public class PointKMeanMPI {

	public PointKMeanMPI(int clusterNum, String inputFileName) {
		try {
			// get the current rank
			int myRank = MPI.COMM_WORLD.Rank();
			int[] lenArr = new int[1];	
			
			// if this is the master
			if (myRank == 0) {
				
				// record start time
				System.out.println("MPI start to run");
				long startTime = System.currentTimeMillis();
				
				// load data from data set
				Point[] pointList = loadData(inputFileName);
				
				// randomly pick centroids
				Point[] centroids = randomPick(pointList, clusterNum);	
				
				// get the number of slaves and the length of data split
				int mpiNum = MPI.COMM_WORLD.Size();
				int len = pointList.length / (mpiNum - 1);
				lenArr[0] = len;
				
				// split the data, for every slave, send one split
				for (int i = 1; i < mpiNum; i++) {
					Point[] listToCluster = new Point[len];
					System.arraycopy(pointList, (i - 1) * len, listToCluster, 0, len);	
					MPI.COMM_WORLD.Send(lenArr, 0, 1, MPI.INT, i, 0);
					MPI.COMM_WORLD.Send(listToCluster, 0, len, MPI.OBJECT, i, 1);					
				}	
				
				// define the finish condition
				boolean finish = false;
				PointsSum[] sums = new PointsSum[clusterNum];

				while (!finish) {
					// initial a pointSum array for every cluster
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new PointsSum();
					}	
					
					// send the centroids to slaves
					for (int i = 1; i < mpiNum; i++) {
						MPI.COMM_WORLD.Send(centroids, 0, clusterNum, MPI.OBJECT, i, 2);
					}
					
					// get the result from slaves and add them together
					for (int i = 1; i < mpiNum; i++) {
						PointsSum[] localSum = new PointsSum[clusterNum];
						MPI.COMM_WORLD.Recv(localSum, 0, clusterNum, MPI.OBJECT, i, 3);
						for (int j = 0; j < clusterNum; j++) {
							sums[j].add(localSum[j]);
						}
					}	
					
					// recalculate the means and determine if need to continue
					finish = !reCalculateMeans(centroids, sums);
				}
				
				// output the result and count the time
				System.out.println(Arrays.toString(centroids));
				long endTime = System.currentTimeMillis();
				System.out.println("MPI end");
				System.out.println("Time Taken to run MPI: " + (endTime - startTime) + "milliseconds");	
				System.exit(0);
				
			} else {	// if this is slave
				
				// get the data split
				MPI.COMM_WORLD.Recv(lenArr, 0, 1, MPI.INT, 0, 0);
				int len = lenArr[0];
				Point[] localList = new Point[len];
				MPI.COMM_WORLD.Recv(localList, 0, len, MPI.OBJECT, 0, 1);
				Point[] localCentroids = new Point[clusterNum];
				
				while (true) {
					// get the centroids from master
					MPI.COMM_WORLD.Recv(localCentroids, 0, clusterNum, MPI.OBJECT, 0, 2);
					
					// initial a local PointSum array for every cluster
					PointsSum[] sums = new PointsSum[clusterNum];
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new PointsSum();
					}
					// calculate the data in the data split and get pointsum for every cluster
					calculateSum(localCentroids, localList, sums);
					
					// send back the result
					MPI.COMM_WORLD.Send(sums, 0, sums.length, MPI.OBJECT, 0, 3);	
				}				
			}		
		} catch (MPIException e) {
			e.printStackTrace();
		}
	}
	
	// This method load the dataset from file and store them into an array
	private static Point[] loadData(String filename) {
		File file = new File(filename);
		ArrayList<Point> pointList = new ArrayList<Point>();
		try {
			Scanner scan = new Scanner(file);			
			while (scan.hasNext()) {
				String[] str = scan.nextLine().split(" ");
				double x = Double.parseDouble(str[0]);
				double y = Double.parseDouble(str[1]);
				Point cur = new Point(x, y);
				pointList.add(cur);
			}
			scan.close();			
		} catch (FileNotFoundException e) {
			System.out.println("The file does not exist");
		}
		Point[] points = new Point[pointList.size()];
		points = pointList.toArray(points);
		return points;	
	}
	
	// This method is used to randomly pick the initial centroids	
	private static Point[] randomPick(Point[] pointList, int k) {
		// split the pointlist evenly by the number of cluster
		int size = pointList.length;
		int len = size / k;
		Random rand = new Random();
		
		// for every split, randomly pick one point to be the centroid
		Point[] centroids = new Point[k];
		for (int i = 0; i < k; i++) {
			centroids[i] = pointList[len * i + rand.nextInt(len)];
		}
		return centroids;
	}
	
	// This method is used to calculate the Euclidean distance for two points
	private static double calculateDis(Point cen, Point point) {
		double disX = Math.pow(point.x - cen.x, 2);
		double disY = Math.pow(point.y - cen.y, 2);
		double dis = Math.pow(disX + disY, 0.5);
		return dis;
	}
	
	// This method is used to calculate the PointSum of every cluster
	private static void calculateSum(Point[] centroids,
			Point[] pointList, PointsSum[] sums) {
		// for every point
		for (Point point : pointList) {
			int minIndex = Integer.MAX_VALUE;
			double minDis = Double.MAX_VALUE;
			
			//calculate the distance between the point and centroids and get the nearest one
			for (int i = 0; i < centroids.length; i++) {
				double dis = calculateDis(centroids[i], point);
				if (dis < minDis) {
					minIndex = i;
					minDis = dis;
				}
			}
			// add this point to the Pointsum of the nearest centroid
			sums[minIndex].add(point);
		}		
	}
	
	// This method is used to recalculate the centroids and determine if it is changed
	private static boolean reCalculateMeans(Point[] centroids, PointsSum[] sums) {
		boolean change = false;
		int k = sums.length;
		
		// for every pointsum of centroid, divide sum by point number, can get the cluster's new centroid
		for (int i = 0; i < k; i++) {
			double meanX = sums[i].xSum / sums[i].pointNum;
			double meanY = sums[i].ySum / sums[i].pointNum;
			
			// compare the position of new centroid and the old one
			// if exceeds some threshold, the it is changed, program should continue
			if (Math.abs(meanX - centroids[i].x) > 0.001 || 
					Math.abs(meanY - centroids[i].y) > 0.001) {
				change = true;
			}
			Point newCen = new Point(meanX, meanY);
			centroids[i] = newCen;
		}
		return change;
	}
}