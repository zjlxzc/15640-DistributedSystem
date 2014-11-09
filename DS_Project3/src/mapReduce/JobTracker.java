package mapReduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import dfs.BlockRef;
import dfs.NodeRef;

public class JobTracker {
	private static JobTracker jobTracker = null;
	private static int jobID;
	private ArrayList<Job> jobList; 
	
	public JobTracker() {
		jobID = 1;		
	}
	
	public static JobTracker getInstance() {
		if (jobTracker == null) {
			jobTracker = new JobTracker();
		}
		return jobTracker;
	}
	
	public void createJob(String inputFile, int splitNum, Hashtable<NodeRef, ArrayList<BlockRef>> refTable, 
					String outputPath, Class<?> mapReduceClass) {
				
		HashSet<String> splitNameSet = new HashSet<String>();
		HashMap<NodeRef, ArrayList<BlockRef>> assignment = new HashMap<NodeRef, ArrayList<BlockRef>>();
		int taskID = 1;
		for (NodeRef node : refTable.keySet()) {
			ArrayList<BlockRef> blockList = refTable.get(node);
			while (splitNum > 0) {
				int i = 0;
				for (; i < blockList.size(); i++) {
					String curSplitName = blockList.get(i).getFileName();
					if (!splitNameSet.contains(curSplitName)) {
						if (!assignment.containsKey(node)) {
							ArrayList<BlockRef> list = new ArrayList<BlockRef>();
							list.add(blockList.get(i));
							assignment.put(node, list);
						} else {
							assignment.get(node).add(blockList.get(i));
						}
						splitNameSet.add(curSplitName);
						splitNum--;
					}
				}
			}			
		}
		NodeRef reducer = null;
		int min = Integer.MAX_VALUE;
		for (NodeRef node : assignment.keySet()) {
			if (assignment.get(node).size() < min) {
				reducer = node;
			}
		}
		ArrayList<NodeRef> reducers = new ArrayList<NodeRef>();
		reducers.add(reducer);
		
		Job job = new Job(jobID, new ArrayList<MapperTask>(), new ArrayList<ReducerTask>());
		jobList.add(job);
		
		Socket soc = null;
		
		HashMap<String, Integer> ips = new HashMap<String, Integer>();
		try {
			for (NodeRef node : reducers) {						
				soc = new Socket(node.getIp(), node.getPort());
				BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
				out.println("StartTaskTracker");
				int port = Integer.parseInt(in.readLine());
				NodeRef refToTaskTracker = new NodeRef(node.getIp().toString(), port);		
				ReducerTask task = new ReducerTask(refToTaskTracker, taskID, mapReduceClass, outputPath);
				ips.put(node.getIp().toString(), port);
				job.addReducerTasks(task);
				taskID++;
			}
			for (NodeRef node : assignment.keySet()) {
				NodeRef refToTaskTracker = null;
				if (!ips.containsKey(node.getIp().toString())) {
					soc = new Socket(node.getIp(), node.getPort());
					BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
					out.println("StartTaskTracker");
					int port = Integer.parseInt(in.readLine());
					refToTaskTracker = new NodeRef(node.getIp().toString(), port);	
					ips.put(node.getIp().toString(), port);
				} else {
					String curIp = node.getIp().toString();
					refToTaskTracker = new NodeRef(curIp, ips.get(curIp));
				}				
				MapperTask task = new MapperTask(refToTaskTracker, taskID, mapReduceClass, assignment.get(node), reducers);
				job.addMapperTasks(task);
				taskID++;
			}			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		new Thread(new JobRunner(job)).start();		
	}
	
	
	private class JobRunner implements Runnable {
		private Job job;
		public JobRunner(Job job) {
			this.job = job;
		}
		
		@Override
		public void run() {
			Socket soc = null;
			try {
				boolean stop = false;
				for (ReducerTask reducerTask : job.getReducerTasks()) {
					NodeRef cur = reducerTask.getNode();				
					soc = new Socket(cur.getIp(), cur.getPort());
					ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
					out.writeObject("ReduceTask");
					out.writeObject(reducerTask);
					String ret = (String)in.readObject();
					if (!ret.equals("ReduceSuccess")) {
						System.out.println("Reduce Task Failed at " + cur.getIp());
						stop = true;
						break;
					}
				}
				if (!stop) {
					for (MapperTask mapperTask : job.getMapperTasks()) {
						NodeRef cur = mapperTask.getNode();				
						soc = new Socket(cur.getIp(), cur.getPort());
						ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
						out.writeObject("MapperTask");
						out.writeObject(mapperTask);
						String ret = (String)in.readObject();
						if (!ret.equals("MapperSuccess")) {
							System.out.println("Mapper Task Failed at " + cur.getIp());
							stop = true;
							break;							
						}
					}
				}
				if (!stop) {
					new Thread(new JobMonitor(job)).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					soc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	private class JobMonitor implements Runnable {
		private Job job;
		public JobMonitor(Job job) {
			this.job = job;
		}
		@Override
		public void run() {
			
			
		}
	}
	
}
