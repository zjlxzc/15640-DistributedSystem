package fileio;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to do reducer job.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		Iterator<SingleRecord> iterator = context.getIterator(); // get all key-value pair
		
		while (iterator.hasNext()) {
			SingleRecord sr = (SingleRecord)iterator.next();
			System.out.println("In Reducer while :" + sr.getKey() + sr.getValue());
			if (!map.containsKey(sr.getKey())) { // put all values of the same key to a hash map
				map.put(sr.getKey(), new ArrayList<String>());
			}
			map.get(sr.getKey()).add(sr.getValue());
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		MRContext contextReducer = new MRContext(); // store result to an object
		
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
		
		for (String key : map.keySet()) {
			mr.reduce(key, map.get(key).iterator(), contextReducer); // call user-defined reducer method
		}
		
		Iterator<SingleRecord> it = contextReducer.getIterator();
		SingleRecord record = new SingleRecord();
		while (it.hasNext()) {
			record = it.next();
			System.out.println("sisisis" + record.getKey() + "\t" + record.getValue());
			writer.write(record.getKey() + "\t" + record.getValue() + "\n");
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
				
				Object obj = inStream.readObject();
				System.out.println("SingleRecord is NULL: " + obj == null + " **" + obj.toString().length());
				
				SingleRecord record = new SingleRecord();
				if (obj instanceof SingleRecord) {
					record = (SingleRecord)obj;
					context.context(record.getKey(), record.getValue());
				}
			}
			System.out.println("flag is set to false");
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
