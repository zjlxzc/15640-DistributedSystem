package exampleClient;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;

import registry.SimpleRegistry;
import server.RemoteObjectRef;
import exception.AccessException;
import exception.NotBoundException;

public class CalculatorClient {

	public static void main(String[] args) throws IOException, NotBoundException, AccessException {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];

		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port);
		RemoteObjectRef ror = sr.lookup(serviceName);

		Calculator cal = (Calculator) ror.localise();

		System.out.println(cal.add(3, 4));
	}
}