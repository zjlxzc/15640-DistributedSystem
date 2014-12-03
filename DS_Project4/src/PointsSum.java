
public class PointsSum {
	public double xSum;
	public double ySum;
	public int pointNum;
	public PointsSum() {
		xSum = 0;
		ySum = 0;
		pointNum = 0;
	}
	public void add(Point p) {
		xSum += p.x;
		ySum += p.y;
		pointNum++;
	}
	
	public void add(PointsSum sum) {
		xSum += sum.xSum;
		ySum += sum.ySum;
		pointNum += sum.pointNum;
	}
}
