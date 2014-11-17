package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a data node.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	Hashtable<String, ArrayList<BlockRef>> fileTable; // a mapping of a file and its blocks
	private int BLOCK_SIZE;
	private int PORT; // data node port
	private int blockID;
	private String masterIP; // name node ip address
	private int masterPort; // name node port
	
	public DataNode(int port, String masterIP, int masterPort) {
		new Thread(new Main()).start(); // start main thread
		new Thread(new Listen()).start(); // start listen thread to listen to name node
		
		fileTable = new Hashtable<String, ArrayList<BlockRef>>();
		PORT = port;
		blockID = 0;
		this.masterIP = masterIP;
		this.masterPort = masterPort;
	}
	
	// useful command to use our system
	private static void Usage() {
		System.out.println("Please enter command:");
		System.out.println("To upload a file to DFS : upload [filename]");
		System.out.println("To list all the file information: files");
		System.out.println("To submit a mapreduce job: job [input_file] [output_file] [mapreduce_class]");
		System.out.println("To quit the system: quit");
	}
	
	private class Main implements Runnable {				
		@Override
		public void run() {		
			Usage();
			Scanner scan = new Scanner(System.in);
			
			// take the user input to decide the work flow
			while (true) {							
				String[] str = scan.nextLine().split(" ");
				if (str[0].equals("upload")) {
					String fileName = str[1];
					new Thread(new Upload(fileName)).start();
				} else if (str[0].equals("quit")) {
					System.out.println("ByeBye");
					scan.close();
					System.exit(0);
				} else if (str[0].equals("job")) {
					String inputFile = str[1];
					String outputPath = str[2];
					String mapReduceFile = str[3];
					new Thread(new MapReduceJob(inputFile, outputPath, mapReduceFile)).start();
				} else if (str[0].equals("files")){
					System.out.println("Files on this node:");
					System.out.println("===================================================");
					new Thread(new ListFileThread()).start();
				} else {
					System.out.println("The input command is wrong.");
					Usage();
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
					ObjectInputStream in = new ObjectInputStream(remote.getInputStream());	
					ObjectOutputStream out = new ObjectOutputStream(remote.getOutputStream());
		
					String first = (String)in.readObject();
					if (first.equals("BlockSize")) {
						BLOCK_SIZE = (int)in.readObject();
						System.out.println("block size: " + BLOCK_SIZE);
					} else if (first.equals("BlockTransfer")) {
						System.out.println("Start to receive from " + remote.getRemoteSocketAddress());
						new Thread(new BlockReceiver(in, out)).start();
					} else if (first.equals("StartTaskTracker")) {
						System.out.println(first);
						TaskTracker taskTracker = new TaskTracker();
						out.writeObject("" + taskTracker.getPort());
						out.flush();
						System.out.println(taskTracker.getPort());
					}
				} 				
			}catch (IOException e) {
				e.printStackTrace();	
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					listenSoc.close();
				} catch (IOException e) {
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
				Block curBlock = new Block(blockID, BLOCK_SIZE);
				
				while ((line = br.readLine()) != null) {						
					curBlock.addRecord(line);
					if (curBlock.isFull()) {
						BlockRef curRef = curBlock.generateRef(me, fileName, splitNum);
						blockList.add(curRef);
						new Thread(new BlockAdder(fileName, curRef)).start();
						blockID++;
						splitNum++;
						curBlock = new Block(blockID, BLOCK_SIZE);
						Thread.sleep(1000);
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
				e.printStackTrace();
			} catch (InterruptedException e) {
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
				ObjectOutputStream masterOut = new ObjectOutputStream(master.getOutputStream());
				ObjectInputStream masterIn = new ObjectInputStream(master.getInputStream());
				masterOut.writeObject("addBlock");
				masterOut.writeObject(fileName);
				masterOut.writeObject(curRef);
				masterOut.flush();
				ArrayList<NodeRef> addList = (ArrayList<NodeRef>)masterIn.readObject();
				masterIn.close();
				masterOut.close();
				master.close();
				
				new Thread(new BlockTransfer(addList, curRef)).start();	
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
					boolean transfered = false;
					
					while (!transfered) {
						Socket soc = new Socket(node.getIp(), node.getPort());		
						ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(soc.getInputStream());						
						BufferedReader br = new BufferedReader(new FileReader(outFile));
						String line = "";
						out.writeObject("BlockTransfer");
						out.writeObject(sourceBlock.getParentFile());
						out.writeObject(sourceBlock.getSplitNum());
						
						while ((line = br.readLine()) != null) {
							out.writeObject(line);
						}	
						
						out.writeObject("end of block");
						br.close();
						String response = (String)in.readObject();
						System.out.println("Block Transfer: " + response);
						
						if (response.equals("Received")) {
							transfered = true;
						}
						
						out.flush();
						in.close();
						out.close();
						soc.close();
					}					
				}				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 	
		}		
	}	
	
	private class BlockReceiver implements Runnable {
		private ObjectInputStream in;		
		private ObjectOutputStream out;
		
		public BlockReceiver(ObjectInputStream in, ObjectOutputStream out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public void run() {
			try {
				String parentFile = (String)in.readObject();
				int splitNum = (int)in.readObject();
				Block receiveBlock = new Block(blockID, BLOCK_SIZE);
				NodeRef me = new NodeRef(InetAddress.getLocalHost().getHostName(), PORT);
				String line = "";
				
				while ((line = (String)in.readObject()) != null) {
					if (line.equals("end of block")) {
						break;
					}
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
				
				// Report to the master
				Socket report = new Socket(masterIP, masterPort);
				ObjectOutputStream masterOut = new ObjectOutputStream(report.getOutputStream());
				ObjectInputStream masterIn = new ObjectInputStream(report.getInputStream());
				masterOut.writeObject("update");
				masterOut.writeObject(me);
				masterOut.writeObject(fileTable);
				masterOut.flush();
				masterIn.close();
				masterOut.close();
				report.close();	
				out.writeObject("Received");
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
				out.flush();
				in.close();
				out.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 		
		}
	}
}
