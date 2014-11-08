package HDFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataNode {
	HashMap<String, ArrayList<BlockRef>> fileMap;
	private int BLOCK_SIZE;
	private int PORT;
	private int blockID;
	private String master;
	public DataNode(int port, String master) {
		new Thread(new Main()).start();
		new Thread(new Listen()).start();
		fileMap = new HashMap<String, ArrayList<BlockRef>>();
		PORT = port;
		blockID = 0;
		this.master = master;
	}
	
	private static void Usage() {
		System.out.println("Please enter command:\n");
		System.out.println("[U]pload file");
		System.out.println("[S]ubmit job");
		System.out.println("[Q]uit");
		System.out.println("Please input:[S/U/Q]:");
	}
	
	private class Main implements Runnable {				
		@Override
		public void run() {			
			Scanner scan = new Scanner(System.in);
			while (true) {
				Usage();
				String str = scan.nextLine();
				if (str.equals("U")) {
					new Thread(new Upload()).start();
				} else if (str.equals("Q")) {
					System.exit(0);
				}
			}
		}
	}
	
	private class Listen implements Runnable {

		@Override
		public void run() {
			ServerSocket listenSoc = null;
			try {
				listenSoc = new ServerSocket(PORT);
				Socket remote;
				while (true) {									
					remote = listenSoc.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
					String first = in.readLine();
					if (first.equals("BlockSize")) {
						BLOCK_SIZE = Integer.parseInt(in.readLine());
						System.out.println("block size: " + BLOCK_SIZE);
					} else if (first.equals("BlockTransfer")) {
						new Thread(new BlockReceiver(in)).start();
					}
				} 				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();	
			} finally {
				try {
					listenSoc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	
	private class Upload implements Runnable {

		@Override
		public void run() {
			try {
//				if (System.getSecurityManager() == null) {
//		            System.setSecurityManager(new SecurityManager());
//		        }
	            String name = "NameNodeRMI";
	            Registry registry = LocateRegistry.getRegistry(master);
	            NameNodeRMI nameNode = (NameNodeRMI) registry.lookup(name);				
				
				Scanner scan = new Scanner(System.in);
				System.out.println("Please input the file name:");
				String fileName = scan.nextLine();
				File inputFile = new File(fileName);
				scan.close();
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				String line;
				NodeRef me = new NodeRef(InetAddress.getLocalHost().getHostName(), PORT);
				int splitNum = 1;
				ArrayList<BlockRef> blockList = new ArrayList<BlockRef>();
				while ((line = br.readLine()) != null) {
					Block curBlock = new Block(blockID, BLOCK_SIZE);
					while(!curBlock.isFull()) {
						curBlock.addRecord(line);
					}
					BlockRef curRef = curBlock.generateRef(me, fileName, splitNum);
					blockList.add(curRef);
					ArrayList<NodeRef> addList = nameNode.addBlock(fileName, me);
					new Thread(new BlockTransfer(addList, curRef)).start();
					blockID++;
					splitNum++;					
				}
				fileMap.put(fileName, blockList);
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class BlockTransfer implements Runnable {
		private ArrayList<NodeRef> addList;
		private BlockRef sourceBlock;
		
		public BlockTransfer(ArrayList<NodeRef> addList, BlockRef sourceBlock) {
			this.addList = addList;
			this.sourceBlock = sourceBlock;
		}
		
		@Override
		public void run() {			
			try {
				Socket soc = null;
				BufferedReader br = null;				
				File outFile = new File(sourceBlock.getFileName());
				for (NodeRef node : addList) {
					soc = new Socket(node.getIp(), node.getPort());		
					PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
					br = new BufferedReader(new FileReader(outFile));
					String line;
					out.println("BlockTransfer");
					out.println(sourceBlock.getParentFile());
					out.println(sourceBlock.getSplitNum());
					while ((line = br.readLine()) != null) {
						out.println(line);
					}
					br.close();
					soc.close();					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}			
	}
	
	private class BlockReceiver implements Runnable {
		private BufferedReader in;
		public BlockReceiver(BufferedReader in) {
			this.in = in;
		}
		@Override
		public void run() {
			try {
				String parentFile = in.readLine();
				int splitNum = Integer.parseInt(in.readLine());
				Block receiveBlock = new Block(blockID, BLOCK_SIZE);
				NodeRef me = new NodeRef(InetAddress.getLocalHost().getHostName(), PORT);
				String line;
				while ((line = in.readLine()) != null) {
					receiveBlock.addRecord(line);
				}
				BlockRef receiveBlockRef = receiveBlock.generateRef(me, parentFile, splitNum);
				ArrayList<BlockRef> blockList;
				if (fileMap.containsKey(parentFile)) {
					blockList = fileMap.get(parentFile);
				} else {
					blockList = new ArrayList<BlockRef>();
				}
				blockList.add(receiveBlockRef);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}
