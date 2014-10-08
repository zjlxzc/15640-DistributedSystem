package textScraper;

import java.io.IOException;

import registry.LocateRegistry;
import registry.SimpleRegistry;
import remote.RemoteObjectRef;
import exampleClient.Calculator;
import exception.AccessException;
import exception.NotBoundException;

public class TextScraperClient {
	
	public TextScraperClient() {
	}
	
	public static void main(String[] args) throws IOException, NotBoundException, AccessException {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];
		String keyword = args[3];
		
		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port);
		System.out.println("Client     : Get Regsitry");
		RemoteObjectRef ror = sr.lookup(serviceName);
		System.out.println("Client     : Get the remote object reference of \"" + serviceName + "\"");

		TextScraper scraper = (TextScraper) ror.localise();
		
		System.out.println("Client     : call method arguments: (" + keyword + ")");
		System.out.println();
		
		System.out.println("Client     : get the result of add: " + scraper.query(keyword) + "\n");
	}
}





