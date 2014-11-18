package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a name node (master).
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import mapReduce.JobTracker;

public class NameNode {
	
	private DataNodeTable dataNodeTable;
	
	//table<filename, table<nodeIp, blockList>>
	private Hashtable<String, Hashtable<String, ArrayList<BlockRef>>> metaTable;
	private int BLOCK_SIZE;
	private int REPLICA_FACTOR;
	private int PORT;
	
	public NameNode(String confPath, int port) {
		this.dataNodeTable = new DataNodeTable();
		this.metaTable = new Hashtable<String, Hashtable<String, ArrayList<BlockRef>>>();
		File conf = new File(confPath); // get configuration file
		this.PORT = port; // get port
						
		try {
			BufferedReader br = new BufferedReader(new FileReader(conf)); // read configuration file
			String line;
			Socket master = null;
			
			while ((line = br.readLine()) != null) {
				String[] pars = line.split(" ");	
				
				if (pars[0].equals("slave")) {
					dataNodeTable.addNode(pars[1], Integer.parseInt(pars[2]));
				} else if (pars[0].equals("BlockSize")) { // get the size of a block
					BLOCK_SIZE = Integer.parseInt(pars[1]);
				} else if (pars[0].equals("ReplicaFactor")) { // get the replication factor
					REPLICA_FACTOR = Integer.parseInt(pars[1]);
				}	
			}
			
			ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes(); // get all data nodes
			NodeRef cur;				
			
			for (int i = 0; i < nodeList.size(); i++) {
				cur =  nodeList.get(i);
				master = new Socket(cur.getIp(), cur.getPort()); // for each data node, establish connection
				
				ObjectOutputStream out = new ObjectOutputStream(master.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(master.getInputStream());
				
				out.writeObject("BlockSize");
				out.writeObject(BLOCK_SIZE);
				out.flush(); // write the above information to data node
				
				in.close();
				out.close();
				master.close();
			}				
			
			br.close();
			
			new Thread(new Main()).start();
			new Thread(new ListenToSlave()).start();
			for (NodeRef node : nodeList) {
				new Thread(new Poll(node)).start();
			}
		} catch (FileNotFoundException e) {
			System.out.println("The input file path does not exist");
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	private static void Usage() {
		System.out.println("Please enter command:");
		System.out.println("To list all the nodes information: nodes");
		System.out.println("To list all the files information: files");
		System.out.println("To list all the jobs information: jobs");
		System.out.println("To quit the system: quit");
	}

	
	private class Main implements Runnable {				
		@Override
		public void run() {		
			Usage();
			Scanner scan = new Scanner(System.in);
			
			while (true) {								
				String[] str = scan.nextLine().split(" ");
				
				if (str[0].equals("files")){
					System.out.println("Files distribution on DFS:");
					System.out.println("===================================================");
					
					new Thread(new ListFileThread()).start();
				} else if (str[0].equals("quit")) {
					System.out.println("ByeBye");
					scan.close();
					
					System.exit(0);
				} else if (str[0].equals("jobs")) {
					JobTracker jobTracker = JobTracker.getInstance();
					jobTracker.ListJobs();					
				} else if (str[0].equals("nodes")) {
					System.out.println("DataNodes available on DFS:");
					System.out.println("===================================================");
					new Thread(new ListNodeThread()).start();
				} else {
					System.out.println("The input command is wrong.");
					Usage();
				}				
			}
		}
	}	
	
	private class Poll implements Runnable {
		
		NodeRef dataNode;
		public Poll(NodeRef dataNode) {
			this.dataNode = dataNode;
		}
				
		@Override
		public void run() {
			Socket master = null;
			ObjectOutputStream out;
			ObjectInputStream in;
			int port = 0;
			try {	
				master = new Socket(dataNode.getIp(), dataNode.getPort());
				out = new ObjectOutputStream(master.getOutputStream());				
				out.writeObject("BeginPolling");
				out.flush();
				Thread.sleep(1000);
				in = new ObjectInputStream(master.getInputStream());
				port = Integer.parseInt((String)in.readObject());
				in.close();
				out.close();
				master.close();
											
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			try {
				master = new Socket(dataNode.getIp(), port);
				out = new ObjectOutputStream(master.getOutputStream());
				in = new ObjectInputStream(master.getInputStream());
				while (true) {
					Thread.sleep(5000);
					out.writeObject("polling");
					out.flush();
				}	
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println(dataNode.getIp()  +  " : lost connection");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
	}
	
	private class ListFileThread implements Runnable {		
		@Override
		public void run() {
			if (metaTable == null || metaTable.isEmpty()) {
				System.out.println("There is no file in the system!");
			} else {
				for (String file : metaTable.keySet()) {
					System.out.println("File Name : " + file);
					
					Hashtable<String, ArrayList<BlockRef>> curTable = metaTable.get(file);
					for (String ip : curTable.keySet()) {
						System.out.println(ip);
						
						for (BlockRef block : curTable.get(ip)) {
							System.out.print(block.getFileName() + " "); // get file information
						}
						System.out.println();
					}
					System.out.println("===================================================");
				}
			}			
		}		
	}
	
	private class ListNodeThread implements Runnable {		
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
					Socket slave = master.accept(); // establish connection with data node
					ObjectInputStream in = new ObjectInputStream(slave.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(slave.getOutputStream());
					
					String first = (String)in.readObject(); // get information sent by data node
					if (first.equals("addBlock")) {
						new Thread(new BlockAdder(in, out)).start();
					} else if (first.equals("update")) {
						new Thread(new Updater(in, out)).start();
					} else if (first.equals("MapReduceNewJob")) {
						new Thread(new MapReduceJob(in, out)).start();
					}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					master.close();
				} catch (IOException e) {
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
				String fileName = (String)in.readObject(); // get filename
				BlockRef sourceBlock = (BlockRef)in.readObject(); // get block reference
				NodeRef sourceNode = sourceBlock.getNodeRef(); // get source node information
				
				ArrayList<NodeRef> ret = new ArrayList<NodeRef>();
				ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes();
				Random rand = new Random();
				
				if (!metaTable.containsKey(fileName)) { // if the meta data table has this file information
					int cnt = 1;
					int total = nodeList.size();
					HashSet<String> ips = new HashSet<String>();
					
					while (cnt < REPLICA_FACTOR) { // if not reaching the number of replica factor
						int index = 0;
						NodeRef des = null;
						
						while (des == null || des.getIp().getHostAddress().equals(sourceNode.getIp().getHostAddress()) ||
								(!ips.isEmpty() && ips.contains(des.getIp().toString()))) {
							index = rand.nextInt(total);
							des = nodeList.get(index);						
						}
						
						ret.add(des);
						ips.add(des.getIp().toString());
						cnt++;
					}
					
					Hashtable<String, ArrayList<BlockRef>> newNodeTable = new Hashtable<String, ArrayList<BlockRef>>();
					ArrayList<BlockRef> newBlockList = new ArrayList<BlockRef>();
					
					newBlockList.add(sourceBlock);
					newNodeTable.put(sourceNode.getIp().getHostAddress(), newBlockList);
					metaTable.put(fileName, newNodeTable);
				} else {
					HashMap<NodeRef, Integer> freq = new HashMap<NodeRef, Integer>();
					Hashtable<String, ArrayList<BlockRef>> map = metaTable.get(fileName);
					
					for (String nodeIP : map.keySet()) {
						NodeRef node = dataNodeTable.getDataNode(nodeIP);
						freq.put(node, map.get(nodeIP).size());
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
					HashSet<String> ips = new HashSet<String>();
					
					while (cnt < REPLICA_FACTOR) {
						int index = 0;
						NodeRef des = null;
						
						while (des == null || des.getIp().getHostAddress().equals(sourceNode.getIp().getHostAddress()) ||
								(!ips.isEmpty() && ips.contains(des.getIp().toString()))) {
							index = rand.nextInt(total);
							des = sort.get(index).getKey();							
						}
						
						ips.add(des.getIp().toString());
						ret.add(des);
						cnt++;
					}
					
					Hashtable<String, ArrayList<BlockRef>> nodeTable = metaTable.get(fileName);
					ArrayList<BlockRef> blockList = nodeTable.get(sourceNode.getIp().getHostAddress());
					blockList.add(sourceBlock);
					nodeTable.put(sourceNode.getIp().getHostAddress(), blockList);
					metaTable.put(fileName, nodeTable);
				}
				
				out.writeObject(ret);
				out.flush();
				in.close();
				out.close();				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		public Updater(ObjectInputStream in, ObjectOutputStream out) {
			this.out = out;
			this.in = in;;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void run() {		
			try {	
				NodeRef sourceNode = (NodeRef)in.readObject(); //update information to name node
				Hashtable<String, ArrayList<BlockRef>> nodeTable = 
						(Hashtable<String, ArrayList<BlockRef>>)in.readObject();
				
				for (String filename : nodeTable.keySet()) {
					Hashtable<String, ArrayList<BlockRef>> fileTable = metaTable.get(filename);
					fileTable.put(sourceNode.getIp().getHostAddress(), nodeTable.get(filename)); // update file table
				}
				
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class MapReduceJob implements Runnable {
		private ObjectOutputStream out = null;
		private ObjectInputStream in = null;
		
		public MapReduceJob(ObjectInputStream in, ObjectOutputStream out) {
			this.out = out;
			this.in = in;
		}
		
		@Override
		public void run() {
			try {
				String inputFile = (String)in.readObject();
				String outputPath = (String)in.readObject();
				Class<?> mapReduceClass = (Class<?>)in.readObject(); // get required map reduce job information
				
				Hashtable<String, ArrayList<BlockRef>> ipTable = metaTable.get(inputFile); // get data node ip address
				Hashtable<NodeRef, ArrayList<BlockRef>> refTable = new Hashtable<NodeRef, ArrayList<BlockRef>>();
				
				for (String ip : ipTable.keySet()) {
					refTable.put(dataNodeTable.getDataNode(ip), ipTable.get(ip));
				}
								
				int totalBlockNum = 0;
				for (ArrayList<BlockRef> blockList : refTable.values()) {
					totalBlockNum += blockList.size();
				}
				
				int splitNum = totalBlockNum / REPLICA_FACTOR;
				JobTracker jobTracker = JobTracker.getInstance();
				
				// create a job by starting a job tracker
				jobTracker.createJob(inputFile, splitNum, refTable, outputPath, mapReduceClass);
				
				in.close();
				out.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
}

