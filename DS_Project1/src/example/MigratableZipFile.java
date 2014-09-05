package example;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import transactionalIO.TransactionalFileInputStream;
import transactionalIO.TransactionalFileOutputStream;
import migratableProcess.MigratableProcess;

/*
 * This class is a test example of Migratable Process.
 * This function of this class is to zip an input file and generate a zipped output file.
 */
public class MigratableZipFile implements MigratableProcess {

	// this is generated serial version ID
	private static final long serialVersionUID = -5855854688202918287L;
	private TransactionalFileInputStream inputFile;
	private TransactionalFileOutputStream outputFile;

	// "volatile" is used to indicate that this variable's value will be modified by different threads
	private volatile boolean isSuspending; // this variable is a signal of suspending

	// this variable is used to store the original set of arguments with which this class is called
	private String[] args;

	public MigratableZipFile() {
	}

	public MigratableZipFile(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("usage: MigratableZipFile <inputFile>");
			throw new Exception("Invalid Arguments.");
		}

		File input = new File(args[0]);
		if (!input.isFile()) {
			System.out.println("\"" + args[0] + "\" is not a valid file.");
			throw new Exception("Invalid Input File.");
		}

		inputFile = new TransactionalFileInputStream(args[0]);
		outputFile = new TransactionalFileOutputStream(args[0] + ".gz", false);
		this.args = args;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		DataInputStream in = new DataInputStream(inputFile);

		try {
			GZIPOutputStream out = new GZIPOutputStream(outputFile);

			while (!isSuspending) {
				int readByte = in.read(buffer);
				if (readByte == -1) { // if reaching the end of the stream
					break;
				}

				out.write(buffer, 0, readByte);
				// out.flush();
				Thread.sleep(100); // Make it take longer so that we don't require extremely
									// large files for interesting results
			}

			in.close();
			out.finish();
			out.close();
			System.out.println("MigratableZipFile End of zipping.");
		} catch (IOException e) {
			System.out.println("MigratableZipFile IOException: " + e);
		} catch (InterruptedException e) {
			System.out.println("MigratableZipFile InterruptedException: " + e);
		}

		isSuspending = false;
	}

	@Override
	public void suspend() {
		isSuspending = true;
		while (isSuspending)
			;
	}

	/*
	 * This method is used to produce a simple string representation of the
	 * object. It is used to print the class name of the process and the
	 * arguments. This would make the debugging and tracing relative easy.
	 */
	public String toString() {
		StringBuilder sbuilder = new StringBuilder("MigratableZipFile");
		for (String arg : args) {
			sbuilder.append(" " + arg);
		}

		return sbuilder.toString();
	}

	public static void main(String[] args) {
		String[] testArgs = { "testIn.txt", "testOut.txt" };
		try {
			MigratableZipFile zipFile = new MigratableZipFile(testArgs);
			Thread thread = new Thread(zipFile);
			thread.start();

			Thread.sleep(1000);
			System.out.println("In main after first sleep.");
			
			zipFile.suspend();
			System.out.println("In main after suspend.");

			Thread.sleep(1000);
			System.out.println("In main after second sleep.");
			
			thread = new Thread(zipFile);
			thread.start();
		} catch (Exception e) {
			System.out.println("In main Exception" + e);
		}
	}
}
