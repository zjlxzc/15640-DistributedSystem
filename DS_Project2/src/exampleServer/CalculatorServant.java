package exampleServer;

import exception.RemoteException;

// CalculatorServant.java        
// A Remote object class that implements Calculator. 
public class CalculatorServant implements Calculator {
	public CalculatorServant() {
	}

	public int add(int x, int y) {
		System.out.println("CalculatorServant : Got request to add " + x
				+ " and " + y);
		return x + y;
	}

	@Override
	public int minus(int x, int y) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("CalculatorServant : Got request to minus " + x
				+ " and " + y);
		return x - y;
	}

	@Override
	public int mutiply(int x, int y) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("CalculatorServant : Got request to mutiply " + x
				+ " and " + y);
		return x * y;
	}

	@Override
	public int divide(int x, int y) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("CalculatorServant : Got request to devide " + x
				+ " and " + y);
		return x / y;
	}

}