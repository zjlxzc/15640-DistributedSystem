package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a file block.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Block {
	private final static int DEFAULT_Capacity = 120; // default capacity
	private int id; // block id
	private int capacity; // user defined capacity
	private int size; // current size
	private ArrayList<String> records; // an array list to store incoming records
	
	public Block(int id, int capacity) {
		this.id = id;
		this.capacity = capacity;
		this.size = 0;
		records = new ArrayList<String>();
	}
	
	public Block(int id) {
		this(id, DEFAULT_Capacity); // set default capacity
	}
	
	public void addRecord(String record) { // add a new record
		this.records.add(record);
		this.size++;
	}
	
	public BlockRef generateRef(NodeRef node, String parentFile, int splitNum) {
		String filename = parentFile + "_" + splitNum; // create a new file name
		try {
			File outputFile = new File(filename); // create a new file
			PrintWriter out = new PrintWriter(outputFile);
			for (int i = 0; i < records.size(); i++) { // write all records to this new file
				out.println(records.get(i));
			}
			out.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BlockRef ref = new BlockRef(node, id, parentFile, splitNum, filename); // create a block reference
		return ref;
	}
	
	public boolean isFull() { // check to see if this block is full
		return this.size == this.capacity;
	}
	
	public int getSize() { // return current size
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
