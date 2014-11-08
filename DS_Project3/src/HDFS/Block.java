package HDFS;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Block {
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
	
	public BlockRef generateRef(NodeRef node, String parentFile, int splitNum) {
		String filename = parentFile + "_" + id;
		try {
			PrintWriter out = new PrintWriter(filename);
			for (int i = 0; i < records.size(); i++) {
				out.println(records.get(i));
			}
			out.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BlockRef ref = new BlockRef(node, id, parentFile, splitNum, filename);
		return ref;
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

	public ArrayList<String> getRecords() {
		return records;
	}
}
