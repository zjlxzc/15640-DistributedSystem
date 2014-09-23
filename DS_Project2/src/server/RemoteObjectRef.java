package server;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RemoteObjectRef implements Serializable{

	private static final long serialVersionUID = 1L;
	public String ip_adr; // server ip address
	public int port; // server port 
	public int obj_key;
	public String remote_Interface_Name;

	public RemoteObjectRef(String ip, int port, int obj_key, String riname) {
		this.ip_adr = ip;
		this.port = port;
		this.obj_key = obj_key;
		this.remote_Interface_Name = riname;
	}

	// this method is important, since it is a stub creator.
	//
	public Object localise() {

		try {
			Class<?> c = Class.forName(remote_Interface_Name + "_Stub");
			Object o = null;
			try {
				Constructor constructor =
				        c.getConstructor(new Class[]{RemoteObjectRef.class});
				try {
					o = constructor.newInstance(this);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
