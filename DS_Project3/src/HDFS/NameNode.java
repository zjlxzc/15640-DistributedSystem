package HDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class NameNode {
	
	private static DataNodeTable dataNodeTable;
	private static Hashtable<String, ArrayList<BlockRef>> fileTable;
	private static int BLOCK_SIZE;
	private static int REPLICA_FACTOR;
	
	public NameNode(String confPath) {
		dataNodeTable = new DataNodeTable();
		File conf = new File(confPath);
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf));
			String line;
			Socket master = null;
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");	
				if (pars[0].equals("slave")) {
					dataNodeTable.addNode(pars[1], Integer.parseInt(pars[2]));
				} else if (pars[0].equals("BlockSize")) {
					BLOCK_SIZE = Integer.parseInt(pars[1]);
				} else if (pars[0].equals("ReplicaFactor")) {
					REPLICA_FACTOR = Integer.parseInt(pars[1]);
				}	
			}
			ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes();
			NodeRef cur;				
			for (int i = 0; i < nodeList.size(); i++) {
				cur =  nodeList.get(i);
				master = new Socket(cur.getIp(), cur.getPort());
				PrintWriter out = new PrintWriter(master.getOutputStream(), true);
				out.println("BlockSize");
				out.println(BLOCK_SIZE);
			}	
			master.close();
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		new Thread(new Poll()).start();
		new Thread(new Main()).start();
		new Thread(new RMIServer()).start();
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
				} else if (str.equals("Q")) {
					System.exit(0);
				}
			}
		}
	}	
	
	private class Poll implements Runnable {

		@Override
		public void run() {
			Socket master;	
			int i = 0;
			try {				
				while (true) {
					ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes();
					NodeRef cur;
					for (i = 0; i < nodeList.size(); i++) {
						Thread.sleep(5000);
						master = new Socket();
						cur =  nodeList.get(i);
						master.connect(new InetSocketAddress(cur.getIp(), cur.getPort()), 1000);
					}		
				}						
			} catch (IOException e) {
				System.out.println(e.getMessage());
				NodeRef errorNode = dataNodeTable.getDataNodes().get(i);
				System.out.print(errorNode.getIp() + " : " + errorNode.getPort() + " : lost connection");
			} catch (InterruptedException e) {
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
	
	private static class RMIServer implements Runnable {

		@Override
		public void run() {
//			if (System.getSecurityManager() == null) {
//	            System.setSecurityManager(new SecurityManager());
//	        }
	        try {
	            String name = "NameNodeRMI";
	            NameNodeRMI rmi = new NameNodeRMIServent(fileTable, dataNodeTable, REPLICA_FACTOR);
	            NameNodeRMI stub =
	                (NameNodeRMI) UnicastRemoteObject.exportObject(rmi, 0);
	            Registry registry = LocateRegistry.getRegistry();
	            System.out.println(name);
	            registry.rebind(name, stub);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}		
	}	
}

