package mapReduce;

import java.util.ArrayList;

public class Job {
	private int jobID;
	private ArrayList<MapperTask> mapperTasks;
	private ArrayList<ReducerTask> reducerTasks;
	
	public Job(int jobID, ArrayList<MapperTask> mapperTasks, ArrayList<ReducerTask> reducerTasks) {
		
		this.jobID = jobID;
		this.mapperTasks = mapperTasks;
		this.reducerTasks = reducerTasks;
	}

	public int getJobID() {
		return jobID;
	}

	public void addMapperTasks(MapperTask mapperTask) {
		mapperTasks.add(mapperTask);
	}	

	public void addReducerTasks(ReducerTask reducerTask) {
		reducerTasks.add(reducerTask);
	}

	public ArrayList<MapperTask> getMapperTasks() {
		return mapperTasks;
	}

	public ArrayList<ReducerTask> getReducerTasks() {
		return reducerTasks;
	}
}
