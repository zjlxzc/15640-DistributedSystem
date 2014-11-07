package HDFS;

import java.io.Serializable;

public class BlockRef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String fileName;
	private int startRecord;
	private int endRecord;
	private NodeRef nodeRef;
	
	public BlockRef(NodeRef nodeRef, int id, String fileName, int start, int end) {
		this.id = id;
		this.fileName = fileName;
		this.startRecord = start;
		this.endRecord = end;
		this.nodeRef = nodeRef;
	}
	
	public boolean equals(BlockRef ref) {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(int startRecord) {
		this.startRecord = startRecord;
	}

	public int getEndRecord() {
		return endRecord;
	}

	public void setEndRecord(int endRecord) {
		this.endRecord = endRecord;
	}
}
