package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a single job.
 */

import java.util.ArrayList;
import java.util.Hashtable;

import dfs.BlockRef;
import dfs.NodeRef;

public class Job {
	private int jobID;							// specify job id
	private ArrayList<MapperTask> mapperTasks;	// array list to store all mapper tasks
	private ArrayList<ReducerTask> reducerTasks;// array list to store all reducer tasks
	private String inputFile;					// user uploaded input file
	private String outputFile;					// user specified output file
	private Class<?> mapReducer;				// user defined mapReduce class
	private Hashtable<NodeRef, ArrayList<BlockRef>> refTable;
	private String status;						// job status
	
	// create a new job with all required information
	public Job(int jobID, String inputFile, String outputFile, Class<?> mapReducer, 
			ArrayList<MapperTask> mapperTasks, ArrayList<ReducerTask> reducerTasks,
			Hashtable<NodeRef, ArrayList<BlockRef>> refTable, String status) {
		
		this.jobID = jobID;
		this.mapperTasks = mapperTasks;
		this.reducerTasks = reducerTasks;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.mapReducer = mapReducer;
		this.refTable = refTable;
		this.setStatus(status);
	}

	public int getJobID() {
		return jobID;
	}

	// add mapper task to corresponding array list
	public void addMapperTasks(MapperTask mapperTask) {
		mapperTasks.add(mapperTask);
	}	
	
	// remove mapper task from corresponding array list
	public void removeMapperTask(MapperTask mapperTask) {
		mapperTasks.remove(mapperTask);
	}

	// add reducer task to corresponding array list
	public void addReducerTasks(ReducerTask reducerTask) {
		reducerTasks.add(reducerTask);
	}
	
	// a set of getters
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
		return outputFile;
	}

	public Class<?> getMapReducer() {
		return mapReducer;
	}

	public Hashtable<NodeRef, ArrayList<BlockRef>> getRefTable() {
		return refTable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
