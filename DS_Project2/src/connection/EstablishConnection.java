package connection;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import server.RemoteObjectRef;

public class EstablishConnection extends RemoteConnection{

	public EstablishConnection() {
	}
	
	public EstablishConnection(String ipAddr, int port) throws UnknownHostException, IOException {
		super(ipAddr, port);
	}
	
	public Object connect(RemoteObjectRef reference, Method m, Object[] args) throws IOException {
		Class<?>[] clas = m.getParameterTypes();
		outStream.writeObject(reference);
		
		
	}
}
