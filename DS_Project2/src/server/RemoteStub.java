package server;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

import connection.ConnectionManagement;

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
//	
//	public Object invoke(Method method, Object[] parameters) {
//		if (reference != null) {
//			return reference.invoke(this, method, parameters);
//		}
//		System.out.println("Reference is NULL!!!");
//		return null;
//	}
//	
	public Object invoke(RemoteObjectRef ref, Method method, Object[] parameters) throws IOException, ClassNotFoundException {
		//ConnectionManagement cm = new ConnectionManagement(ref.ip_adr, ref.port);
		RMIMessage message = new RMIMessage(ref.ip_adr, ref.port); 
		//message.setRef(ref);
		//message.setMethod(method);
		//message.setParameters(parameters);
		message.sendOut(ref, method.getName(), method.getParameterTypes(), parameters);
		
		//message.outStream.writeObject(ref);
        //for(int i = 0; i < parameters.length; i++){
        	//message.marshalling(types[i], parameters[i], message.outStream);
        //}
        //message.outStream.flush();
        
        return message.getResultValue(method.getReturnType());  
	}
}
