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
		
		for (NodeRef node : reducers) {
			ReducerTask task = new ReducerTask(node, taskID, mapReduceClass, outputPath);
			job.addReducerTasks(task);
			taskID++;
		}
		
		for (NodeRef node : assignment.keySet()) {
			MapperTask task = new MapperTask(node, taskID, mapReduceClass, assignment.get(node), reducers);
			job.addMapperTasks(task);
			taskID++;
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
					out.writeObject("MapReduceTask");
					out.writeObject(reducerTask);
					String ret = (String)in.readObject();
					if (!ret.equals("ReduceSuccess")) {
						System.out.println("Reduce Task Failed at " + cur.getIp());
						stop = true;
						break;
					}
				}
				if (!stop) {
					
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
}
