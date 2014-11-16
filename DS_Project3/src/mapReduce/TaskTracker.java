package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to do reducer job.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import dfs.BlockRef;
import dfs.NodeRef;
import fileio.FileReaderDFS;
import fileio.FileWriterDFS;

public class TaskTracker {
	
	private static Class<?> MRClass;
	private static Hashtable<Integer, Hashtable<Integer, String>> status;
	private static String reducerStatus;
	private static boolean isFinished = false;
	private static int recordCount = 10;

	private static int reportPort = 0;
	private static FileWriterDFS reduce;
	
	public TaskTracker() {
		status = new Hashtable<Integer, Hashtable<Integer, String>>();
		try {
			Thread listenThread = new Thread(new Listen());	// start a new thread to listen
			listenThread.start();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void trackMapper(MapperTask task) throws FileNotFoundException {
		int taskID = task.getTaskID(); // get taskID
		ArrayList<BlockRef> blocks = task.getBlockList(); // get all file blocks
		ArrayList<NodeRef> reducers = task.getReducers(); // get required reducers
		Hashtable<Integer, String> stat = new Hashtable<Integer, String>(); // to store status
		int num = task.getBlockList().size();
		MRClass = task.getMapReducer(); // get user map-reduce class
		
		for (int i = 0; i < num; i++) {
			FileReaderDFS map = new FileReaderDFS(blocks.get(i).getFileName(), reducers, MRClass);
			Thread mapper = new Thread(map); // create a new thread to do map job
			mapper.start();
			stat.put(blocks.get(i).getId(), "Starting"); // record current status
			
			while (true) {
				if (map.getCount() != recordCount) { // to check if map is done
					isFinished = false;
				} else {
					isFinished = true;
					stat.put(blocks.get(i).getId(), "finished"); // update map status
					break;
				}
			}
		}
		status.put(taskID, stat);
	}
	
	public static void trackReducer(ReducerTask task) throws IOException {
		String outputPath = task.getOutputPath();
		reduce = new FileWriterDFS(outputPath, task);
		Thread reducer = new Thread(reduce);
		reducer.start();
		
		reducerStatus = task.getStatus();
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	private static class Listen implements Runnable {
		ServerSocket taskListenSocket;
		public Listen() throws IOException {
			taskListenSocket = new ServerSocket(0); // start a new socket to listen to name node
			reportPort = taskListenSocket.getLocalPort(); // get generated port
		}
		
		public void run() {
			while (true) {
				try {
					Socket nameNode = taskListenSocket.accept(); // get name node socket	
					ObjectInputStream object = new ObjectInputStream(nameNode.getInputStream());
					ObjectOutputStream srcOut = new ObjectOutputStream(nameNode.getOutputStream());								
					
					String inLine = (String)object.readObject(); // get name node information
					System.out.println(inLine);
					if (inLine.equals("MapperTask")) {
						MapperTask task = (MapperTask)object.readObject(); // get map task
						trackMapper(task);
					} else if (inLine.equals("ReduceTask")) {
						ReducerTask task = (ReducerTask)object.readObject(); // get reduce task
						trackReducer(task);
					} else if (inLine.equals("ReportMapper")){ // send map information
						System.out.println("TASKTRACK " + status.toString());
						srcOut.writeObject(status);
						srcOut.flush();
					} else if (inLine.equals("ReportReducer")) { // send reduce information
						System.out.println("ReportReducer " + reducerStatus.toString());
						srcOut.writeObject(reducerStatus);
						srcOut.flush();
						System.out.println("ReportReducer after " + reducerStatus.toString());
					} else if (inLine.equals("MapperFinished")) {
						reduce.setFlag(false);
					} else if (inLine.equals("StartSend")) {
						srcOut.writeObject(String.valueOf(reduce.getNewPort()));
						srcOut.flush();
					}
					object.close();
					srcOut.close();
					nameNode.close();
					System.out.println("Socket Close");
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		}		
	}
	
	public int getPort() {
		return reportPort; // return port to name node
	}
	
	public String getReducerStatus() {
		return reducerStatus; // return reducer status
	}
}
