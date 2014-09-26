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

	public static final int port = 2014;
	public static final String postfix = "_Stub";
	public static final String uri = "/stubs/";
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
		
		RMIMessage message = new RMIMessage(ref, method.getName(), method.getParameterTypes(), parameters); 
		
		outStream.writeObject(message);
		outStream.flush();
		
		RMIMessage result = (RMIMessage)(inStream.readObject());
        return result.getResultValue(method.getReturnType(), result);  
	}
}
