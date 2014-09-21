package server;

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
		reference = referenc;
	}
	
	/**
	 * @param stub - the remote stub
	 * @param ref - the remote reference
	 */
	protected void setRef(RemoteStub stub, RemoteObjectRef ref) {
		reference = ref;
	}
	
	public Object execute(RemoteObjectRef reference, Method method, Object[] args) {
		return reference.execute(reference, method, args);
	}
}
