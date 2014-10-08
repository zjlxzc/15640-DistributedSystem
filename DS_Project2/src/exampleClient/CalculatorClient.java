package exampleClient;

import java.io.IOException;

import registry.LocateRegistry;
import registry.SimpleRegistry;
import remote.RemoteObjectRef;
import exception.AccessException;
import exception.NotBoundException;

public class CalculatorClient {

	public static void main(String[] args) throws IOException, NotBoundException, AccessException {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];
		int arg1 = Integer.parseInt(args[3]);
		int arg2 = Integer.parseInt(args[4]);
		
		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port);
		System.out.println("Client     : Get Regsitry");
		RemoteObjectRef ror = sr.lookup(serviceName);
		System.out.println("Client     : Get the remote object reference of \"" + serviceName + "\"");

		Calculator cal = (Calculator) ror.localise();
		
		System.out.println("Client     : call method arguments: (" + arg1 + ", " + arg2 + ")");
		System.out.println();
		
		System.out.println("Client     : get the result of add: " + cal.add(arg1, arg2) + "\n");
		System.out.println("Client     : get the result of minus: " + cal.minus(arg1, arg2) + "\n");
		System.out.println("Client     : get the result of multiply: " + cal.multiply(arg1, arg2) + "\n");
		System.out.println("Client     : get the result divide: " + cal.divide(arg1, arg2) + "\n");
		System.out.println();
	}
}