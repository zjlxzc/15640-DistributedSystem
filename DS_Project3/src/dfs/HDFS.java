package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the entry of this system.
 * It reads configuration file and set up the system.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public class HDFS {
	public static void main(String[] args) {
		File conf = new File(args[0]); // args[0] is configuration file
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line = "";
			String masterIP = "";
			int masterPort = 0;
			
			// read configuration file to set up the system
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");	
				if (pars[0].equals("master")) { // if this line has "master" keyword
					masterIP = pars[1]; // set ip address for master
					masterPort = Integer.parseInt(pars[2]); // set port for master
				}				
				
				// if this line has current machine information
				if (InetAddress.getLocalHost().getHostAddress().equals(pars[1])) {
					if (pars[0].equals("master")) { // new name node
						// if this machine is master, then create a name node instance
						new NameNode(args[0], Integer.parseInt(pars[2]));				
					} else {
						// if this machine is slave, then create a data node instance
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
