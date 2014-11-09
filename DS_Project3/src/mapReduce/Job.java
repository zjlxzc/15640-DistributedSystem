package mapReduce;

import java.util.ArrayList;

public class Job {
	private int jobID;
	private String inputFile;
	private String outputPath;
	private Class<?> mapReduceClass;
	private ArrayList<Task> tasks;
	
	public Job(int jobID, String inputFile, String outputPath,
				Class<?> mapReduceClass, ArrayList<Task> tasks) {
		
		this.jobID = jobID;
		this.inputFile = inputFile;
		this.outputPath = outputPath;
		this.mapReduceClass = mapReduceClass;
		this.tasks = tasks;
	}

	public int getJobID() {
		return jobID;
	}

	public String getInputFile() {
		return inputFile;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public Class<?> getMapReduceClass() {
		return mapReduceClass;
	}

	public void setMapReduceClass(Class<?> mapReduceClass) {
		this.mapReduceClass = mapReduceClass;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void addTasks(Task task) {
		tasks.add(task);
	}	
}
