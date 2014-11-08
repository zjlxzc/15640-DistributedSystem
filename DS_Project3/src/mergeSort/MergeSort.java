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
		ArrayList<SingleRecord> input = new ArrayList<SingleRecord>();
		String line = "";
		SingleRecord sr = null;
		
		while ((line = bufferedReader.readLine()) != null) { // read file line by line
			String[] pair = line.split("\t");
			sr = new SingleRecord(pair[0], pair[1]);
			input.add(sr);
		}
		Arrays.sort(input.toArray()); // sort input file by key
		
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile)); // write to output file
		for (SingleRecord srecord : input) {
			bufferedWriter.write(srecord.toString()); // write to output file
			bufferedWriter.newLine(); // write a line separator
		}
		
		bufferedReader.close();
		bufferedWriter.close();
	}
}
