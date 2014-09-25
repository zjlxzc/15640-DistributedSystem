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

		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port);
		System.out.println("Client : Get Regsitry");
		RemoteObjectRef ror = sr.lookup(serviceName);
		System.out.println("Client : Get the remote object reference of " + serviceName);

		Calculator cal = (Calculator) ror.localise();
		
		System.out.println("Client : call method add(3, 4)");
		
		System.out.println("Client : get the result : " + cal.add(3 ,4));
	}
}