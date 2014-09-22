package server;

public class RemoteObjectRef {
	public String ip_adr; // server ip address
	public int port; // server port 
	public int obj_key;
	public String remote_Interface_Name;

	public RemoteObjectRef(String ip, int port, int obj_key, String riname) {
		ip_adr = ip;
		port = port;
		obj_key = obj_key;
		remote_Interface_Name = riname;
	}

	// this method is important, since it is a stub creator.
	//
	Object localise() {

		try {
			Class<?> c = Class.forName(remote_Interface_Name + "_stub");
			Object o = c.newInstance();
			return o;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
