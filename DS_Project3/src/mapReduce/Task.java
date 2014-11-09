package mapReduce;

public class Task {

	private int taskID;
	private int jobID;
	private String status;
	
	public Task(int taskID, int jobID, String status) {
		super();
		this.taskID = taskID;
		this.jobID = jobID;
		this.status = status;
	}
	
	public int getTaskID() {
		return taskID;
	}
	
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	public int getJobID() {
		return jobID;
	}
	
	public void setJobID(int jobID) {
		this.jobID = jobID;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
