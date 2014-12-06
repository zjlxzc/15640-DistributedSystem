/**
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This is the main entry of the k-mean algorithm.
*/

import mpi.MPI;
import mpi.MPIException;

public class MainEntry {

	public static void main(String[] args) {		
		
		try {
			MPI.Init(args);
			int clusterNum = 0;
			String inputFileName = "";
			String type = "";
			if (args.length == 3) {
				clusterNum = Integer.parseInt(args[0]);
				inputFileName = args[1];
				type = args[2];	
			} else {
				System.out.println("Please input correct command");				
			}
			
			if (type.equals("points")) {
				new PointKMeanMPI(clusterNum, inputFileName);
			} else {
				new DNAKMeanMPI(clusterNum, inputFileName);
			}			
			MPI.Finalize();
		} catch (MPIException e) {
			System.out.println("MPI Exception: " + e.getMessage());
		}
	}
}
