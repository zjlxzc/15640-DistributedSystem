package managementTool;

public class Job {
	private int jobID;
	private String mapperPath;
	private String reducerPath;
	private String inputPath;
	private String outputPath;
	
	public Job(int jobID, String mapperPath, String reducerPath, 
					String inputPath, String outputPath) {
		this.jobID = jobID;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.mapperPath = mapperPath;
		this.reducerPath = reducerPath;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public void setMapperPath(String mapperPath) {
		this.mapperPath = mapperPath;
	}

	public String getReducerPath() {
		return reducerPath;
	}

	public void setReducerPath(String reducerPath) {
		this.reducerPath = reducerPath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
}
