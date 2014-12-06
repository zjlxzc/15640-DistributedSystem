/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to calculate k-means of a set of 2D points sequentially.
 * The input arguments is k cluster and input file name
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class PointKMeanSeq {

	public static void main(String[] args) {
		
		// record the start time
		System.out.println("MPI start to run");
		long startTime = System.currentTimeMillis();
		int k = Integer.parseInt(args[0]);
		
		String inputFileName = args[1];
		ArrayList<Point> pointList = loadData(inputFileName);
		
		Point[] centroids = randomPick(pointList, k);
		
		// initialize an k length array to store pointssum from different cluster
		PointsSum[] sums = new PointsSum[k];
		for (int i = 0; i < k; i++) {
			sums[i] = new PointsSum();
		}
		
		// define a finish condition
		boolean finish = false;
		while (!finish) {	
			calculateSum(centroids, pointList, sums);
			finish = !reCalculateMeans(centroids, sums);
		}
		
		// output the result and get the running time
		System.out.println(Arrays.toString(centroids));		
		long endTime = System.currentTimeMillis();
		System.out.println("MPI end");
		System.out.println("Time Taken to run MPI: " + (endTime - startTime) + "milliseconds");
	}
	
	// This method is used to load data to an arraylist for run the algorithm
	private static ArrayList<Point> loadData(String filename) {
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
		return pointList;	
	}
	
	// This method is used to randomly pick the initial centroids	
	private static Point[] randomPick(ArrayList<Point> pointList, int k) {
		// split the pointlist evenly by the number of cluster
		int size = pointList.size();
		int len = size / k;
		Random rand = new Random();
		
		// for every split, randomly pick one point to be the centroid
		Point[] centroids = new Point[k];
		for (int i = 0; i < k; i++) {
			centroids[i] = pointList.get(len * i + rand.nextInt(len));
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
			ArrayList<Point> pointList, PointsSum[] sums) {
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
