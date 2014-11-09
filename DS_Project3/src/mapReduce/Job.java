package mapReduce;

public class Job {
	private int jobID;
	private String status;
	private String mapReducePath;
	private String inputPath;
	private String outputPath;
	
	public Job(int jobID, String mapperPath, String reducerPath, String mapReducePath, 
					String inputPath, String outputPath) {
		this.jobID = jobID;
		status = "Initializing";
		this.mapReducePath = mapReducePath;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
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
	
	public String getMapReducerPath() {
		return mapReducePath;
	}

	public void setMapReducerPath(String mapReducePath) {
		this.mapReducePath = mapReducePath;
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
