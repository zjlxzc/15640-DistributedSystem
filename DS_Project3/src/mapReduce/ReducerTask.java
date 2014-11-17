package mapReduce;

import dfs.NodeRef;

public class ReducerTask extends Task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String outputPath;
	
	public ReducerTask() {
		super();
	}
	
	public ReducerTask(NodeRef node, int taskID, int jobID, Class<?> mapReduce, String outputPath) {
		super(node, taskID, jobID, mapReduce);
		this.setOutputPath(outputPath);
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
