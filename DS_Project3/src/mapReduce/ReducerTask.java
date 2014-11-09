package mapReduce;

import dfs.NodeRef;

public class ReducerTask extends Task{
	
	private String outputPath;
	
	public ReducerTask() {
		super();
	}
	
	public ReducerTask(NodeRef node, int taskID, Class<?> mapReduce, String outputPath) {
		super(node, taskID, mapReduce);
		this.setOutputPath(outputPath);
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
