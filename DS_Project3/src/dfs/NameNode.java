package dfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class NameNode {
	
	private DataNodeTable dataNodeTable;
	private Hashtable<String, Hashtable<NodeRef, ArrayList<BlockRef>>> metaTable;
	private int BLOCK_SIZE;
	private int REPLICA_FACTOR;
	private int PORT;
	
	public NameNode(String confPath, int port) {
		this.dataNodeTable = new DataNodeTable();
		this.metaTable = new Hashtable<String, Hashtable<NodeRef, ArrayList<BlockRef>>>();
		File conf = new File(confPath);
		this.PORT = port;
						
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
		new Thread(new ListenToSlave()).start();
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
	
	private class ListThread implements Runnable {		
		@Override
		public void run() {
			dataNodeTable.list();	
		}		
	}
	
	private class ListenToSlave implements Runnable {

		@Override
		public void run() {
			ServerSocket master = null;
			try {
				master = new ServerSocket(PORT);
				while(true) {
					Socket slave = master.accept();
					ObjectOutputStream out = new ObjectOutputStream(slave.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(slave.getInputStream());
					String first = (String)in.readObject();
					if (first.equals("addBlock")) {
						new Thread(new BlockAdder(in, out)).start();
					} else if (first.equals("update")) {
						new Thread(new Updater(slave)).start();
					}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					master.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}	
	
	private class BlockAdder implements Runnable {
		private ObjectOutputStream out = null;
		private ObjectInputStream in = null;
		public BlockAdder(ObjectInputStream in, ObjectOutputStream out) {
			this.out = out;
			this.in = in;
		}
		
		@Override
		public void run() {		
			try {				
				String fileName = (String)in.readObject();
				BlockRef sourceBlock = (BlockRef)in.readObject();
				System.out.println("Get the block add request from: " + sourceBlock.getNodeRef().getIp());
				NodeRef sourceNode = sourceBlock.getNodeRef();
				ArrayList<NodeRef> ret = new ArrayList<NodeRef>();
				ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes();
				Random rand = new Random();
				if (!metaTable.contains(fileName)) {
					int cnt = 1;
					int total = nodeList.size();
					while (cnt < REPLICA_FACTOR) {
						int index = 0;
						NodeRef des = null;
						while (des == null || des.getIp().equals(sourceNode.getIp())) {
							index = rand.nextInt(total);
							des = nodeList.get(index);						
						}
						ret.add(des);
						System.out.println("First: send the block des: " + des.getIp());
						cnt++;
					}
					Hashtable<NodeRef, ArrayList<BlockRef>> newNodeTable = new Hashtable<NodeRef, ArrayList<BlockRef>>();
					ArrayList<BlockRef> newBlockList = new ArrayList<BlockRef>();
					newBlockList.add(sourceBlock);
					newNodeTable.put(sourceNode, newBlockList);
					metaTable.put(fileName, newNodeTable);
				} else {
					HashMap<NodeRef, Integer> freq = new HashMap<NodeRef, Integer>();
					Hashtable<NodeRef, ArrayList<BlockRef>> map = metaTable.get(fileName);
					for (NodeRef node : map.keySet()) {
						freq.put(node, map.get(node).size());
					}
					for (NodeRef node : nodeList) {
						if (!freq.containsKey(node)) {
							freq.put(node, 0);
						}
					}
					List<Map.Entry<NodeRef,Integer>> sort=new ArrayList<>();  
					sort.addAll(freq.entrySet());  
					Collections.sort(sort, new ValueComparator());
					int cnt = 1;
					int total = sort.size();
					while (cnt < REPLICA_FACTOR) {
						int index = 0;
						NodeRef des = null;
						while (des == null || des.getIp().equals(sourceNode.getIp())) {
							index = rand.nextInt(total);
							des = sort.get(index).getKey();							
						}
						ret.add(des);
						System.out.println("Second: send the block des: " + des.getIp());
						cnt++;
					}
					Hashtable<NodeRef, ArrayList<BlockRef>> nodeTable = metaTable.get(fileName);
					ArrayList<BlockRef> blockList = nodeTable.get(sourceNode);
					blockList.add(sourceBlock);
					nodeTable.put(sourceNode, blockList);
					metaTable.put(fileName, nodeTable);
				}
				out.writeObject(ret);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private class ValueComparator implements Comparator<Map.Entry<NodeRef, Integer>> {    
	        public int compare(Map.Entry<NodeRef, Integer> mp1, Map.Entry<NodeRef, Integer> mp2) {    
	            return mp1.getValue() - mp2.getValue();    
	        }    
	    }
	}
	
	private class Updater implements Runnable {
		private Socket slave;
		public Updater(Socket slave) {
			this.slave = slave;
		}
		
		@Override
		public void run() {		
			try {		
				ObjectInputStream in = new ObjectInputStream(slave.getInputStream());
				NodeRef sourceNode = dataNodeTable.getDataNode(slave.getRemoteSocketAddress().toString());
				
				@SuppressWarnings("unchecked")
				Hashtable<String, ArrayList<BlockRef>> nodeTable = 
						(Hashtable<String, ArrayList<BlockRef>>)in.readObject();
				for (String filename : nodeTable.keySet()) {
					Hashtable<NodeRef, ArrayList<BlockRef>> fileTable = metaTable.get(filename);
					fileTable.put(sourceNode, nodeTable.get(filename));
				}			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

