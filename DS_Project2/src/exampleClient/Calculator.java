package exampleClient;

import exception.RemoteException;
import registry.Remote;

public interface Calculator extends Remote {
   // this method will be called from remote clients
   int add (int x, int y) throws RemoteException;
}