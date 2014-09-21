package exampleServer;

import exception.RemoteException;

public class CalculatorServant extends UnicastRemoteObject implements Calculator {
       public CalculatorServant() throws RemoteException {
       }
       public int add(int x, int y) throws RemoteException {
            System.out.println("Got request to add " + x + " and " + y);

            return x + y;
    }
}