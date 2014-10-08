/**
 * File name: TextSraper_Stub.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a stub of textScraper.
 */

package exampleServer;

import java.io.IOException;
import java.lang.reflect.Method;

import remote.Remote;
import remote.RemoteObjectRef;
import server.RemoteStub;
import exampleTextScraperClient.TextScraper;

public class TextScraper_Stub extends RemoteStub implements TextScraper, Remote {

	private static final long serialVersionUID = -8090518904775512975L;
	private static Method query; // define method
	// create TextScraperServant instance
	private TextScraperServant scraperServant = new TextScraperServant();

	RemoteObjectRef paramRemoteRef;
	
	public TextScraper_Stub() {
	}
	
	public TextScraper_Stub(RemoteObjectRef paramRemoteRef) {
		this.paramRemoteRef = paramRemoteRef; // assign remote object reference
	}
	
	public int query(String param) {
		System.out.println(this.getClass().getName() + " : get method call on QUERY");
		
		Class<? extends TextScraperServant> c = scraperServant.getClass();
		Class[] cArg = new Class[1]; // there is one parameters
        cArg[0] = String.class;
        
		try {
			query = c.getMethod("query", cArg); // get query method from the class
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			// call the method in RemoteStub
			localObject = invoke(paramRemoteRef, query, new Object[] {new String("\"" + param + "\"")});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue(); // get return value
	}
}
