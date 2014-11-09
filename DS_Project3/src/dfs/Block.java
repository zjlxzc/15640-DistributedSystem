package dfs;

import java.io.File;
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
		this.records.add(record);
		this.size++;
	}
	
	public BlockRef generateRef(NodeRef node, String parentFile, int splitNum) {
		String filename = parentFile + "_" + splitNum;
		try {
			File outputFile = new File(filename);
			System.out.println("generate:" + filename);
			PrintWriter out = new PrintWriter(outputFile);
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
		return this.size == this.capacity;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getId() {
		return this.id;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public ArrayList<String> getRecords() {
		return this.records;
	}
}