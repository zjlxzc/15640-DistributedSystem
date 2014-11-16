package mapReduce;

import java.util.ArrayList;

import dfs.BlockRef;
import dfs.NodeRef;

public class MapperTask extends Task{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<BlockRef> blockList;
	private ArrayList<NodeRef> reducers;
	
	public MapperTask() {
		super();
	}
	
	public MapperTask(NodeRef node, int taskID, Class<?> mapReduce,
			ArrayList<BlockRef> blockList, ArrayList<NodeRef> reducers) {
		super(node, taskID, mapReduce);
		this.blockList = blockList;
		this.reducers = reducers;
	}
	
	public void addBlock(BlockRef ref) {
		blockList.add(ref);
	}
	
	public void addRecucers(NodeRef ref) {
		reducers.add(ref);
	}
	
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
