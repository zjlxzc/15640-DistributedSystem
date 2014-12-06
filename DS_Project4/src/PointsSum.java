import java.io.Serializable;

/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class used to store a sum of a serious of point.
*/

public class PointsSum implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double xSum;
	public double ySum;
	public int pointNum;
	public PointsSum() {
		xSum = 0;
		ySum = 0;
		pointNum = 0;
	}
	
	// This method is used to add one point to this sum
	public void add(Point p) {
		xSum += p.x;
		ySum += p.y;
		pointNum++;
	}
	
	// This method is used to add two sets of sum
	public void add(PointsSum sum) {
		xSum += sum.xSum;
		ySum += sum.ySum;
		pointNum += sum.pointNum;
	}
}
