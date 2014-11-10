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
	private ArrayList<Socket> sockets; // store all connection of this job
	private Class<?> MRClass; // get the class of map-reduce job
	
	public FileReaderDFS(String fileName, ArrayList<NodeRef> reducers, Class<?> MRClass)
			throws FileNotFoundException {
		this.fileName = fileName;
		this.reducers = reducers;
		this.MRClass = MRClass;
	}

	public void connectReducer() throws IOException {
		for (NodeRef node : reducers) { // connect to each reducer
			Socket clientSocket = new Socket(node.getIp(), node.getPort());
			sockets.add(clientSocket);
		}
	}

	@Override
	public void run() {
		Constructor<?> constructor = null;
		MapReduce mr = null;
		BufferedReader reader = null;
		
		try {
			constructor = MRClass.getConstructor(); // get constructor
			mr = (MapReduce)constructor.newInstance(); // create a new instance
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

			connectReducer();
			Iterator<SingleRecord> iterator = context.getIterator();
			SingleRecord pair = null;

			while (iterator.hasNext()) { // iterator each key-value pair and
											// send to corresponding reducer
				pair = iterator.next();
				int hashValue = pair.hashCode() % reducers.size();
				Socket clientSocket = sockets.get(hashValue); // get client socket
				ObjectOutputStream sendPair = new ObjectOutputStream(
						clientSocket.getOutputStream());
				sendPair.writeObject(pair); // write out object
				sendPair.flush();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// return current number of lines that have been processed
	public int getCount() {
		return count;
	}
}
