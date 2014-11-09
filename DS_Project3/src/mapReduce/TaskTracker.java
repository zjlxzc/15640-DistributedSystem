package mapReduce;

import java.io.FileNotFoundException;
import java.io.IOException;
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

	private String hostname;
	private Class<?> MRClass;
	private String outputPath;
	private ArrayList<BlockRef> blocks;
	private ArrayList<NodeRef> reducers;
	
	private String mapRed = "";	
	private boolean isFinished = false;
	private int recordCount = 10;
	
	//private Thread reportThread;
	private int reportPort = 15640;
	
	public TaskTracker(Task task) throws IOException {
		//hostname = ;
		//MRClass = ;
		//outputPath = ;
		//blocks = ;
		//reducers = ;

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
	
	public void trackMapper(int num) throws FileNotFoundException {
		for (int i = 0; i < num; i++) {
			FileReaderDFS map = new FileReaderDFS(blocks.get(i).getFileName(), reducers);
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
	}
	
	public void trackReducer(int num) throws IOException {
		for (int i = 0; i < num; i++) {
			FileWriterDFS reduce = new FileWriterDFS(outputPath);
			Thread reducer = new Thread(reduce);
			reducer.start();
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	private class Listen implements Runnable {
		ServerSocket taskListenSocket;
		public Listen(ServerSocket taskListenSocket) {
			this.taskListenSocket = taskListenSocket;
		}
		
		public void run() {
			while (true) {
				Socket master;
				try {
					master = taskListenSocket.accept();	
					
					
					if (mapRed.equals("mapper")) {
						trackMapper(blocks.size());
					} else {
						trackReducer(reducers.size());
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
