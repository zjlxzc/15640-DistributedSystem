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
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import mapReduce.MRContext;
import mapReduce.MapReduce;
import mapReduce.Task;
import mergeSort.SingleRecord;

public class FileWriterDFS implements Runnable{
	private String fileName; // the name of output file
	private boolean flag = true; // this variable indicate if the mapper job is done
	private Task task; // reducer task
	private int newPort; // port of a new socket
	private Class<?> MRClass; // get the class of map-reduce job
	
	public FileWriterDFS(String fileName, Task task) throws IOException {
		this.fileName = fileName;
		this.task = task;
		this.MRClass = task.getMapReducer();
	}
	
	public void reducer(MRContext context) throws IOException {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		PriorityQueue<SingleRecord> queue = context.getQueue(); // get all key-value pair
		
		while (queue.size() > 0) { // go through each record
			SingleRecord sr = queue.poll();
			//System.out.println("In Reducer while :" + sr.getKey() + sr.getValue());
			if (!map.containsKey(sr.getKey())) { // put all values of the same key to a hash map
				map.put(sr.getKey(), new ArrayList<String>());
			}
			map.get(sr.getKey()).add(sr.getValue());
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		MRContext contextReducer = new MRContext(); // store result to an object
		
		Constructor<?> constructor = null;
		MapReduce mr = null;

		try {
			constructor = MRClass.getConstructor(); // get constructor
			mr = (MapReduce)constructor.newInstance(); // create a new corresponding instance
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (String key : map.keySet()) {
			mr.reduce(key, map.get(key).iterator(), contextReducer); // call user-defined reducer method
		}
		
		PriorityQueue<SingleRecord> que = contextReducer.getQueue();
		SingleRecord record = new SingleRecord();
		
		while (que.size() > 0) { // to through each record
			record = que.poll();
			//System.out.println("sisisis" + record.getKey() + "\t" + record.getValue());
			writer.write(record.getKey() + "\t" + record.getValue() + "\n");
		}
		
		writer.close();
		task.setStatus("finished"); // set current status
		System.out.println("This task finished successfully!");
	}

	@Override
	public void run() {
		ServerSocket reducer;
		try {
			reducer = new ServerSocket(0);	// create a new socket
			newPort = reducer.getLocalPort();
			MRContext context = new MRContext(); // store result to a context object
			
			while (flag) { // true means mapper is still running
				Socket clientSocket = reducer.accept(); // get client socket				
				new Thread(new ReceiveMapper(clientSocket, context)).start();
			}

			reducer(context); // after map phase, it can start to do reduce process
			reducer.close();
		} catch (IOException e) {
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
	
	private class ReceiveMapper implements Runnable {
		Socket client;
		MRContext context;
		public ReceiveMapper(Socket client, MRContext context) {
			this.client = client;
			this.context = context;
		}
		
		@Override
		public void run() {
			try {
				ObjectInputStream inStream = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream outStream = new ObjectOutputStream(client.getOutputStream());
			
				while (flag) {
					Object obj = inStream.readObject();
					
					SingleRecord record = new SingleRecord();
					if (obj instanceof SingleRecord) {
						record = (SingleRecord)obj;
						context.context(record.getKey(), record.getValue());
					} else {
						break;
					}
				}				
				inStream.close();
				outStream.close();
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
