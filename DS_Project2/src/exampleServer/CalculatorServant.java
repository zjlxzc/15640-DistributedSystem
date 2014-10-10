/**
 * File name: CalculatorServant.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This is class is a servant of the example - "calculator",
 * it is a remote object class that implements Calculator.
 * This function of it is to do the calculation for given two parameters.
 */

package exampleServer;


public class CalculatorServant implements Calculator {
	public CalculatorServant() {
	}

	@Override
	public int add(int x, int y) {
		System.out.println("CalculatorServant: Got request to add " + x
				+ " and " + y);
		System.out.println("CalculatorServant: get the result " + (x + y));
		return x + y; // return the result of add
	}

	@Override
	public int minus(int x, int y) {
		System.out.println("CalculatorServant: Got request to minus " + x
				+ " and " + y);
		System.out.println("CalculatorServant: get the result " + (x - y));
		return x - y; // return the result of minus
	}

	@Override
	public int multiply(int x, int y) {
		System.out.println("CalculatorServant: Got request to mutiply " + x
				+ " and " + y);
		System.out.println("CalculatorServant: get the result " + (x * y));
		return x * y; // return the result of multiply
	}

	@Override
	public int divide(int x, int y) {
		System.out.println("CalculatorServant: Got request to devide " + x
				+ " and " + y);
		System.out.println("CalculatorServant: get the result " + (x / y));
		return x / y; // return the result of divide
	}
}