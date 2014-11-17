package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a single job.
 */

import java.util.ArrayList;

public class Job {
	private int jobID;
	private ArrayList<MapperTask> mapperTasks;
	private ArrayList<ReducerTask> reducerTasks;
	private String inputFile;
	private String outputPath;
	private Class<?> mapReducer;
	
	public Job(int jobID, String inputFile, String outputPath, Class<?> mapReducer, 
			ArrayList<MapperTask> mapperTasks, ArrayList<ReducerTask> reducerTasks) {
		
		this.jobID = jobID;
		this.mapperTasks = mapperTasks;
		this.reducerTasks = reducerTasks;
		this.inputFile = inputFile;
		this.outputPath = outputPath;
		this.mapReducer = mapReducer;
	}

	public int getJobID() {
		return jobID;
	}

	// add tasks to corresponding array list
	public void addMapperTasks(MapperTask mapperTask) {
		mapperTasks.add(mapperTask);
	}	

	public void addReducerTasks(ReducerTask reducerTask) {
		reducerTasks.add(reducerTask);
	}
	
	// getters
	public ArrayList<MapperTask> getMapperTasks() {
		return mapperTasks;
	}

	public ArrayList<ReducerTask> getReducerTasks() {
		return reducerTasks;
	}

	public String getInputFile() {
		return inputFile;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public Class<?> getMapReducer() {
		return mapReducer;
	}

}
