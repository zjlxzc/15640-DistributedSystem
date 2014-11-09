package mapReduce;

import java.util.ArrayList;
import java.util.HashMap;

public class JobTracker {
	private static JobTracker jobTracker = null;
	private static int jobID;
	private static HashMap<Integer, ArrayList<TaskTracker>> jobTable; // jobTable<jobID, NodeIp>
	
	public JobTracker() {
		jobID = 0;		
	}
	
	public static JobTracker getInstance() {
		if (jobTracker == null) {
			jobTracker = new JobTracker();
		}
		return jobTracker;
	}
	
	public void excuteJob(Job job) {
		
	}
			
	public int getJobID() {
		return jobID;
	}

	public void setJobNum(int jobID) {
		this.jobID = jobID;
	}	
	
	public HashMap<Integer, ArrayList<TaskTracker>> getJobTable() {
		return jobTable;
	}

	public void setJobTable(HashMap<Integer, ArrayList<TaskTracker>> jobTable) {
		this.jobTable = jobTable;
	}
}
