package lab1;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable {

	// default serial version ID
	//private static final long serialVersionUID = 1L;
	
	// generated serial version ID
	private static final long serialVersionUID = -8577982678271040984L;
	private String fileName;
	
	public TransactionalFileInputStream () {
	}
	
	public TransactionalFileInputStream(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public int read() throws IOException {
		
		return 0;
	}

}
