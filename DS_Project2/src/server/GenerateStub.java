package server;

import java.lang.reflect.Method;

import exception.AccessException;
import exception.AlreadyBoundException;
import exception.NotBoundException;
import exception.RemoteException;
import registry.Registry;
import registry.Remote;

public class GenerateStub extends RemoteStub implements Registry{

	private static final long serialVersionUID = 8113690227477647784L;
	
	private Method bind;
	private Method lookup;

	public GenerateStub() throws SecurityException, NoSuchMethodException {
		bind = Registry.class.getMethod("bind", new Class[]{String.class, RemoteObjectRef.class});
		lookup = Registry.class.getMethod("lookup", new Class[]{String.class, RemoteObjectRef.class});
	}

	public GenerateStub(server.RemoteObjectRef reference) {
		super(reference);
	}
	
	@Override
	public void bind(String serviceName, RemoteObjectRef obj)
			throws RemoteException, AlreadyBoundException, AccessException {
		execute(bind, new Object[]{serviceName, obj});
		
	}

	@Override
	public Remote lookup(String name) throws RemoteException,
			NotBoundException, AccessException {
		
		return (Remote)execute(bind, new Object[]{name});
	}
}
