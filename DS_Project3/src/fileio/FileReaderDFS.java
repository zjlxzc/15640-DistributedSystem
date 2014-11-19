package fileio;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to do map job.
 * It will read a file and do some pre-process and send to reducers.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;

import mapReduce.MRContext;
import mapReduce.MapReduce;
import mergeSort.SingleRecord;
import dfs.NodeRef;

public class FileReaderDFS implements Runnable {
	private String fileName;
	private ArrayList<NodeRef> reducers; // store all reducer of this job
	private ArrayList<NodeRef> sockets; // store all connection of this job
	private Class<?> MRClass; // get the class of map-reduce job, either mapper or reducer
	private boolean isEnd = false; // to check it reach the end of mapper
	
	public FileReaderDFS() {
	}
	
	public FileReaderDFS(String fileName, ArrayList<NodeRef> reducers, Class<?> MRClass)
			throws FileNotFoundException {
		this.fileName = fileName;
		this.reducers = reducers;
		this.MRClass = MRClass;
		sockets = new ArrayList<NodeRef>(); 
	}

	public void connectReducer() throws IOException, ClassNotFoundException {
		
		Socket clientSocket = null;
		for (NodeRef node : reducers) { // connect to each reducer
			clientSocket = new Socket(node.getIp(), node.getPort());
			ObjectOutputStream sendInfor = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream sendIn = new ObjectInputStream(clientSocket.getInputStream());
			
			sendInfor.writeObject("StartSend"); // write out information as a signal
			sendInfor.flush();
			
			int port = (Integer)sendIn.readObject();
			sockets.add(new NodeRef(node.getIp().getHostAddress(), port));
			sendIn.close();
			sendInfor.close();
			clientSocket.close();
		}
	}

	@Override
	public void run() {
		Constructor<?> constructor = null;
		MapReduce mr = null;
		BufferedReader reader = null;
		try {
			constructor = MRClass.getConstructor(); // get constructor
			mr = (MapReduce)constructor.newInstance(); // create a new corresponding instance
			reader = new BufferedReader(new FileReader(fileName)); // read file
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String line = "";
		MRContext context = null;
		
		try {
			line = reader.readLine();
			context = new MRContext(); // store result to an object

			while (line != null) { // read file line by line
				mr.map("", line, context); // call user map method
				line = reader.readLine();
			}
			
			try {
				connectReducer();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			PriorityQueue<SingleRecord> queue = context.getQueue();
			SingleRecord pair = null;
			ArrayList<Socket> socketList = new ArrayList<Socket>();
			ArrayList<ObjectOutputStream> outputList = new ArrayList<ObjectOutputStream>();

			for (NodeRef reducer : sockets) {
				Socket clientSocket = new Socket(reducer.getIp(), reducer.getPort());	
				socketList.add(clientSocket);
				outputList.add(new ObjectOutputStream(clientSocket.getOutputStream()));
			}
			
			while (queue.size() > 0) { // iterator each key-value pair and
											// send to corresponding reducer
				pair = queue.remove();
				int hashValue = pair.hashCode() % reducers.size(); // get correct reducer number
				ObjectOutputStream sendPair = outputList.get(hashValue);
				sendPair.writeObject(pair); // write out key-value object
				sendPair.flush();
			}	
			
			isEnd = true;
			reader.close();
			
			for (int i = 0; i < sockets.size(); i++) {
				ObjectOutputStream out = outputList.get(i);
				out.writeObject(""); // a signal to indicate the end of sending from mapper
				out.flush();
				
				Thread.sleep(100);
				out.close();
				socketList.get(i).close();
			}
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	// check to see if get the end of mapper
	public boolean getEnd() {
		return isEnd;
	}
}
