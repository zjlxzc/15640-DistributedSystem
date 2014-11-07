package HDFS;

import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2019191705281685957L;
	private final static int DEFAULT_Capacity = 120;
	private int id;
	private int capacity;
	private int size;
	private ArrayList<String> records;
	
	public Block(int id, int capacity) {
		this.id = id;
		this.capacity = capacity;
		this.size = 0;
		records = new ArrayList<String>();
	}
	
	public Block(int id) {
		this(id, DEFAULT_Capacity);
	}
	
	public void addRecord(String record) {
		records.add(record);
		size++;
	}
	
	public boolean isFull() {
		return size == this.capacity;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getId() {
		return id;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public ArrayList<String> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<String> records) {
		this.records = records;
	}
	
}
