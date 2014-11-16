package mapReduce;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		System.out.println("JobTracker start");
		return jobTracker;
	}
	
	public void createJob(String inputFile, int splitNum, Hashtable<NodeRef, ArrayList<BlockRef>> refTable, 
					String outputPath, Class<?> mapReduceClass) {
				
		HashSet<String> splitNameSet = new HashSet<String>();
		HashMap<NodeRef, ArrayList<BlockRef>> assignment = new HashMap<NodeRef, ArrayList<BlockRef>>();
		int taskID = 1;
		
		while (splitNum > 0) {
			for (NodeRef node : refTable.keySet()) {
				ArrayList<BlockRef> blockList = refTable.get(node);
		
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
						break;
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
		
		
		try {
			ArrayList<NodeRef> newReducers = new ArrayList<NodeRef>();
			for (NodeRef node : reducers) {			
				soc = new Socket(node.getIp(), node.getPort());
				ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(soc.getInputStream());				
				out.writeObject("StartTaskTracker");
				out.flush();
				int port = Integer.parseInt((String)in.readObject());
				NodeRef refToTaskTracker = new NodeRef(node.getIp().getHostAddress(), port);		
				ReducerTask task = new ReducerTask(refToTaskTracker, taskID, mapReduceClass, outputPath);
				job.addReducerTasks(task);
				taskID++;
				newReducers.add(refToTaskTracker);
				in.close();
				out.close();
				soc.close();
			}
			HashMap<String, Integer> ips = new HashMap<String, Integer>();
			for (NodeRef node : assignment.keySet()) {
				NodeRef refToTaskTracker = null;
				if (!ips.containsKey(node.getIp().getHostAddress())) {
					soc = new Socket(node.getIp(), node.getPort());
					ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(soc.getInputStream());					
					out.writeObject("StartTaskTracker");
					out.flush();
					int port = Integer.parseInt((String)in.readObject());
					refToTaskTracker = new NodeRef(node.getIp().getHostAddress(), port);	
					System.out.println(refToTaskTracker.getIp() + " : " + refToTaskTracker.getPort());
					ips.put(node.getIp().getHostAddress(), port);
					in.close();
					out.close();
					soc.close();
				} else {
					String curIp = node.getIp().getHostAddress();
					refToTaskTracker = new NodeRef(curIp, ips.get(curIp));
				}				
				MapperTask task = new MapperTask(refToTaskTracker, taskID, mapReduceClass, 
						new ArrayList<BlockRef>(assignment.get(node)), new ArrayList<NodeRef>(newReducers));
				System.out.println(job.getMapperTasks().size());
				System.out.println(task.getBlockList());
				job.addMapperTasks(task);
				taskID++;

			}			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
				String ip = mapper.getNode().getIp().getHostAddress();
				int port = mapper.getNode().getPort();
				System.out.println(ip);
				ArrayList<BlockRef> blocks = mapper.getBlockList();
				for (BlockRef block : blocks) {
					System.out.println(ip + " : " + port + " : " + block.getFileName());
				}				
			}
			System.out.println("Reducer Job is running on :");
			for (ReducerTask reducer : reducers) {
				String ip = reducer.getNode().getIp().getHostAddress();
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
					out.flush();

					in.close();
					out.close();
					soc.close();
				}
				if (!stop) {
					for (MapperTask mapperTask : job.getMapperTasks()) {
						NodeRef cur = mapperTask.getNode();				
						soc = new Socket(cur.getIp(), cur.getPort());
						ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
						out.writeObject("MapperTask");
						out.writeObject(mapperTask);
						out.flush();

						in.close();
						out.close();
						soc.close();
					}
				}
				if (!stop) {
					new Thread(new JobMonitor(job)).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				System.out.println("MapperTasks: " + mapperTasks.toString());
				Socket jobTracker = null;
				while (!mapperStatus.isEmpty()) {
					for (MapperTask task : mapperTasks) {
						System.out.println("MapperStatus: " + mapperStatus.toString());
						System.out.println("CurTask : " + task.getTaskID());
						NodeRef taskTracker = task.getNode();
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						System.out.println(jobTracker.getLocalAddress() + " : " + jobTracker.getLocalPort());
						System.out.println("IN JOBTRA1 " + task.getNode().getIp() + " * " + task.getNode().getPort());
						ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());							
						System.out.println("IN JOBTRA2 " + task.getNode().getIp() + " * " + task.getNode().getPort());
						out.writeObject("ReportMapper");
						out.flush();	
						Thread.sleep(1000);
						System.out.println("IN JOBTRA3 " + task.getNode().getIp() + " * " + task.getNode().getPort());
						ObjectInputStream in = new ObjectInputStream(jobTracker.getInputStream());
						System.out.println("IN JOBTRA4 " + task.getNode().getIp() + " * " + task.getNode().getPort());
						Hashtable<Integer, Hashtable<Integer, String>> nodeReport = 
								(Hashtable<Integer, Hashtable<Integer, String>>)in.readObject();
						System.out.println("JobTracker MapperReport: " + nodeReport.toString());
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
						in.close();
						out.close();
						jobTracker.close();
					}					
				}
				System.out.println("Mappers Finished!");
				
				ArrayList<ReducerTask> reducerTasks = job.getReducerTasks();
				HashSet<Integer> reducerStatus = new HashSet<Integer>();
				for (ReducerTask task : reducerTasks) {
					reducerStatus.add(task.getTaskID());
					NodeRef taskTracker = task.getNode();					
					jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
					ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());
					@SuppressWarnings("unused")
					ObjectInputStream in = new ObjectInputStream(jobTracker.getInputStream());
					System.out.println("JOBTRACKER: " + task.getStatus());
					out.writeObject("MapperFinished");
					out.flush();
				}
				while (!reducerStatus.isEmpty()) {
					for (ReducerTask task : reducerTasks) {
						NodeRef taskTracker = task.getNode();					
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(jobTracker.getInputStream());
						out.writeObject("ReportReducer");
						out.flush();
						String report = (String)in.readObject();
						if (report.equals("finished")) {
							reducerStatus.remove(task.getTaskID());
						}
						Thread.sleep(5000);
					}
				}				
				System.out.println("Job Finished!");				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
