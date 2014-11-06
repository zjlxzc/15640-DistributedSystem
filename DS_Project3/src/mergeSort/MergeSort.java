package mergeSort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

// This class is used to do merge sort after Map phase
public class MergeSort {

	/**
	 * @param files - a set of files to be sorted
	 * @param outputFile - this file is used to store the sorted result
	 * @throws IOException - possible exception
	 */
	public static void mergeSortFiles(ArrayList<String> files, String outputFile)
			throws IOException {
		// each file will have a BufferedReader
		ArrayList<BufferedReader> bufferedReader = new ArrayList<BufferedReader>();
		
		// We use PriorityQueue to do sort on the key
		PriorityQueue<ResultPair<String, BufferedReader>> queue =
			new PriorityQueue<ResultPair<String, BufferedReader>>(30, new Comparator<ResultPair<String, BufferedReader>>() {
				public int compare(ResultPair<String, BufferedReader> rp1,
						ResultPair<String, BufferedReader> rp2) {
					return rp1.getKey().compareTo(rp2.getKey());
				}
			});

		for (int index = 0; index < files.size(); index++) {
			BufferedReader reader = new BufferedReader(new FileReader(
					files.get(index))); // read one file
			String line = reader.readLine(); // read one line from that file
			if (line != null) {
				ResultPair<String, BufferedReader> newPair = new ResultPair<String, BufferedReader>(
						line, reader); // create an new object with file-reader pair
				queue.add(newPair); // add new pair to queue
				bufferedReader.add(reader); // add reader to the list
			} else {
				reader.close();
			}
		}

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				outputFile)); // write to output file
		
		while (queue.size() > 0) {
			ResultPair<String, BufferedReader> pair = queue.poll();
			bufferedWriter.write(pair.getKey());
			bufferedWriter.newLine(); // write a line separator

			if (bufferedReader.contains(pair.getValue())) {
				String newLine = pair.getValue().readLine(); // read a new line of the same file
				if (newLine != null) {
					ResultPair<String, BufferedReader> newPair = new ResultPair<String, BufferedReader>(
							newLine, pair.getValue());
					queue.add(newPair);
				} else {
					pair.getValue().close(); // close reader
					bufferedReader.remove(pair.getValue()); // remove reader from the list
				}
			}
		}

		bufferedWriter.flush();
		bufferedWriter.close();
	}
}
