package server;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

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
		
		System.out.println("RemoteStub : create new RMIMessage to communate");
		RMIMessage message = new RMIMessage(ref.ip_adr, ref.port); 

		message.sendOut(ref, method.getName(), method.getParameterTypes(), parameters);
		
        return message.getResultValue(method.getReturnType());  
	}
}
