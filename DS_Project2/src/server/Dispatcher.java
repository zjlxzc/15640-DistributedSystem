/**
 * File name: Dispatcher.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a single object which can unmarshalling any method invocation
 */

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
	
	// initialize variables
	public Dispatcher (RORtbl table, ObjectInputStream in, ObjectOutputStream out) {
		this.out = out;
		this.in = in;
		this.table = table;
	}
	
	public void localize() {		
		try {
			message = (RMIMessage)in.readObject(); // get RMIMessage object
			System.out.println("Dispatcher	: read in RMIMessage");
			
			RemoteObjectRef ror = message.getRef(); // get remote object reference
			System.out.println("Dispatcher	: get the Remote Object Reference of " + ror.getRemote_Interface_Name());
				
			Object obj = table.findObj(ror); // get object
			Class<?> c = obj.getClass();
			System.out.println("Dispatcher	: get the local Object of " + c.getName());
	
			Method method = null;
			method = c.getDeclaredMethod(message.getMethodName(), message.getTypes()); // get method
			System.out.println("Dispatcher	: get the method name:  " + method.getName());	
			
			Object[] args = message.getParameters(); // get method parameters
			System.out.println("Dispatcher	: get the parameters of " + method.getName());
			
			System.out.println("Dispatcher	: call the method and get the result");
			Object ret = method.invoke(obj,args); // call method to get result
			message.setResult(ret); // set result to RMIMessage
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void dispatch() {
		try {
			out.writeObject(message); // send out RMIMessage
			out.flush();
			System.out.println("Dispatcher	: send back the message");
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
}
