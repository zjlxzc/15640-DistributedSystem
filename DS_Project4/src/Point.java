import java.io.Serializable;
import java.text.DecimalFormat;


public class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public Point() {}
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public String toString() {
		DecimalFormat format = new DecimalFormat("0.000"); 
		return "(" + format.format(x) + "," + format.format(y) + ")";
	}
}
