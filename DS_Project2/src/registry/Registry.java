package registry;

/*
 * Registry is a remote interface to a simple remote object registry
 * that provides methods for storing and retrieving remote object references
 * bound with arbitrary string names.
 */

public interface Registry extends Remote {

	public static final int REGISTRY_OBJID = 0x0; // object ID
	public static final int REGISTRY_TCP_PORT = 12345; // TCP port number

	public void bind(String name, Remote obj) throws RemoteException,
			AlreadyBoundException, AccessException;
	public Remote lookup(String name) throws RemoteException,
	NotBoundException, AccessException; // query the current name bindings
	
	/* We may do not need to use these methods.
	 * 
	public String[] list() throws RemoteException, AccessException;
	public void rebind(String name, Remote obj) throws RemoteException,
			AccessException;
	public void unbind(String name) throws RemoteException, NotBoundException,
			AccessException;
	*/
}
