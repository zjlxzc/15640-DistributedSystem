package HDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NameNode {
	
	private static DataNodeTable dataNodeTable;
	private static HashMap<String, ArrayList<BlockRef>> fileMap;
	private static int PORT;
	
	public NameNode(String confPath) {
		dataNodeTable = new DataNodeTable();
		File conf = new File(confPath);
		
		Thread countThread = new Thread(new InitCount());
		countThread.start();
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");				
				if (InetAddress.getLocalHost().getHostAddress().equals(pars[1])) {
					if (pars[0].equals("slave")) {
						dataNodeTable.addNode(pars[1], Integer.parseInt(pars[2]));
					} else {
						PORT = Integer.parseInt(pars[2]);
					}
				}
			}
			br.close();
			countThread = null;
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		new Thread(new Listen()).start();
		new Thread(new Main()).start();
	}
	
	private static void Usage() {
		System.out.println("Please enter command:\n");
		System.out.println("[L]ist all the nodes");
		System.out.println("[Q]uit");
		System.out.println("Please input:[S/L/E/Q]:");
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
	
	private class Listen implements Runnable {

		@Override
		public void run() {
			ServerSocket master;
			
			try {
				master = new ServerSocket(PORT);
				while (true) {
					Socket slave = master.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(slave.getInputStream()));
					String line = in.readLine();
					if (line.equals("report")) {
						new Thread(new UpdateSlave(slave)).start();
					}
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
	}
	
	private static class ListThread implements Runnable {		
		@Override
		public void run() {
			dataNodeTable.list();	
		}		
	}
	
	private static class UpdateSlave implements Runnable {
		private Socket slave;
		
		public UpdateSlave(Socket slave) {
			this.slave = slave;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			HashMap<String, ArrayList<BlockRef>> nodeReport = null;
			try {
				ObjectInputStream in = new ObjectInputStream(slave.getInputStream());
				nodeReport = (HashMap<String, ArrayList<BlockRef>>)in.readObject();
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
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
}

