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
import mergeSort.SingleRecord;

public class FileWriterDFS implements Runnable{
	private String fileName;
	private boolean flag = true; // this variable indicate if the mapper job is done
	
	public FileWriterDFS(String fileName) throws IOException {
		this.fileName = fileName;
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
	}

	@Override
	public void run() {
		ServerSocket reducer;
		try {
			reducer = new ServerSocket();		
			MRContext context = new MRContext(); // store result to an object
		
			while (flag) {
				Socket clientSocket = reducer.accept(); // get client socket
				InputStreamReader inStream = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader br = new BufferedReader(inStream); // reader client input stream			
				String[] inLine = br.readLine().split("\t");
				context.context(inLine[0], inLine[1]);
			}
		
			reducer(context);		
			reducer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
