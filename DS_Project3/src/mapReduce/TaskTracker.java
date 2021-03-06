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
import java.net.InetAddress;
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
			
			SeparateMapper separate = new SeparateMapper(stat, map, blocks.get(i).getId());
			Thread singleMapper = new Thread(separate);
			singleMapper.start();
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
			MapperTask mapperTask = new MapperTask();
			ReducerTask reducerTask = new ReducerTask();
			while (true) {
				try {
					Socket nameNode = taskListenSocket.accept(); // get name node socket	
					ObjectOutputStream srcOut = new ObjectOutputStream(nameNode.getOutputStream());
					ObjectInputStream object = new ObjectInputStream(nameNode.getInputStream());			
					
					String inLine = (String)object.readObject(); // get name node information
					if (inLine.equals("MapperTask")) {
						mapperTask = (MapperTask)object.readObject(); // get map task
						srcOut.writeObject("MapperSuccess");
						srcOut.flush();
						trackMapper(mapperTask);
						System.out.println("Mapper Started!");
					} else if (inLine.equals("ReduceTask")) {
						reducerTask = (ReducerTask)object.readObject(); // get reduce task
						srcOut.writeObject("ReduceSuccess");
						srcOut.flush();
						trackReducer(reducerTask);
						System.out.println("Reducer Started!");
					} else if (inLine.equals("ReportMapper")){ // send map information
						srcOut.writeObject(status);
						srcOut.flush();
					} else if (inLine.equals("ReportReducer")) { // send reduce information
						srcOut.writeObject(reducerTask.getStatus());
						srcOut.flush();
					} else if (inLine.equals("MapperFinished")) { // if mapper has finished
						reduce.setFlag(false);
						Socket toRed = new Socket(InetAddress.getLocalHost(), reduce.getNewPort());
						ObjectOutputStream redOut = new ObjectOutputStream(toRed.getOutputStream());
						redOut.writeObject("");
						redOut.flush();
						toRed.close();
						System.out.println("Mapper Finished!");
					} else if (inLine.equals("StartSend")) {
						srcOut.writeObject(reduce.getNewPort());
						srcOut.flush();
					}
					nameNode.close();
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
	
	private static class SeparateMapper implements Runnable {
		Hashtable<Integer, String> stat = new Hashtable<Integer, String>(); // to store status
		FileReaderDFS map = new FileReaderDFS();
		int blockID = 0;
		
		public SeparateMapper(Hashtable<Integer, String> status, FileReaderDFS map, int id) {
			stat = status;
			this.map = map;
			blockID = id;
		}
		@Override
		public void run() {
			stat.put(blockID, "Starting"); // record current status			
			while (!map.getEnd()) {	    // to check if map is done
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			stat.put(blockID, "finished"); // update map status
		}	
	}
}
