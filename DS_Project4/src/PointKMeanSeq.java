import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class PointKMeanSeq {

	public static void main(String[] args) {
		int k = Integer.parseInt(args[0]);
		ArrayList<Point> pointList = loadData("pointsDataSet");
		Point[] centroids = randomPick(pointList, k);
		PointsSum[] sums = new PointsSum[k];
		for (int i = 0; i < k; i++) {
			sums[i] = new PointsSum();
		}
		boolean finish = false;
		while (!finish) {			
			calculateSum(centroids, pointList, sums);
			finish = !reCalculateMeans(centroids, sums);
		}
		  
		System.out.println(Arrays.toString(centroids));		
	}
	
	private static void calculateSum(Point[] centroids,
			ArrayList<Point> pointList, PointsSum[] sums) {
		
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
	
	private static Point[] randomPick(ArrayList<Point> pointList, int k) {
		int size = pointList.size();
		int len = size / k;
		Random rand = new Random();
		Point[] centroids = new Point[k];
		for (int i = 0; i < k; i++) {
			centroids[i] = pointList.get(len * i + rand.nextInt(len));
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

			if (Math.abs(meanX - centroids[i].x) > 0.00001 || 
					Math.abs(meanY - centroids[i].y) > 0.00001) {
				change = true;
			}
			Point newCen = new Point(meanX, meanY);
			centroids[i] = newCen;
		}
		return change;
	}
}
