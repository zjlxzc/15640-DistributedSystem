package fileio;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to do reducer job.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import example.WordCount;
import mapReduce.MRContext;
import mapReduce.Task;
import mergeSort.SingleRecord;

public class FileWriterDFS implements Runnable{
	private String fileName; // the name of output file
	private boolean flag = true; // this variable indicate if the mapper job is done
	private Task task; // reducer task
	private int newPort; // port of a new socket
	
	public FileWriterDFS(String fileName, Task task) throws IOException {
		this.fileName = fileName;
		this.task = task;
	}
	
	public void reducer(MRContext context) throws IOException {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Iterator<SingleRecord> iterator = context.getIterator(); // get all key-value pair
		
		while (iterator.hasNext()) {
			SingleRecord sr = (SingleRecord)iterator.next();
			if (!map.containsKey(sr.getKey())) { // put all values of the same key to a hash map
				map.put(sr.getKey(), new ArrayList<String>());
			}
			map.get(sr.getKey()).add(sr.getValue());
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		MRContext contextReducer = new MRContext(); // store result to an object
		WordCount wc = new WordCount(); // create an instance of word count example
		
		for (String key : map.keySet()) {
			wc.reduce(key, map.get(key).iterator(), contextReducer); // call user-defined reducer method
			System.out.println("sisisis" + context.getIterator().next().getKey() + "\t" + context.getIterator().next().getValue());
			writer.write(context.getIterator().next().getKey() + "\t" + context.getIterator().next().getValue());
		}
		writer.close();
		task.setStatus("finished"); // set current status
	}

	@Override
	public void run() {
		ServerSocket reducer;
		try {
			reducer = new ServerSocket(0);	// create a new socket
			newPort = reducer.getLocalPort();
			MRContext context = new MRContext(); // store result to a context object
			ObjectOutputStream outStream = null;
			ObjectInputStream inStream = null;
			while (flag) { // true means mapper is still running
				Socket clientSocket = reducer.accept(); // get client socket
				inStream = new ObjectInputStream(clientSocket.getInputStream());
				outStream = new ObjectOutputStream(clientSocket.getOutputStream());
				
				SingleRecord record = (SingleRecord)inStream.readObject();
				//System.out.println(flag + " * IN Reducer" + record.getKey() + "**" + record.getValue());
				context.context(record.getKey(), record.getValue());
			}
			reducer(context); // after map phase, it can start to do reduce process
			inStream.close();
			outStream.close();
			reducer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int getNewPort() {
		return newPort;
	}

	public void setNewPort(int newPort) {
		this.newPort = newPort;
	}
}
