package HDFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataNode {
	HashMap<String, ArrayList<BlockRef>> fileMap;
	public DataNode() {
		new Thread(new Main()).start();
		fileMap = new HashMap<String, ArrayList<BlockRef>>();
	}
	
	private class Main implements Runnable {				
		@Override
		public void run() {			
			Scanner scan = new Scanner(System.in);
			while (true) {
				Usage();
				String str = scan.nextLine();
				if (str.equals("L")){
					new Thread(new ListThread()).start();
				}
			}
		}
	}
}
