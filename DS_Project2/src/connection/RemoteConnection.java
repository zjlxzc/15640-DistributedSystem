package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RemoteConnection {

	public ObjectInputStream inStream;
	public ObjectOutputStream outStream;
	public Socket socket;
	
	public RemoteConnection() {
	}
	
	public RemoteConnection(String ipAddr, int port) throws UnknownHostException, IOException {
		socket = new Socket(ipAddr, port);
		outStream = new ObjectOutputStream(socket.getOutputStream());
		inStream = new ObjectInputStream(socket.getInputStream());
	}
	
	public void close() throws IOException {
		inStream.close();
		outStream.close();
		socket.close();
	}
}
