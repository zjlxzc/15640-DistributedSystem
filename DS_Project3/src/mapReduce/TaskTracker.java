package mapReduce;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import dfs.NodeRef;
import fileio.FileReaderDFS;
import fileio.FileWriterDFS;

public class TaskTracker {

	private String mapper = "mapper";
	//private String reducer = "reducer";
	private String fileName = "";
	private HashMap<Integer, NodeRef> reducerNode;
	private boolean isFinished = false;
	private int recordCount = 10;
	
	public void trackMapper() throws FileNotFoundException {
		FileReaderDFS map = new FileReaderDFS(fileName, reducerNode);
		Thread mapper = new Thread(map);
		mapper.start();
		
		while (true) {
			if (map.getCount() != recordCount) {
				isFinished = false;
			} else {
				isFinished = true;
				break;
			}
		}
	}
	
	public void trackReducer() throws IOException {
		FileWriterDFS reduce = new FileWriterDFS(fileName);
		Thread reducer = new Thread(reduce);
		reducer.start();
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public static void main(String[] args) throws IOException {
		TaskTracker tt = new TaskTracker();
		
		if (args[0].equals(tt.mapper)) {
			tt.trackMapper();
		} else {
			tt.trackReducer();
		}
	}
}
