package mapReduce;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import dfs.BlockRef;
import dfs.NodeRef;
import fileio.FileReaderDFS;
import fileio.FileWriterDFS;

public class TaskTracker {

	private static TaskTracker instance;
	
	private Class<?> MRClass;
	private static Hashtable<Integer, String> status;
	
	private static boolean isFinished = false;
	private static int recordCount = 10;
	
	//private Thread reportThread;
	private int reportPort = 15640;
	
	public static TaskTracker getInstance() {
		if (instance == null) {
			instance = new TaskTracker();
		}
		return instance;
	}
	
	public TaskTracker() {
	}
	
	public void add(Task task) throws IOException {
		try {
			ServerSocket taskListenSocket = new ServerSocket(15640);
			Thread listenThread = new Thread(new Listen(taskListenSocket));		
			listenThread.start();
			
		} catch (IOException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void trackMapper(MapperTask task) throws FileNotFoundException {
		int taskID = task.getTaskID();
		ArrayList<BlockRef> blocks = task.getBlockList();
		ArrayList<NodeRef> reducers = task.getReducers();
		int num = task.getBlockList().size();
		
		for (int i = 0; i < num; i++) {
			FileReaderDFS map = new FileReaderDFS(blocks.get(i).getFileName(), reducers);
			Thread mapper = new Thread(map);
			mapper.start();
			status.put(taskID, "Starting");
			
			while (true) {
				if (map.getCount() != recordCount) {
					isFinished = false;
				} else {
					isFinished = true;
					break;
				}
			}
			status.put(taskID, "Finished");
		}
	}
	
	public static void trackReducer(ReducerTask task) throws IOException {
		String outputPath = task.getOutputPath();
		int taskID = task.getTaskID();
		int num = task.ge
		for (int i = 0; i < num; i++) {
			FileWriterDFS reduce = new FileWriterDFS(outputPath);
			Thread reducer = new Thread(reduce);
			reducer.start();
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	private static class Listen implements Runnable {
		ServerSocket taskListenSocket;
		public Listen(ServerSocket taskListenSocket) {
			this.taskListenSocket = taskListenSocket;
		}
		
		public void run() {
			while (true) {
				try {
					Socket nameNode = taskListenSocket.accept();					
					InputStreamReader inStream = new InputStreamReader(nameNode.getInputStream());
					BufferedReader br = new BufferedReader(inStream); 
					PrintWriter outStream = new PrintWriter(nameNode.getOutputStream());				
					
					String inLine = br.readLine();
					if (inLine.equals("MapperTask")) {
						br = new BufferedReader(inStream); 
						ObjectInputStream object = new ObjectInputStream(nameNode.getInputStream());
						MapperTask task = (MapperTask)object.readObject();
						trackMapper(task);
					} else {
						br = new BufferedReader(inStream); 
						ObjectInputStream object = new ObjectInputStream(nameNode.getInputStream());
						ReducerTask task = (ReducerTask)object.readObject();
						trackReducer(task);
					}				
					
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		}		
	}
	
	public int getPort() {
		return reportPort;
	}
	
}
