package server;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

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
		
		System.out.println("Remote Stub: method == null:" + method == null);
		RMIMessage message = new RMIMessage(ref, method, parameters);    
		
		
		//message.outStream.writeObject(ref);
        //for(int i = 0; i < parameters.length; i++){
        	//message.marshalling(types[i], parameters[i], message.outStream);
        //}
        //message.outStream.flush();
        
        return message.getReturnValue(method);  
	}
}
