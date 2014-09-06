/**
 * File name: TransactionalFileOutputStream.java
 * @author Jialing Zhou (jialingz), Chun Xu (chunx)
 * Course/Section: 15640/A
 * 
 * Description: Lab 1: Portable, Migratable Work
 * 
 * This class will be used to facilitate migrating processes with files operations.
 */

package transactionalIO;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

	// generated serial version ID
	private static final long serialVersionUID = 1689452747729402165L;
	private String fileName;
	private long offset;
	
	public TransactionalFileOutputStream() {
	}
	
	public TransactionalFileOutputStream(String fileName, boolean isAppend) {
		this.fileName = fileName;
		offset = isAppend ? new File(fileName).length() : 0L;
	}

	@Override
	public void write(int writeByte) throws IOException {
		// instance of RandomAccessFile support both reading and writing to a random access file
		RandomAccessFile raf = new RandomAccessFile(fileName, "rws");
		raf.seek(offset++);
		raf.write(writeByte);
		raf.close();
	}
}
