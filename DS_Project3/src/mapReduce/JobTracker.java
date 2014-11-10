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
	private static ArrayList<Job> jobList; 
	
	public JobTracker() {
		jobID = 1;		
	}
	
	public static JobTracker getInstance() {
		if (jobTracker == null) {
			jobTracker = new JobTracker();
			jobList = new ArrayList<Job>();
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
		
		Job job = new Job(jobID, inputFile, outputPath, mapReduceClass,
				new ArrayList<MapperTask>(), new ArrayList<ReducerTask>());
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
	
	public void ListJobs() {
		for (Job job : jobList) {
			ArrayList<MapperTask> mappers = job.getMapperTasks();
			ArrayList<ReducerTask> reducers = job.getReducerTasks();
			System.out.println("Job ID       : " + job.getJobID());
			System.out.println("InputFile    : " + job.getInputFile());
			System.out.println("OutputPath   : " + job.getOutputPath());
			System.out.println("MapperReducer: " + job.getMapReducer().getSimpleName());
			System.out.println("Mapper Job is running on :");
			for (MapperTask mapper : mappers) {
				String ip = mapper.getNode().getIp().toString();
				int port = mapper.getNode().getPort();
				ArrayList<BlockRef> blocks = mapper.getBlockList();
				for (BlockRef block : blocks) {
					System.out.println(ip + " : " + port + " : " + block.getFileName());
				}				
			}
			System.out.println("Reducer Job is running on :");
			for (ReducerTask reducer : reducers) {
				String ip = reducer.getNode().getIp().toString();
				int port = reducer.getNode().getPort();
				System.out.println(ip + " : " + port);				
			}
		}
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
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				ArrayList<MapperTask> mapperTasks = job.getMapperTasks();
				Hashtable<Integer, HashSet<Integer>> mapperStatus = new Hashtable<Integer, HashSet<Integer>>();
				for (MapperTask task : mapperTasks) {
					HashSet<Integer> blockIDs = new HashSet<Integer>();
					for (BlockRef block : task.getBlockList()) {
						blockIDs.add(block.getId());
					}
					mapperStatus.put(task.getTaskID(), blockIDs);
				}
								
				ObjectOutputStream out;
				ObjectInputStream in;
				Socket jobTracker = null;
				while (!mapperStatus.isEmpty()) {
					for (MapperTask task : mapperTasks) {
						NodeRef taskTracker = task.getNode();					
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						out = new ObjectOutputStream(jobTracker.getOutputStream());
						in = new ObjectInputStream(jobTracker.getInputStream());
						out.writeObject("ReportMapper");
						Hashtable<Integer, Hashtable<Integer, String>> nodeReport = 
								(Hashtable<Integer, Hashtable<Integer, String>>)in.readObject();
						for (Integer taskID : nodeReport.keySet()) {
							HashSet<Integer> blockIDs = mapperStatus.get(taskID);
							Hashtable<Integer, String> blockReport = nodeReport.get(taskID);
							for (Integer blockID : blockReport.keySet()) {
								if (blockReport.get(blockID).equals("finished")) {
									blockIDs.remove(blockID);
								}
								if (blockIDs.isEmpty()) {
									mapperStatus.remove(taskID);
								}
							}
						}
					}					
				}
				System.out.println("Mappers Finished!");
				
				ArrayList<ReducerTask> reducerTasks = job.getReducerTasks();
				HashSet<Integer> reducerStatus = new HashSet<Integer>();
				for (ReducerTask task : reducerTasks) {
					reducerStatus.add(task.getTaskID());
				}
				while (!reducerStatus.isEmpty()) {
					for (ReducerTask task : reducerTasks) {
						NodeRef taskTracker = task.getNode();					
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						out = new ObjectOutputStream(jobTracker.getOutputStream());
						in = new ObjectInputStream(jobTracker.getInputStream());
						out.writeObject("ReportReducer");
						String report = (String)in.readObject();
						if (report.equals("finished")) {
							reducerStatus.remove(task.getTaskID());
						}
					}
				}				
				System.out.println("Job Finished!");				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
