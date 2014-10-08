package exampleServer;

import java.io.IOException;
import java.lang.reflect.Method;

import remote.Remote;
import remote.RemoteObjectRef;
import server.RemoteStub;
import exampleClient.Calculator;

public class Calculator_Stub extends RemoteStub implements Calculator, Remote {
	private static final long serialVersionUID = 2L;
	private static Method add;
	private static Method minus;
	private static Method multiply;
	private static Method divide;
	private CalculatorServant calServant = new CalculatorServant();

	RemoteObjectRef paramRemoteRef;
	
	public Calculator_Stub() {
	}
	
	public Calculator_Stub(RemoteObjectRef paramRemoteRef) {
		this.paramRemoteRef = paramRemoteRef;
	}
	
	public int add(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on ADD");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2];
        cArg[0] = int.class;
        cArg[1] = int.class;
		try {
			add = c.getMethod("add", cArg);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
	
	public int minus(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on MINUS");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2];
        cArg[0] = int.class;
        cArg[1] = int.class;
		try {
			minus = c.getMethod("minus", cArg);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, minus, new Object[] { new Integer(paramInt1),
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
	
	public int multiply(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on MULTIPLY");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2];
        cArg[0] = int.class;
        cArg[1] = int.class;
		try {
			multiply = c.getMethod("multiply", cArg);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, multiply, new Object[] { new Integer(paramInt1),
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
	
	public int divide(int paramInt1, int paramInt2) {
		System.out.println(this.getClass().getName() + " : get method call on DIVIDE");
		
		Class<? extends CalculatorServant> c = calServant.getClass();
		Class[] cArg = new Class[2];
        cArg[0] = int.class;
        cArg[1] = int.class;
		try {
			divide = c.getMethod("divide", cArg);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Object localObject = null;
		try {
			
			localObject = invoke(paramRemoteRef, divide, new Object[] { new Integer(paramInt1),
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
