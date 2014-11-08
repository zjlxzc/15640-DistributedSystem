package HDFS;

import java.io.Serializable;

public class BlockRef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String parentFile;
	private int splitNum;
	private NodeRef nodeRef;
	private String fileName;
	
	public BlockRef(NodeRef nodeRef, int id, String parentFile, int splitNum, String fileName) {
		this.id = id;
		this.parentFile = parentFile;
		this.nodeRef = nodeRef;
		this.splitNum = splitNum;
		this.fileName = fileName;
	}

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
