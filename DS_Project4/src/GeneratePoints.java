/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to generate Point dataset.
*/

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeneratePoints {
	
	public static void main(String[] args) {
		try {
			
			// use the user input to determine the size and range of the data set
			int pointsCnt = Integer.parseInt(args[0]);
			int min = Integer.parseInt(args[1]);
			int max = Integer.parseInt(args[2]);
			FileWriter fw = new FileWriter("pointsDataSet");
			Random rand = new Random();
			
			// for every point, randomly generate x and y
			while (pointsCnt > 0) {
				double x = min + (max -min) * rand.nextDouble();
				double y = min + (max - min) * rand.nextDouble();
				fw.write(x + " " + y + "\n");
				pointsCnt--;
			}
			fw.close();			
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	
}
