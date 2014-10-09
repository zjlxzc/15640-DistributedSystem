/**
 * File name: TextScraperClient.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a client to test RMI.
 * This function of this class is to get the number of products of a web site given a product name.
 */

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
		if (args.length != 4) { // validate input
			System.out.println("Invalid input. Please use IP-address 1099 textScraper \"baby strol\".");
			System.exit(1);
		}
		
		String host = args[0]; // get host
		int port = Integer.parseInt(args[1]); // get port
		String serviceName = args[2]; // get service name
		
		String keyword = args[3]; // get product name
		
		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port); // get registry
		System.out.println("Client     : Get Regsitry");
		
		RemoteObjectRef ror = null;
		try {
			ror = sr.lookup(serviceName); // get remote object reference
		} catch (NotBoundException e) {
			System.out.println(e.getStackTrace());
		} 
		System.out.println("Client     : Get the remote object reference of \"" + serviceName + "\"");

		TextScraper scraper = (TextScraper) ror.localise(); // generate stub
		
		System.out.println("Client     : call method arguments: (" + keyword + ")");
		System.out.println();
		
		try {
			System.out.println("Client     : get the result of add: " + scraper.query(keyword) + "\n");
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		}
	}
}





