package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a task.
 */

import java.io.Serializable;

import dfs.NodeRef;

public class Task implements Serializable {

	private static final long serialVersionUID = 1L; // generated id
	protected int taskID;
	protected int jobID;
	protected NodeRef node;
	protected String status;
	protected Class<?> mapReduce;	
	
	public Task() {	
	}
	
	public Task(NodeRef node, int taskID, int jobID, Class<?> mapReduce) {
		this.node = node;
		this.taskID = taskID;
		this.status = "Initializing";
		this.mapReduce = mapReduce;
		this.jobID = jobID;
	}

	// a set of getters and setters
	public int getTaskID() {
		return taskID;
	}
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Class<?> getMapReducer() {
		return mapReduce;
	}

	public void setMapReducer(Class<?> mapReduce) {
		this.mapReduce = mapReduce;
	}

	public NodeRef getNode() {
		return node;
	}

	public void setNode(NodeRef node) {
		this.node = node;
	}

	public int getJobID() {
		return jobID;
	}	
}
