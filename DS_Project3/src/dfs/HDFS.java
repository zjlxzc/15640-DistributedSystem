package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the entry of this system.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public class HDFS {
	public static void main(String[] args) {
		File conf = new File(args[0]);
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			String masterIP = "";
			int masterPort = 0;
			
			// read configuration file to set up the system
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");	
				if (pars[0].equals("master")) { // if this line has master information
					masterIP = pars[1]; // set master ip address
					masterPort = Integer.parseInt(pars[2]); // set master port
				}				
				
				// if this line has current machine information
				if (InetAddress.getLocalHost().getHostAddress().equals(pars[1])) {
					if (pars[0].equals("master")) { // new name node
						new NameNode(args[0], Integer.parseInt(pars[2]));						
					} else { // new data node
						new DataNode(Integer.parseInt(pars[2]), masterIP, masterPort);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}	
}
