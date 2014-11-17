package mapReduce;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a mapper task.
 */

import java.util.ArrayList;

import dfs.BlockRef;
import dfs.NodeRef;

public class MapperTask extends Task{

	private static final long serialVersionUID = 1L; // generated id
	private ArrayList<BlockRef> blockList;
	private ArrayList<NodeRef> reducers;
	
	public MapperTask() {
		super();
	}
	
	public MapperTask(NodeRef node, int taskID, int jobID, Class<?> mapReduce,
			ArrayList<BlockRef> blockList, ArrayList<NodeRef> reducers) {
		super(node, taskID, jobID, mapReduce);
		this.blockList = blockList;
		this.reducers = reducers;
	}
	
	// add a block reference to the block list
	public void addBlock(BlockRef ref) {
		blockList.add(ref);
	}
	
	// add a block reference to reducer list
	public void addRecucers(NodeRef ref) {
		reducers.add(ref);
	}
	
	// getters and setters
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
}
