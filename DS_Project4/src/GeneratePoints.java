import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class GeneratePoints {
	
	public static void main(String[] args) {
		try {
			int pointsCnt = Integer.parseInt(args[0]);
			int min = Integer.parseInt(args[1]);
			int max = Integer.parseInt(args[2]);
			FileWriter fw = new FileWriter("pointsDataSet");
			Random rand = new Random();
			
			while (pointsCnt > 0) {
				double x = min + (max -min) * rand.nextDouble();
				double y = min + (max - min) * rand.nextDouble();
				fw.write(x + " " + y + "\n");
				pointsCnt--;
			}
			fw.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
