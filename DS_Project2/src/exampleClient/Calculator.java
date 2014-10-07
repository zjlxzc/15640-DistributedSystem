package exampleClient;

import exception.RemoteException;
import remote.Remote;

public interface Calculator extends Remote {
   // this method will be called from remote clients
   int add (int x, int y) throws RemoteException;
   int minus (int x, int y) throws RemoteException;
   int multiply (int x, int y) throws RemoteException;
   int divide (int x, int y) throws RemoteException;
}