import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;

public class PointKMeanMPI {

	public PointKMeanMPI(int clusterNum, String inputFileName) {
		try {
			int myRank = MPI.COMM_WORLD.Rank();
			int[] lenArr = new int[1];						
			if (myRank == 0) {
				System.out.println("MPI start to run");
				long startTime = System.currentTimeMillis();
				Point[] pointList = loadData(inputFileName);
				Point[] centroids = randomPick(pointList, clusterNum);				
				int mpiNum = MPI.COMM_WORLD.Size();
				int len = pointList.length / (mpiNum - 1);
				lenArr[0] = len;
				for (int i = 1; i < mpiNum; i++) {
					Point[] listToCluster = new Point[len];
					System.arraycopy(pointList, (i - 1) * len, listToCluster, 0, len);	
					MPI.COMM_WORLD.Send(lenArr, 0, 1, MPI.INT, i, 0);
					MPI.COMM_WORLD.Send(listToCluster, 0, len, MPI.OBJECT, i, 1);					
				}							
				
				boolean finish = false;
				PointsSum[] sums = new PointsSum[clusterNum];

				while (!finish) {
					
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new PointsSum();
					}					
					for (int i = 1; i < mpiNum; i++) {
						MPI.COMM_WORLD.Send(centroids, 0, clusterNum, MPI.OBJECT, i, 2);
					}
					
					for (int i = 1; i < mpiNum; i++) {
						PointsSum[] localSum = new PointsSum[clusterNum];
						MPI.COMM_WORLD.Recv(localSum, 0, clusterNum, MPI.OBJECT, i, 3);
						for (int j = 0; j < clusterNum; j++) {
							sums[j].add(localSum[j]);
						}
					}										
					finish = !reCalculateMeans(centroids, sums);
				}
				
				System.out.println(Arrays.toString(centroids));
				long endTime = System.currentTimeMillis();
				System.out.println("MPI end");
				System.out.println("Time Taken to run MPI: " + (endTime - startTime) + "milliseconds");	
				System.exit(0);
			} else {				
				MPI.COMM_WORLD.Recv(lenArr, 0, 1, MPI.INT, 0, 0);
				int len = lenArr[0];
				Point[] localList = new Point[len];
				MPI.COMM_WORLD.Recv(localList, 0, len, MPI.OBJECT, 0, 1);
				Point[] localCentroids = new Point[clusterNum];
				
				while (true) {
					MPI.COMM_WORLD.Recv(localCentroids, 0, clusterNum, MPI.OBJECT, 0, 2);
					
					PointsSum[] sums = new PointsSum[clusterNum];
					for (int i = 0; i < clusterNum; i++) {
						sums[i] = new PointsSum();
					}
					calculateSum(localCentroids, localList, sums);
					MPI.COMM_WORLD.Send(sums, 0, sums.length, MPI.OBJECT, 0, 3);	
				}				
			}		
		} catch (MPIException e) {
			e.printStackTrace();
		}
	}
	
	private static void calculateSum(Point[] centroids,
			Point[] pointList, PointsSum[] sums) {
		
		for (Point point : pointList) {
			int minIndex = Integer.MAX_VALUE;
			double minDis = Double.MAX_VALUE;
			for (int i = 0; i < centroids.length; i++) {
				double dis = calculateDis(centroids[i], point);
				if (dis < minDis) {
					minIndex = i;
					minDis = dis;
				}
			}
			sums[minIndex].add(point);
		}		
	}
	
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
	
	private static Point[] randomPick(Point[] pointList, int k) {
		int size = pointList.length;
		int len = size / k;
		Random rand = new Random();
		Point[] centroids = new Point[k];
		for (int i = 0; i < k; i++) {
			centroids[i] = pointList[len * i + rand.nextInt(len)];
		}
		return centroids;
	}
	
	private static double calculateDis(Point cen, Point point) {
		double disX = Math.pow(point.x - cen.x, 2);
		double disY = Math.pow(point.y - cen.y, 2);
		double dis = Math.pow(disX + disY, 0.5);
		return dis;
	}
	
	private static boolean reCalculateMeans(Point[] centroids, PointsSum[] sums) {
		boolean change = false;
		int k = sums.length;
		for (int i = 0; i < k; i++) {
			double meanX = sums[i].xSum / sums[i].pointNum;
			double meanY = sums[i].ySum / sums[i].pointNum;
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