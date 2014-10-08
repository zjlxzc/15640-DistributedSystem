package exampleServer;

import java.io.IOException;
import java.lang.reflect.Method;

import remote.Remote;
import remote.RemoteObjectRef;
import server.RemoteStub;
import exampleTextScraperClient.TextScraper;

public class TextScraper_Stub extends RemoteStub implements TextScraper, Remote {

	private static final long serialVersionUID = -8090518904775512975L;
	private static Method query;
	private TextScraperServant scraperServant = new TextScraperServant();

	RemoteObjectRef paramRemoteRef;
	
	public TextScraper_Stub() {
	}
	
	public TextScraper_Stub(RemoteObjectRef paramRemoteRef) {
		this.paramRemoteRef = paramRemoteRef;
	}
	
	public int query(String param) {
		System.out.println(this.getClass().getName() + " : get method call on QUERY");
		
		Class<? extends TextScraperServant> c = scraperServant.getClass();
		Class[] cArg = new Class[1];
        cArg[0] = String.class;
		try {
			query = c.getMethod("query", cArg);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, query, new Object[] {new String("\"" + param + "\"")});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue();
	}
}
