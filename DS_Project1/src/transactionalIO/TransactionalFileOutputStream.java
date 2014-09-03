package transactionalIO;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

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
		RandomAccessFile raf = new RandomAccessFile(fileName, "rws");
		raf.seek(offset++);
		raf.write(writeByte);
		raf.close();
	}

}
