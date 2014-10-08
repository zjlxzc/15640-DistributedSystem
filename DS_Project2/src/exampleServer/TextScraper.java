package exampleServer;

import remote.Remote;
import exception.RemoteException;

public interface TextScraper extends Remote {
	// this method will be called from remote clients
	int query (String param) throws RemoteException;
}