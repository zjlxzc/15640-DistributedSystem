package HDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class StartDFS {
	public static void main(String[] args) {
		System.out.println("Please input configuration file:");
		Scanner scan = new Scanner(System.in);
		String confPath = scan.nextLine();
		File conf = new File(confPath);
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");
				if (InetAddress.getLocalHost().getHostAddress().equals(pars[1])) {
					if (pars[2].equals("master")) {
						new NameNode(confPath);
					} else {
						new DataNode();
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
