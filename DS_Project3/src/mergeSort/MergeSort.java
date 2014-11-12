package mergeSort;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to sort one file after Map phase.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MergeSort {
	
	public void mergeSortFiles(File inputFile, File outputFile)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
		// put key-value pair to an array list
		ArrayList<SingleRecord> input = new ArrayList<SingleRecord>();
		String line = "";
		SingleRecord sr = null;
		
		while ((line = bufferedReader.readLine()) != null) { // read file line by line
			String[] pair = line.split("\t"); // split a line to key-value pair
			sr = new SingleRecord(pair[0], pair[1]);
			input.add(sr); // add to array list
		}
		Arrays.sort(input.toArray()); // sort input file by key
		
		// write all sorted key-value pairs to an output file
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
		for (SingleRecord srecord : input) {
			bufferedWriter.write(srecord.toString()); // write to output file
			bufferedWriter.newLine(); // write a line separator
		}
		
		bufferedReader.close();
		bufferedWriter.close();
	}
}
