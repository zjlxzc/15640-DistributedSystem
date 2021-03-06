/**
 * File name: RemoteObjectRef.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is used to name objects that live within one JVM from another JVM.
 */

package remote;

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
	public Object localise() {
		try {
			Class<?> c = Class.forName(remote_Interface_Name + "_Stub"); // get class
			Object o = null;
			Constructor<?> constructor =
				        c.getConstructor(new Class[]{RemoteObjectRef.class}); // get constructor
			o = constructor.newInstance(this); // new instance
			System.out.println("Remote Object Reference : generate \"" + o.getClass().getName() + "\"");

			return o;
		} catch (ClassNotFoundException e) { // possible exceptions
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	// getters and setters
	public String getIp_adr() {
		return ip_adr;
	}

	public void setIp_adr(String ip_adr) {
		this.ip_adr = ip_adr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getObj_key() {
		return obj_key;
	}

	public void setObj_key(int obj_key) {
		this.obj_key = obj_key;
	}

	public String getRemote_Interface_Name() {
		return remote_Interface_Name;
	}

	public void setRemote_Interface_Name(String remote_Interface_Name) {
		this.remote_Interface_Name = remote_Interface_Name;
	}
}
