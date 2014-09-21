package connection;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import server.RemoteObjectRef;

// This class is used to connect to server to get the return value
public class EstablishConnection extends RemoteConnection{

	public EstablishConnection() {
	}
	
	public EstablishConnection(String ipAddr, int port) throws UnknownHostException, IOException {
		super(ipAddr, port);
	}

	public Object execute(RemoteObjectRef reference, Method m, Object[] args)
			throws IOException, ClassNotFoundException {
		Class<?>[] clas = m.getParameterTypes();
		outStream.writeObject(reference);
		for (int i = 0; i < args.length; i++) {
			marshalling(clas[i], args[i], outStream);
		}
		outStream.flush();
		
		return getReturnValue(m);
	}
	
	public Object getReturnValue(Method m) throws IOException, ClassNotFoundException {
		Class<?> returnType = m.getReturnType();
		if (returnType == void.class) {
			return null;
		}
		return unmarshalling(returnType, inStream);
		
	}
}
