/**
 * File name: RemoteStub.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is serializable.
 */

package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Socket;

import remote.RemoteObjectRef;

public class RemoteStub implements Serializable{

	private static final long serialVersionUID = -7166353378723846018L;
	public RemoteObjectRef reference;
	
	// Constructs a RemoteStub.
	protected RemoteStub() {
		reference = null;
	}
	
	// Constructs a RemoteStub, with the specified remote reference.
	protected RemoteStub(RemoteObjectRef referenc) {
		setRef(referenc);
	}
	
	/**
	 * @param stub - the remote stub
	 * @param ref - the remote reference
	 */
	protected void setRef(RemoteStub stub, RemoteObjectRef referenc) {
		reference = referenc;
	}
	
	protected void setRef(RemoteObjectRef referenc) {
		reference = referenc;
	}
	
	public Object invoke(RemoteObjectRef ref, Method method, Object[] parameters) throws IOException, ClassNotFoundException {
		
		System.out.println("RemoteStub : create new RMIMessage to communicate");
		
		Socket socket = new Socket(ref.ip_adr, ref.port);
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
		
		// create RMIMessage instance
		RMIMessage message = new RMIMessage(ref, method.getName(), method.getParameterTypes(), parameters); 
		
		outStream.writeObject(message); // send out RMIMessage
		outStream.flush();
		
		RMIMessage result = (RMIMessage)(inStream.readObject()); // get returned RMIMessage
        return result.getResultValue(method.getReturnType(), result); // return value
	}
}
