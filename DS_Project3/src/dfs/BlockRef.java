package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a file block reference.
 * It has user input file information, the data node it is sitting and the order of itself.
 */

import java.io.Serializable;

public class BlockRef implements Serializable {

	private static final long serialVersionUID = 1L; // generated id
	private int id; // block id
	private String parentFile; // user uploaded file
	private int splitNum; // the order of this split
	private NodeRef nodeRef; // node reference
	private String fileName;
	
	// a constructor with all required information to create this block reference
	public BlockRef(NodeRef nodeRef, int id, String parentFile, int splitNum, String fileName) {
		this.id = id;
		this.parentFile = parentFile;
		this.nodeRef = nodeRef;
		this.splitNum = splitNum;
		this.fileName = fileName;
	}

	// a set of getters to be assessed outside of this class
	public int getId() {
		return id;
	}
	
	public int getSplitNum() {
		return splitNum;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public NodeRef getNodeRef() {
		return nodeRef;
	}
	
	public String getParentFile() {
		return parentFile;
	}
}
