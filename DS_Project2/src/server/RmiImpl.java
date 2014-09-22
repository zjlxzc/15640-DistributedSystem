package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import registry.RORtbl;

public class RmiImpl {

	static String host;
	static int port;

	public static void main(String args[]) throws Exception {
		String InitialClassName = args[0];
		String registryHost = args[1];
		int registryPort = Integer.parseInt(args[2]);
		String serviceName = args[3];

		// it should have its own port. assume you hardwire it.
		host = (InetAddress.getLocalHost()).getHostName();
		port = 1099;

		// it now have two classes from MainClassName:
		// (1) the class itself (say ZipCpdeServerImpl) and
		// (2) its skeleton.
		Class initialclass = Class.forName(InitialClassName);
		Class initialskeleton = Class.forName(InitialClassName + "_skel");

		// you should also create a remote object table here.
		// it is a table of a ROR and a skeleton.
		// as a hint, I give such a table's interface as RORtbl.java.
		RORtbl tbl = new RORtbl();

		// after that, you create one remote object of initialclass.
		Object o = initialclass.newInstance();

		// then register it into the table.
		tbl.addObj(host, port, o);

		// create a socket.
		ServerSocket serverSoc = new ServerSocket(port);

		// Now we go into a loop.
		// Look at rmiregistry.java for a simple server programming.
		// The code is far from optimal but in any way you can get basics.
		// Actually you should use multiple threads, or this easily
		// deadlocks. But for your implementation I do not ask it.
		// For design, consider well.
		while (true) {
			// (1) receives an invocation request.
			// (2) creates a socket and input/output streams.
			// (3) gets the invocation, in martiallled form.
			// (4) gets the real object reference from tbl.
			// (5) Either:
			// -- using the interface name, asks the skeleton,
			// together with the object reference, to unmartial
			// and invoke the real object.
			// -- or do unmarshalling directly and involkes that
			// object directly.
			// (6) receives the return value, which (if not marshalled
			// you should marshal it here) and send it out to the
			// the source of the invoker.
			// (7) closes the socket.
			
			Socket client = serverSoc.accept();
			Excution excute = new Excution(client);
			new Thread(excute).start();		
		}
	}
	
	private static class Excution implements Runnable {
		Socket client;
		
		public Excution(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
		}		
	}
}