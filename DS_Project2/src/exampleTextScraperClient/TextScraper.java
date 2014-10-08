/**
 * File name: TextScraper.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This is an interface of the example - "textScraper".
 * This function of it is to provide method(s) that
 * need(s) to be implemented by other classes that implement the interface.
 */

package exampleTextScraperClient;

import remote.Remote;
import exception.RemoteException;

public interface TextScraper extends Remote {
	// this method will be called from remote clients
	int query (String param) throws RemoteException;
}