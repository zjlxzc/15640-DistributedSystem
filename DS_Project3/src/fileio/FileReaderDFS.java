package fileio;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to do map job.
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
import java.util.Iterator;

import mapReduce.MRContext;
import mapReduce.MapReduce;
import mergeSort.SingleRecord;
import dfs.NodeRef;

public class FileReaderDFS implements Runnable {
	private int count;
	private String fileName;
	private ArrayList<NodeRef> reducers; // store all reducer of this job
	private ArrayList<NodeRef> sockets; // store all connection of this job
	private Class<?> MRClass; // get the class of map-reduce job
	private boolean isEnd = false;
	
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
			
			String ppp = (String)sendIn.readObject();
			int port = Integer.parseInt(ppp);
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
				count++;
			}
			//System.out.println("IN MAPPER" + count);
			try {
				connectReducer();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Iterator<SingleRecord> iterator = context.getIterator();
			SingleRecord pair = null;
			
			while (iterator.hasNext()) { // iterator each key-value pair and
											// send to corresponding reducer
				pair = iterator.next();
				int hashValue = pair.hashCode() % reducers.size(); // get correct reducer number
				NodeRef nr = sockets.get(hashValue);
				Socket clientSocket = new Socket(nr.getIp(), nr.getPort());
				ObjectOutputStream sendPair = new ObjectOutputStream(
						clientSocket.getOutputStream());
				ObjectInputStream inPair = new ObjectInputStream(
						clientSocket.getInputStream());
				sendPair.writeObject(pair); // write out key-value object
				sendPair.flush();
				//System.out.println("IN MAPPER" + pair.getKey() + " : " + pair.getValue());
				inPair.close();
				sendPair.close();
				clientSocket.close();
			}
			
			isEnd = true;
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// return current number of lines that have been processed to indicate if this task is done
	public int getCount() {
		return count;
	}
	
	public boolean getEnd() {
		return isEnd;
	}
}
