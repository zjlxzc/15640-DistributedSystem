package exampleTextScraperClient;

import exception.AccessException;
import exception.NotBoundException;
import exception.RemoteException;
import registry.LocateRegistry;
import registry.SimpleRegistry;
import remote.RemoteObjectRef;

public class TextScraperClient {
	
	public TextScraperClient() {
	}
	
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Invalid input. Please use IP-address 1099 textScraper \"baby strol\".");
			System.exit(1);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];
		
		String keyword = args[3];
		
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

		TextScraper scraper = (TextScraper) ror.localise();
		
		System.out.println("Client     : call method arguments: (" + keyword + ")");
		System.out.println();
		
		try {
			System.out.println("Client     : get the result of add: " + scraper.query(keyword) + "\n");
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		}
	}
}





