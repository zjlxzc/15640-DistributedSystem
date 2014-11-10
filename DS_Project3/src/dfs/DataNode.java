package dfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import mapReduce.TaskTracker;

public class DataNode {
	//<filename, blockList>
	Hashtable<String, ArrayList<BlockRef>> fileTable;
	private int BLOCK_SIZE;
	private int PORT;
	private int blockID;
	private String masterIP;
	private int masterPort;
	public DataNode(int port, String masterIP, int masterPort) {
		new Thread(new Main()).start();
		new Thread(new Listen()).start();
		fileTable = new Hashtable<String, ArrayList<BlockRef>>();
		PORT = port;
		blockID = 0;
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}
	
	private static void Usage() {
		System.out.println("Please enter command:\n");
		System.out.println("[U]pload file");
		System.out.println("List all the [F]iles");
		System.out.println("[S]ubmit job");
		System.out.println("[Q]uit");
		System.out.println("Please input:[F/S/U/Q]:");
	}
	
	private class Main implements Runnable {				
		@Override
		public void run() {			
			Scanner scan = new Scanner(System.in);
			while (true) {
				Usage();
				String str = scan.nextLine();
				if (str.equals("U")) {
					System.out.println("Please enter the file name:");
					String fileName = scan.nextLine();
					new Thread(new Upload(fileName)).start();
				} else if (str.equals("Q")) {
					System.exit(0);
				} else if (str.equals("S")) {
					System.out.println("Please enter the input file name: ");
					String inputFile = scan.nextLine();
					System.out.println("Please enter the output path: ");
					String outputPath = scan.nextLine();
					System.out.println("Please enter the MapReduce file name: ");
					String mapReduceFile = scan.nextLine();
					new Thread(new MapReduceJob(inputFile, outputPath, mapReduceFile)).start();
				} else if (str.equals("F")){
					new Thread(new ListFileThread()).start();
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
				System.out.println("LcoalAddress : " + InetAddress.getLocalHost().toString() + " : " + PORT);
				Socket remote;
				while (true) {	
					remote = listenSoc.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
					PrintWriter out = new PrintWriter(remote.getOutputStream(), true);
					String first = in.readLine();
					if (first.equals("BlockSize")) {
						BLOCK_SIZE = Integer.parseInt(in.readLine());
						System.out.println("block size: " + BLOCK_SIZE);
					} else if (first.equals("BlockTransfer")) {
						System.out.println("Get the transfer request");
						new Thread(new BlockReceiver(in, out)).start();
					} else if (first.equals("StartTaskTracker")) {
						TaskTracker taskTracker = new TaskTracker();
						out.println("" + taskTracker.getPort());
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
	
	private class ListFileThread implements Runnable {		
		@Override
		public void run() {
			if (fileTable == null || fileTable.isEmpty()) {
				System.out.println("There is no file in the system!");
			} else {
				for (String file : fileTable.keySet()) {
					System.out.println("File Name : " + file);
					for (BlockRef block : fileTable.get(file)) {
						System.out.print(block.getFileName() + " ");
					}
					System.out.println();
					System.out.println("===================================================");
				}
			}			
		}		
	}
	
	private class Upload implements Runnable {
		private String fileName;
		public Upload(String fileName) {
			this.fileName = fileName;
		}
		@Override
		public void run() {
			try {											
				File inputFile = new File(fileName);
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				String line;
				NodeRef me = new NodeRef(InetAddress.getLocalHost().getHostName(), PORT);
				int splitNum = 1;
				ArrayList<BlockRef> blockList = new ArrayList<BlockRef>();
				Block curBlock = new Block(blockID, BLOCK_SIZE);;
				while ((line = br.readLine()) != null) {						
					curBlock.addRecord(line);
					if (curBlock.isFull()) {
						BlockRef curRef = curBlock.generateRef(me, fileName, splitNum);
						blockList.add(curRef);
						new Thread(new BlockAdder(fileName, curRef)).start();
						blockID++;
						splitNum++;
						curBlock = new Block(blockID, BLOCK_SIZE);
						Thread.sleep(2000);
					}
				}
				if (curBlock.getSize() != 0) {
					BlockRef curRef = curBlock.generateRef(me, fileName, splitNum);
					blockList.add(curRef);
					new Thread(new BlockAdder(fileName, curRef)).start();
					blockID++;
					splitNum++;
				}				
				fileTable.put(fileName, blockList);
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class BlockAdder implements Runnable {
		private String fileName;
		private BlockRef curRef;
		
		public BlockAdder(String fileName, BlockRef curRef) {
			this.fileName = fileName;
			this.curRef = curRef;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			Socket master = null;			
			try {
				master = new Socket(masterIP, masterPort);
				ObjectOutputStream out = new ObjectOutputStream(master.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(master.getInputStream());
				out.writeObject("addBlock");
				out.writeObject(fileName);
				out.writeObject(curRef);
				out.flush();
				ArrayList<NodeRef> addList = (ArrayList<NodeRef>)in.readObject();
				new Thread(new BlockTransfer(addList, curRef)).start();			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
				File outFile = new File(sourceBlock.getFileName());
				for (NodeRef node : addList) {
					System.out.println("start transfer block to: " + node.getIp() + " : " + node.getPort());
					Socket soc = new Socket(node.getIp(), node.getPort());		
					PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
					//BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					BufferedReader br = new BufferedReader(new FileReader(outFile));
					String line;
					out.println("BlockTransfer");
					out.println(sourceBlock.getParentFile());
					out.println(sourceBlock.getSplitNum());
					while ((line = br.readLine()) != null) {
						out.println(line);
					}	
					br.close();
					soc.close();
					Thread.sleep(1000);
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}			
	}
	
	private class BlockReceiver implements Runnable {
		private BufferedReader in;
		private PrintWriter out;
		public BlockReceiver(BufferedReader in, PrintWriter out) {
			this.in = in;
			this.out = out;
		}
		@Override
		public void run() {
			try {
				String parentFile = in.readLine();
				int splitNum = Integer.parseInt(in.readLine());
				Block receiveBlock = new Block(blockID, BLOCK_SIZE);
				NodeRef me = new NodeRef(InetAddress.getLocalHost().getHostName(), PORT);
				String line;
				System.out.println("Start to receive block");
				while ((line = in.readLine()) != null) {
					receiveBlock.addRecord(line);
				}
				BlockRef receiveBlockRef = receiveBlock.generateRef(me, parentFile, splitNum);
				ArrayList<BlockRef> blockList;
				if (fileTable.containsKey(parentFile)) {
					blockList = fileTable.get(parentFile);
				} else {
					blockList = new ArrayList<BlockRef>();
				}
				blockList.add(receiveBlockRef);
				fileTable.put(parentFile, blockList);
				System.out.println("Received " + receiveBlockRef.getFileName());
				Socket report = new Socket(masterIP, masterPort);
				ObjectOutputStream out = new ObjectOutputStream(report.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(report.getInputStream());
				out.writeObject("update");
				out.writeObject(me);
				out.writeObject(fileTable);
				out.flush();
				report.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	private class MapReduceJob implements Runnable {
		private String inputFile;
		private String outputPath;
		private String mapReduceFile;
		
		public MapReduceJob(String inputFile, String outputPath, String mapReduceFile) {
			this.inputFile = inputFile;
			this.outputPath = outputPath;
			this.mapReduceFile = mapReduceFile;
		}

		@Override
		public void run() {
			Socket master = null;					
			try {
				Class<?> mapReduceClass = Class.forName(mapReduceFile);			
				master = new Socket(masterIP, masterPort);
				ObjectOutputStream out = new ObjectOutputStream(master.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(master.getInputStream());
				out.writeObject("MapReduceNewJob");
				out.writeObject(inputFile);
				out.writeObject(outputPath);
				out.writeObject(mapReduceClass);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		
		}
	}
}
