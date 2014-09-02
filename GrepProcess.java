package lab1;

import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

public class GrepProcess implements MigratableProcess {

	private static final long serialVersionUID = -2076220948300508193L;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	private String query;

	private volatile boolean suspending;

	// this variable is used to store the original set of arguments with which
	// this class is called
	private String[] args;

	public GrepProcess(String args[]) throws Exception {
		if (args.length != 3) {
			System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}

		this.args = args;
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], false);
	}

	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {
				@SuppressWarnings("deprecation")
				String line = in.readLine();

				if (line == null)
					break;

				if (line.contains(query)) {
					out.println(line);
				}

				// Make grep take longer so that we don't require extremely
				// large files for interesting results
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			// End of File
		} catch (IOException e) {
			System.out.println("GrepProcess: Error: " + e);
		}

		suspending = false;
	}

	public void suspend() {
		suspending = true;
		while (suspending)
			;
	}

	/*
	 * It is strongly recommended to have a method that can produce a simple
	 * string representation of the object. We can use it to print the class
	 * name of the process and the arguments. This would make the debugging and
	 * tracing relative easy.
	 */
	public String toString() {
		StringBuilder sbuilder = new StringBuilder("Track");
		for (String arg : args) {
			sbuilder.append(arg + " ");
		}

		return sbuilder.toString();
	}
}