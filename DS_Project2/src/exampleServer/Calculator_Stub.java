package exampleServer;

import java.io.IOException;
import java.lang.reflect.Method;

import registry.Remote;
import server.RemoteObjectRef;
import server.RemoteStub;
import exampleClient.Calculator;

public class Calculator_Stub extends RemoteStub implements Calculator, Remote {
	private static final long serialVersionUID = 2L;
	private static Method add;

	RemoteObjectRef paramRemoteRef;
	
	public Calculator_Stub() {	
	}
	
	public Calculator_Stub(RemoteObjectRef paramRemoteRef) {
		this.paramRemoteRef = paramRemoteRef;
	}
	
	public int add(int paramInt1, int paramInt2) {
		Object localObject = null;
		try {
			localObject = invoke(paramRemoteRef, add, new Object[] { new Integer(paramInt1),
							new Integer(paramInt2) });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue();
	}
}
