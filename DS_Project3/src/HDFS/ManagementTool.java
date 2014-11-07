package HDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import managementTool.Job;
import managementTool.JobTracker;

public class ManagementTool {
	private static NodeTable nodeTable;
	private static Node me;
	public static void main(String[] args) {
		init();
		Scanner scan = new Scanner(System.in);
		while (true) {
			Usage();
			String str = scan.nextLine();
			if (str.equals("L")){
				new Thread(new ListThread()).start();
			} else if (str.equals("E")) {
				new Thread(new SubmitJob()).start();
			}
		}
	}	
	
	private static void Usage() {
		System.out.println("Please enter command:\n");
		System.out.println("[L]ist all the nodes");
		System.out.println("[E]xcute job");
		System.out.println("[Q]uit");
		System.out.println("Please input:[S/L/E/Q]:");
	}
	
	private static void init() {	
		System.out.println("Please input configuration file:");
		Scanner scan = new Scanner(System.in);
		String confPath = scan.nextLine();
		File conf = new File(confPath);
		
		Thread countThread = new Thread(new InitCount());
		countThread.start();
		nodeTable = new NodeTable();
				
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");
				nodeTable.addNode(pars[0], Integer.parseInt(pars[1]), pars[2]);
				if ((InetAddress.getLocalHost().getHostAddress()))
			}
			br.close();
			countThread = null;
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	private static class InitCount implements Runnable {
		@Override
		public void run() {
			System.out.print("Initializing");
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.print(".");
			}			
		}		
	}	
	
	private static class ListThread implements Runnable {		
		@Override
		public void run() {
			nodeTable.list();	
		}		
	}
	
	private static class SubmitJob implements Runnable {
		@Override
		public void run() {
			Scanner scan = new Scanner(System.in);
			System.out.println("Please input Mapper File");
			String mapperPath = scan.nextLine();
			System.out.println("Please input Reducer File");
			String reducerPath = scan.nextLine();
			System.out.println("Please input Input File");
			String inputPath = scan.nextLine();
			System.out.println("Please input Output File");
			String outputPath = scan.nextLine();
			
			if (me instanceof NameNode) {
				JobTracker tracker = ((NameNode) me).getJobTracker();
				int curNum = tracker.getJobNum();
				Job job = new Job(curNum + 1, mapperPath, reducerPath, inputPath, outputPath);
				tracker.excuteJob(job);
			} else {
				
			}
		}		
	}
}
