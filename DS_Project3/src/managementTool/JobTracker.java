package managementTool;

import java.util.ArrayList;
import java.util.HashMap;

public class JobTracker {
	private static JobTracker jobTracker = null;
	private static int jobNum;
	
	// jobTable<jobID, NodeIp>
	private static HashMap<Integer, ArrayList<String>> jobTable;
	private JobTracker() {
		jobNum = 0;		
	}
	public static JobTracker getInstance() {
		if (jobTracker == null) {
			jobTracker = new JobTracker();
		}
		return jobTracker;
	}
	
	public void excuteJob(Job job) {
		
	}
		
	
	public int getJobNum() {
		return jobNum;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}	
	
}
