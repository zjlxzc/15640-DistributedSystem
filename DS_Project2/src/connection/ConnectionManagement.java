package connection;

import java.io.IOException;
import java.net.UnknownHostException;

// This class is used to connect to server to get the return value
public class ConnectionManagement extends RemoteConnection{

	public ConnectionManagement() {
	}
	
	public ConnectionManagement(String ipAddr, int port) throws UnknownHostException, IOException {
		super(ipAddr, port);
	}
}
