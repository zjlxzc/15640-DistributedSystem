package exampleCalculationClient;

import registry.LocateRegistry;
import registry.SimpleRegistry;
import remote.RemoteObjectRef;
import exception.AccessException;
import exception.NotBoundException;
import exception.RemoteException;

public class CalculatorClient {

	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];
		int arg1 = 0;
		int arg2 = 0;
		
		try {
			arg1 = Integer.parseInt(args[3]);
			arg2 = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
	    	System.out.println("The last two arguments should be integers.");
	    }
		
		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port);
		System.out.println("Client     : Get Regsitry");
		RemoteObjectRef ror = null;
		try {
			ror = sr.lookup(serviceName);
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		} catch (NotBoundException e) {
			System.out.println(e.getStackTrace());
		} catch (AccessException e) {
			System.out.println(e.getStackTrace());
		}
		System.out.println("Client     : Get the remote object reference of \"" + serviceName + "\"");

		Calculator cal = (Calculator) ror.localise();
		
		System.out.println("Client     : call method arguments: (" + arg1 + ", " + arg2 + ")");
		System.out.println();
		
		try {
			System.out.println("Client     : get the result of add: " + cal.add(arg1, arg2) + "\n");
			System.out.println("Client     : get the result of minus: " + cal.minus(arg1, arg2) + "\n");
			System.out.println("Client     : get the result of multiply: " + cal.multiply(arg1, arg2) + "\n");
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		}
		
		try {
			System.out.println("Client     : get the result divide: " + cal.divide(arg1, arg2) + "\n");
		} catch (ArithmeticException e) {
			System.out.println(e.getStackTrace());
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		}
	}
}