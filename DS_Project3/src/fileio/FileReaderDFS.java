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
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dfs.NodeRef;
import mapReduce.MRContext;
import mergeSort.SingleRecord;
import example.WordCount;

public class FileReaderDFS extends FileReader {
	private static String fileName;
	private HashMap<Integer, NodeRef> reducer; // store all reducer of this job
	private HashMap<Integer, Socket> sockets; // store all connection of this job
	
	public FileReaderDFS(String fileName, HashMap<Integer, NodeRef> map) throws FileNotFoundException {
		super(fileName);
		reducer = map;
	}
	
	public int read() throws IOException {
		int count = 0;
		WordCount wc = new WordCount();
		
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		MRContext context = new MRContext(); // store result to an object
		
		while (line != null) { // read file line by line
			wc.map("", line, context); // call user map method
			line = reader.readLine();
		}
		
		connectReducer();
		Iterator<SingleRecord> iterator = context.getIterator();
		SingleRecord pair = null;
		
		while (iterator.hasNext()) { // iterator each key-value pair and send to corresponding reducer
			pair = iterator.next();
			int hashValue = pair.hashCode() % reducer.size();
			
			Socket clientSocket = sockets.get(hashValue); // get client socket
			ObjectOutputStream sendPair = new ObjectOutputStream(clientSocket.getOutputStream());
			sendPair.writeObject(pair); // // write out object
			sendPair.flush();
		}
		
		reader.close();
		return count;
	}
	
	public void connectReducer() throws IOException {
		for (Map.Entry<Integer, NodeRef> e : reducer.entrySet()) { // connect to each reducer
			Socket clientSocket = new Socket(e.getValue().getIp(), e.getValue().getPort());
			sockets.put(e.getKey(), clientSocket);
		}
	}
	
}
