/**
 * File name: TransactionalFileInputStream.java
 * @author Jialing Zhou (jialingz), Chun Xu (chunx)
 * Course/Section: 15640/A
 * 
 * Description: Lab 1: Portable, Migratable Work
 * 
 * This class will be used to facilitate migrating processes with files operations.
 */

package transactionalIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable {
	
	// generated serial version ID
	private static final long serialVersionUID = -8577982678271040984L;
	private String fileName;
	private long offset;
	
	public TransactionalFileInputStream () {
	}
	
	public TransactionalFileInputStream(String fileName) {
		this.fileName = fileName;
		offset = 0L;
	}

	@Override
	public int read() throws IOException {
		// instance of RandomAccessFile support both reading and writing to a random access file
		RandomAccessFile raf = new RandomAccessFile(fileName, "rws");
		
		// sets the file-pointer offset, measured from the beginning of this file,
		// at which the next read or write occurs
		raf.seek(offset);
		
		// reads a byte of data from this file
		int nextByte = raf.read();
		if (nextByte != -1) { // -1 means the end of the file has been reached
			offset++;
		}
		
		raf.close();
		return nextByte;
	}
}
