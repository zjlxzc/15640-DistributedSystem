package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import remote.RORtbl;
import remote.RemoteObjectRef;

public class Dispatcher{
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private RORtbl table;
	RMIMessage message;
	
	public Dispatcher () {	}
	
	public Dispatcher (RORtbl table, ObjectInputStream in, ObjectOutputStream out) {
		this.out = out;
		this.in = in;
		this.table = table;
	}
	
	public void localize() {		
		try {
			message = (RMIMessage)in.readObject();		
			System.out.println("Dispatcher: read in RMIMessage");
			
			RemoteObjectRef ror = message.getRef();
			System.out.println("Dispatcher: get the Remote Object Reference of " + ror.getRemote_Interface_Name());
			
			Object obj = table.findObj(ror);
			Class<?> c = obj.getClass();
			System.out.println("Dispatcher: get the local Object of " + c.getName());
	
			Method method = null;
			method = c.getDeclaredMethod(message.getMethodName(), message.getTypes());
			System.out.println("Dispatcher: get the method name:  " + method.getName());	
			
			Object[] args = message.getParameters();
			System.out.println("Dispatcher: get the parameters of " + method.getName());
			
			System.out.println("Dispatcher: call the method and get the result");
			Object ret = method.invoke(obj,args);
		message.setResult(ret);		
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dispatch() {
		try {
			out.writeObject(message);
			out.flush();
			System.out.println("Dispatcher: send back the message");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}
