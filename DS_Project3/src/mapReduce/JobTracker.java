package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a job tracker.
 */

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
	private int taskID;

	public JobTracker() {
		jobID = 1;
	}

	// using singleton to ensure only one instance
	public static JobTracker getInstance() {
		if (jobTracker == null) {
			jobTracker = new JobTracker();
			jobList = new ArrayList<Job>();
		}
		return jobTracker;
	}

	// create a map reduce job
	public void createJob(String inputFile, int splitNum,
			Hashtable<NodeRef, ArrayList<BlockRef>> refTable,
			String outputPath, Class<?> mapReduceClass) {
		
		HashSet<String> splitNameSet = new HashSet<String>(); // use hash set to ensure one file split gets processed once
		HashMap<NodeRef, ArrayList<BlockRef>> assignment = new HashMap<NodeRef, ArrayList<BlockRef>>();
		taskID = 1;

		while (splitNum > 0) { // loop until there is not split needs to be processed
			for (NodeRef node : refTable.keySet()) { // loop for each node
				ArrayList<BlockRef> blockList = refTable.get(node); // get block list on that node

				int i = 0;
				for (; i < blockList.size(); i++) { // loop for each block
					String curSplitName = blockList.get(i).getFileName();
					
					if (!splitNameSet.contains(curSplitName)) { // if this file split not gets processed
						if (!assignment.containsKey(node)) {
							ArrayList<BlockRef> list = new ArrayList<BlockRef>();
							list.add(blockList.get(i));
							assignment.put(node, list);
						} else {
							assignment.get(node).add(blockList.get(i));
						}
						
						splitNameSet.add(curSplitName); // add processed split
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
		
		ArrayList<NodeRef> reducers = new ArrayList<NodeRef>(); // an array list to store reducer
		reducers.add(reducer);

		// create a new job
		Job job = new Job(jobID, inputFile, outputPath, mapReduceClass,
				new ArrayList<MapperTask>(), new ArrayList<ReducerTask>(), refTable, "initializing");
		jobList.add(job); // add that job to job list
		
		ArrayList<NodeRef> newReducers = new ArrayList<NodeRef>();
		dispatchReducer(job, reducers, newReducers); // dispatch reducer task
		dispatchMapper(job, assignment, newReducers); // dispatch mapper task
		
		new Thread(new JobRunner(job)).start();
	}
	
	public void dispatchReducer(Job job, ArrayList<NodeRef> reducers, ArrayList<NodeRef> newReducers) {
		Socket soc = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		for (NodeRef node : reducers) { // loop for each reducer
			try {
				soc = new Socket(node.getIp(), node.getPort());	// establish connection		
				out = new ObjectOutputStream(soc.getOutputStream());
				in = new ObjectInputStream(soc.getInputStream());
				out.writeObject("StartTaskTracker"); // send out signal
				out.flush();
				
				int port = Integer.parseInt((String) in.readObject()); // get replied port
				NodeRef refToTaskTracker = new NodeRef(node.getIp()
						.getHostAddress(), port);
				// create reducer task
				ReducerTask task = new ReducerTask(refToTaskTracker, taskID, jobID,
						job.getMapReducer(), job.getOutputPath());
				
				job.addReducerTasks(task);
				taskID++;
				newReducers.add(refToTaskTracker);
				
				in.close();
				out.close();
				soc.close();
			} catch (IOException e) {
				System.out.println("In JobTracker - IOException: " + e.getMessage());
			} catch (NumberFormatException e) {
				System.out.println("In JobTracker - NumberFormatException: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("In JobTracker - ClassNotFoundException: " + e.getMessage());
			}
		}
	}
	
	public void dispatchMapper(Job job, HashMap<NodeRef, ArrayList<BlockRef>> assignment, 
			ArrayList<NodeRef> newReducers) {
		Socket soc = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try {	
			HashMap<String, Integer> ips = new HashMap<String, Integer>();
			
			for (NodeRef node : assignment.keySet()) { // loop for each node to do mapper task
				NodeRef refToTaskTracker = null;
				
				if (!ips.containsKey(node.getIp().getHostAddress())) {
					soc = new Socket(node.getIp(), node.getPort());
					out = new ObjectOutputStream(soc.getOutputStream());
					in = new ObjectInputStream(soc.getInputStream());
					
					out.writeObject("StartTaskTracker"); // send out signal
					out.flush();
					
					int port = Integer.parseInt((String) in.readObject());
					refToTaskTracker = new NodeRef(node.getIp()
							.getHostAddress(), port);
					ips.put(node.getIp().getHostAddress(), port);
					
					in.close();
					out.close();
					soc.close();
				} else { // if ip table does not contain this node
					String curIp = node.getIp().getHostAddress();
					refToTaskTracker = new NodeRef(curIp, ips.get(curIp));
				}
				
				// create a new mapper task
				MapperTask task = new MapperTask(refToTaskTracker, taskID, jobID, 
						job.getMapReducer(), new ArrayList<BlockRef>(
								assignment.get(node)), new ArrayList<NodeRef>(
								newReducers));
				
				job.addMapperTasks(task);
				taskID++;
			}
		} catch (IOException e) {
			System.out.println("In JobTracker - dispatchMapper() method: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("In JobTracker - dispatchMapper() method: " + e.getMessage());
		}
	}
		
	// list all job information
	public void ListJobs() {
		if (jobList.size() == 0) {
			System.out.println("There is no job on this system!");
		}
		
		for (Job job : jobList) { // for each job, display details of this job
			ArrayList<MapperTask> mappers = job.getMapperTasks();
			ArrayList<ReducerTask> reducers = job.getReducerTasks();
			System.out.println("Job ID       : " + job.getJobID());
			System.out.println("InputFile    : " + job.getInputFile());
			System.out.println("OutputFile   : " + job.getOutputPath());
			System.out.println("MapperReducer: " + job.getMapReducer().getSimpleName());
			
			if (job.getStatus().equals("finished")) { // if this job is finished
				System.out.println("Status       : finished");
				continue;
			}	
			
			System.out.println("Status       : " + job.getStatus());			
			System.out.println("Mapper Job is running on :");
			
			for (MapperTask mapper : mappers) { // get mapper task information
				String ip = mapper.getNode().getIp().getHostAddress();
				int port = mapper.getNode().getPort();
				ArrayList<BlockRef> blocks = mapper.getBlockList();
				
				for (BlockRef block : blocks) {
					System.out.println(ip + " : " + port + " : "
							+ block.getFileName());
				}
			}
			
			System.out.println("Reducer Job is running on :");
			
			for (ReducerTask reducer : reducers) { // get reducer task information
				String ip = reducer.getNode().getIp().getHostAddress();
				int port = reducer.getNode().getPort();
				System.out.println(ip + " : " + port);
			}
		}
	}

	// run this class in a separate thread
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
					
					ObjectOutputStream out = new ObjectOutputStream(
							soc.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(
							soc.getInputStream());
					
					out.writeObject("ReduceTask"); // send out signal
					out.writeObject(reducerTask);
					out.flush();

					in.close();
					out.close();
					soc.close();
				}
				
				if (!stop) {
					for (MapperTask mapperTask : job.getMapperTasks()) { // connect to each mapper
						NodeRef cur = mapperTask.getNode();
						soc = new Socket(cur.getIp(), cur.getPort());
						
						ObjectOutputStream out = new ObjectOutputStream(
								soc.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(
								soc.getInputStream());
						
						out.writeObject("MapperTask"); // send out signal
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
				System.out.println(e);
			}
		}
	}

	// run job monitor in a separated thread
	private class JobMonitor implements Runnable {
		private Job job;

		public JobMonitor(Job job) {
			this.job = job;
			job.setStatus("running");
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {			
			ArrayList<MapperTask> mapperTasks = job.getMapperTasks();
			Hashtable<Integer, HashSet<Integer>> mapperStatus = new Hashtable<Integer, HashSet<Integer>>();
			
			for (MapperTask task : mapperTasks) { // get each mapper task
				HashSet<Integer> blockIDs = new HashSet<Integer>();
				
				for (BlockRef block : task.getBlockList()) { // get each block reference
					blockIDs.add(block.getId());
				}
				mapperStatus.put(task.getTaskID(), blockIDs); // put each mapper task status
			}
			
			Socket jobTracker = null;				
			while (!mapperStatus.isEmpty()) { // go through each mapper status
				for (MapperTask task : mapperTasks) { // get each mapper task
					NodeRef taskTracker = task.getNode();
					
					if (!mapperStatus.containsKey(task.getTaskID())) {
						continue;
					}
					
					try {							
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());	
						
						out.writeObject("ReportMapper"); // send out signal
						out.flush();	
						
						ObjectInputStream in = new ObjectInputStream(jobTracker.getInputStream());
						Hashtable<Integer, Hashtable<Integer, String>> nodeReport = 
								(Hashtable<Integer, Hashtable<Integer, String>>)in.readObject();
						
						for (Integer taskID : nodeReport.keySet()) {
							HashSet<Integer> blockIDs = mapperStatus.get(taskID);
							Hashtable<Integer, String> blockReport = nodeReport.get(taskID);
							
							for (Integer blockID : blockReport.keySet()) {	 // go through each block							
								if (blockReport.get(blockID).equals("finished")) { // if this block is finished
									blockIDs.remove(blockID); // remove this block from the list
								}
								if (blockIDs.isEmpty()) { // if all blocks are finished
									mapperStatus.remove(taskID); // remove this task
								}
							}
						}	
						
						in.close();
						out.close();
						jobTracker.close();
						
						Thread.sleep(1000);
					}catch (IOException e) {
						System.out.println("Mapper tast failed on " + taskTracker.getIp() + " : " + taskTracker.getPort());
						reAddMapper(job, task);
					} catch (ClassNotFoundException e) {
						System.out.println(e.getMessage());
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
				}
			}
			System.out.println("Mappers Finished!");
				
			try {
				ArrayList<ReducerTask> reducerTasks = job.getReducerTasks();
				HashSet<Integer> reducerStatus = new HashSet<Integer>();
				
				for (ReducerTask task : reducerTasks) { // go through each reducer task
					reducerStatus.add(task.getTaskID());
					NodeRef taskTracker = task.getNode();	
					
					jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
					ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());
					
					out.writeObject("MapperFinished"); // send out signal
					out.flush();
				}
				
				while (!reducerStatus.isEmpty()) {
					for (ReducerTask task : reducerTasks) {
						NodeRef taskTracker = task.getNode();					
						jobTracker = new Socket(taskTracker.getIp(), taskTracker.getPort());
						ObjectOutputStream out = new ObjectOutputStream(jobTracker.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(jobTracker.getInputStream());
						
						out.writeObject("ReportReducer"); // send out signal
						out.flush();
						String report = (String)in.readObject(); // get status
						
						if (report.equals("finished")) {
							reducerStatus.remove(task.getTaskID());
							break;
						}
						
						Thread.sleep(1000);
					}
				}				
				
				System.out.println("Job Finished!");
				job.setStatus("finished");
				Hashtable<NodeRef, ArrayList<BlockRef>> refTable = job.getRefTable();
				
				for (ReducerTask task : reducerTasks) { // go through each reducer task
					reducerStatus.add(task.getTaskID());
					NodeRef taskTracker = task.getNode();	
					
					for (NodeRef reducer : refTable.keySet()) { // go through each reducer node
						if (reducer.getIp().getHostAddress().equals(taskTracker.getIp().getHostAddress())) {
							Socket uploadSoc = new Socket(reducer.getIp(), reducer.getPort());
							ObjectOutputStream out = new ObjectOutputStream(uploadSoc.getOutputStream());
							
							out.writeObject("upload"); // send out signal
							out.writeObject(task.getOutputPath());
							
							out.flush();
							Thread.sleep(100);
							
							out.close();
							uploadSoc.close();							
						}
					}
				}					
					
			} catch (IOException e) {
				System.out.println("In JobTracker - JobMonitor: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("In JobTracker - JobMonitor: " + e.getMessage());
			} catch (InterruptedException e) {
				System.out.println("In JobTracker - JobMonitor: " + e.getMessage());
			}
		}
	}
	
	// if mapper task failed, start new mapper task of corresponding block
	private void reAddMapper(Job job, MapperTask task) {
		ArrayList<BlockRef> failedBlockList = task.getBlockList();
		Hashtable<NodeRef, ArrayList<BlockRef>> refTable = jobList.get(task.getJobID() - 1).getRefTable();			
		HashMap<NodeRef, ArrayList<BlockRef>> assignment = new HashMap<NodeRef, ArrayList<BlockRef>>();
		
		int size = failedBlockList.size();
		HashSet<String> splitNameSet = new HashSet<String>();
		
		for (BlockRef block : failedBlockList) {
			splitNameSet.add(block.getFileName());
		}
		
		while (size > 0) {
			for (NodeRef node : refTable.keySet()) { // go through each node to check status
				if (!node.getIp().getHostAddress().equals(task.getNode().getIp().getHostAddress())) {
					ArrayList<BlockRef> blockList = refTable.get(node);
					
					int i = 0;
					for (; i < blockList.size(); i++) { // go through each block list
						String curSplitName = blockList.get(i).getFileName();
						
						if (splitNameSet.contains(curSplitName)) {
							if (!assignment.containsKey(node)) {
								ArrayList<BlockRef> list = new ArrayList<BlockRef>();
								list.add(blockList.get(i));
								assignment.put(node, list);
							} else {
								assignment.get(node).add(blockList.get(i));
							}
							
							size--;
							break;
						}
					}
				}
				
				if (size == 0) { // break loop if reach the last data node
					break;
				}
			}				
		}
		
		// redo mapper task
		dispatchMapper(job, assignment, task.getReducers());
	}
}
