package mapReduce;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import dfs.BlockRef;
import dfs.NodeRef;
import fileio.FileReaderDFS;
import fileio.FileWriterDFS;

public class TaskTracker {
	
	private Class<?> MRClass;
	private static Hashtable<Integer, Hashtable<Integer, String>> status;
	private static String reducerStatus;
	private static boolean isFinished = false;
	private static int recordCount = 10;
	
	//private Thread reportThread;
	private int reportPort = 0;
	
	public TaskTracker() {
		reportPort = (int)Math.random() + 15640;
		try {
			ServerSocket taskListenSocket = new ServerSocket(reportPort);
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
		Hashtable<Integer, String> stat = new Hashtable<Integer, String>();
		int num = task.getBlockList().size();
		
		for (int i = 0; i < num; i++) {
			FileReaderDFS map = new FileReaderDFS(blocks.get(i).getFileName(), reducers);
			Thread mapper = new Thread(map);
			mapper.start();
			stat.put(blocks.get(i).getId(), "Starting");
			
			while (true) {
				if (map.getCount() != recordCount) {
					isFinished = false;
				} else {
					isFinished = true;
					stat.put(blocks.get(i).getId(), "Finished");
					break;
				}
			}
		}
		status.put(taskID, stat);
	}
	
	public static void trackReducer(ReducerTask task) throws IOException {
		String outputPath = task.getOutputPath();
		
		FileWriterDFS reduce = new FileWriterDFS(outputPath);
		Thread reducer = new Thread(reduce);
		reducer.start();
		reducerStatus = task.getStatus();
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
					ObjectInputStream object = new ObjectInputStream(nameNode.getInputStream());			
					
					String inLine = (String)object.readObject();
					if (inLine.equals("MapperTask")) {
						MapperTask task = (MapperTask)object.readObject();
						trackMapper(task);
					} else {
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
	
	public String getReducerStatus() {
		return reducerStatus;
	}
}
