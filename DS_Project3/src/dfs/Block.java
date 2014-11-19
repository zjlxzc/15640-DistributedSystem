package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a file block.
 * It has block ID, block size, and the records information.
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
	
	// constructor to initialize a block instance
	public Block(int id, int capacity) { 
		this.id = id;
		this.capacity = capacity;
		this.size = 0;
		records = new ArrayList<String>();
	}
	
	// set default capacity if not specified capacity
	public Block(int id) {
		this(id, DEFAULT_Capacity); 
	}
	
	// add a new record to the array list
	public void addRecord(String record) { 
		this.records.add(record);
		this.size++;
	}
	
	// This method generates a block reference to by used by Data Node
	public BlockRef generateRef(NodeRef node, String parentFile, int splitNum) {
		String filename = parentFile + "_" + splitNum; // create a new file name to differentiate blocks
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
		
		// create a block reference by create a new instance
		BlockRef ref = new BlockRef(node, id, parentFile, splitNum, filename);
		return ref;
	}
	
	// check to see if this block is full
	public boolean isFull() {
		return this.size == this.capacity;
	}
	
	// return current size
	public int getSize() { 
		return this.size;
	}
	
	// return the id of this block
	public int getId() {
		return this.id;
	}

	// get user-defined capacity
	public int getCapacity() {
		return this.capacity;
	}

	// get all records of this block
	public ArrayList<String> getRecords() {
		return this.records;
	}
}
