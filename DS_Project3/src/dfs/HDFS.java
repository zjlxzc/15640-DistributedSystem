package dfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class HDFS {
	public static void main(String[] args) {
		File conf = new File(args[0]);
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			String masterIP = "";
			int masterPort = 0;
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");	
				if (pars[0].equals("master")) {
					masterIP = pars[1];
					masterPort = Integer.parseInt(pars[2]);
				}				
				if (InetAddress.getLocalHost().getHostAddress().equals(pars[1])) {
					if (pars[0].equals("master")) {
						new NameNode(args[0], Integer.parseInt(pars[2]));						
					} else {
						new DataNode(Integer.parseInt(pars[2]), masterIP, masterPort);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}	
}
