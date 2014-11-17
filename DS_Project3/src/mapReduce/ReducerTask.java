package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a reducer task.
 */

import dfs.NodeRef;

public class ReducerTask extends Task{

	private static final long serialVersionUID = 1L; // generated id
	private String outputFile;
	
	public ReducerTask() {
		super();
	}
	
	public ReducerTask(NodeRef node, int taskID, Class<?> mapReduce, String outputPath) {
		super(node, taskID, mapReduce);
		this.setOutputPath(outputPath);
	}

	public String getOutputPath() {
		return outputFile;
	}

	public void setOutputPath(String outputFile) {
		this.outputFile = outputFile;
	}

}
