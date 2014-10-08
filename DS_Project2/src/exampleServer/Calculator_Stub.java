/**
 * File name: Calculator_Stub.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a stub of calculator.
 */

package exampleServer;

import java.io.IOException;
import java.lang.reflect.Method;

import remote.Remote;
import remote.RemoteObjectRef;
import server.RemoteStub;
import exampleCalculationClient.Calculator;

public class Calculator_Stub extends RemoteStub implements Calculator, Remote {
	private static final long serialVersionUID = 2L;
	
	// define methods
	private static Method add;
	private static Method minus;
	private static Method multiply;
	private static Method divide;
	
	// create CalculatorServant instance
	private CalculatorServant calServant = new CalculatorServant();

	RemoteObjectRef paramRemoteRef;
	
	public Calculator_Stub() {
	}
	
	public Calculator_Stub(RemoteObjectRef paramRemoteRef) {
		this.paramRemoteRef = paramRemoteRef; // assign remote object reference
	}
	
	public int add(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on ADD");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2]; // there are two parameters
        cArg[0] = int.class; // both parameters are integer
        cArg[1] = int.class;
        
		try {
			add = c.getMethod("add", cArg); // get add method from the class
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			localObject = invoke(paramRemoteRef, add, new Object[] { new Integer(paramInt1),
							new Integer(paramInt2) }); // call the method in RemoteStub
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue(); // get return value
	}
	
	public int minus(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on MINUS");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2]; // there are two parameters
        cArg[0] = int.class; // both parameters are integer
        cArg[1] = int.class;
		try {
			minus = c.getMethod("minus", cArg); // get method minus from the class
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, minus, new Object[] { new Integer(paramInt1),
							new Integer(paramInt2) }); // call the method in RemoteStub
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue(); // get return value
	}
	
	public int multiply(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on MULTIPLY");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2]; // there are two parameters
        cArg[0] = int.class; // both parameters are integer
        cArg[1] = int.class;
		try {
			multiply = c.getMethod("multiply", cArg); // get method multiply from the class
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, multiply, new Object[] { new Integer(paramInt1),
							new Integer(paramInt2) }); // call the method in RemoteStub
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue(); // get return value
	}
	
	public int divide(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on DIVIDE");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2]; // there are two parameters
        cArg[0] = int.class; // both parameters are integer
        cArg[1] = int.class;
		try {
			divide = c.getMethod("divide", cArg); // get method divide from the class
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, divide, new Object[] { new Integer(paramInt1),
							new Integer(paramInt2) }); // call the method in RemoteStub
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ((Integer) localObject).intValue(); // get return value
	}
}
