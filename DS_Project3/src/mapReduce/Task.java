package mapReduce;

import java.util.ArrayList;

import dfs.BlockRef;
import dfs.NodeRef;

public class Task {
	private int taskID;
	private NodeRef node;
	private String status;
	private Class<?> mapReduce;
	private String outputPath;
	private ArrayList<BlockRef> blockList;
	private ArrayList<NodeRef> reducers;
	
	public Task(NodeRef node, int taskID, Class<?> mapReduce, String outputPath, 
			ArrayList<BlockRef> blockList, ArrayList<NodeRef> reducers) {
		this.node = node;
		this.taskID = taskID;
		this.status = "Initializing";
		this.mapReduce = mapReduce;
		this.outputPath = outputPath;
		this.blockList = blockList;		
		this.reducers = reducers;
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

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public ArrayList<BlockRef> getBlockList() {
		return blockList;
	}

	public void setBlockList(ArrayList<BlockRef> blockList) {
		this.blockList = blockList;
	}

	public ArrayList<NodeRef> getReducers() {
		return reducers;
	}

	public void setReducers(ArrayList<NodeRef> reducers) {
		this.reducers = reducers;
	}

	public NodeRef getNode() {
		return node;
	}

	public void setNode(NodeRef node) {
		this.node = node;
	}
	
}
