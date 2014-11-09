package mapReduce;

import dfs.NodeRef;

public class Task {
	protected int taskID;
	protected NodeRef node;
	protected String status;
	protected Class<?> mapReduce;	
	public Task() {
		
	}
	
	public Task(NodeRef node, int taskID, Class<?> mapReduce) {
		this.node = node;
		this.taskID = taskID;
		this.status = "Initializing";
		this.mapReduce = mapReduce;
	}

	public int getTaskID() {
		return taskID;
	}
	public String getStatus() {
		return status;
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
	
}
