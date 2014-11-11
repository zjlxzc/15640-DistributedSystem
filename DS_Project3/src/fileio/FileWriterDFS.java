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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	private String fileName;
	private boolean flag = true; // this variable indicate if the mapper job is done
	private Task task;
	private int newPort;
	
	public FileWriterDFS(String fileName, Task task) throws IOException {
		this.fileName = fileName;
		this.task = task;
	}
	
	public void reducer(MRContext context) throws IOException {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Iterator<SingleRecord> iterator = context.getIterator();
		
		while (iterator.hasNext()) {
			SingleRecord sr = (SingleRecord)iterator.next();
			if (!map.containsKey(sr.getKey())) { // put all values of the same key to a hash map
				map.put(sr.getValue(), new ArrayList<String>());
			}
			map.get(sr.getKey()).add(sr.getValue());
		}
		
		MRContext contextReducer = new MRContext(); // store result to an object
		WordCount wc = new WordCount();
		for (String key : map.keySet()) {
			wc.reduce(key, map.get(key).iterator(), contextReducer);
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		writer.write(context.getIterator().next().getKey() + "\t" + context.getIterator().next().getValue());
		writer.close();
		task.setStatus("finished");
	}

	@Override
	public void run() {
		ServerSocket reducer;
		try {
			reducer = new ServerSocket(0);	
			newPort = reducer.getLocalPort();
			MRContext context = new MRContext(); // store result to an object
			System.out.println("flag : " + flag);	
			while (flag) {
				Socket clientSocket = reducer.accept(); // get client socket
				InputStreamReader inStream = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader br = new BufferedReader(inStream); // reader client input stream			
				String[] inLine = br.readLine().split("\t");
				context.context(inLine[0], inLine[1]);
			}
			System.out.println("after flage : ");	
			reducer(context);		
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
}
