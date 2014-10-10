/**
 * File name: CalculatorClient.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a client to test RMI.
 * This function of this class is to get the result of calculation given two operands.
 */

package exampleCalculatorClient;

import registry.LocateRegistry;
import registry.SimpleRegistry;
import remote.RemoteObjectRef;
import exception.NotBoundException;
import exception.RemoteException;

public class CalculatorClient {

	public static void main(String[] args) {
		String host = args[0]; // get host
		int port = Integer.parseInt(args[1]); // get port
		String serviceName = args[2]; // get service name
		int arg1 = 0;
		int arg2 = 0;
		
		try {
			arg1 = Integer.parseInt(args[3]); // get first operand
			arg2 = Integer.parseInt(args[4]); // get second operand
		} catch (NumberFormatException e) { // handle possible exception if the input is not valid
	    	System.out.println("The last two arguments should be integers.");
	    }
		
		SimpleRegistry sr = (SimpleRegistry) LocateRegistry.getRegistry(host, port); // get registry
		System.out.println("Client     : Get Regsitry");
		
		RemoteObjectRef ror = null;
		try {
			ror = sr.lookup(serviceName); // get remote object reference
		} catch (NotBoundException e) {
			System.out.println(e.getStackTrace());
		}
		System.out.println("Client     : Get the remote object reference of \"" + serviceName + "\"");

		System.out.println("name: " + ror.getRemote_Interface_Name());
		Calculator cal = (Calculator) ror.localise(); // generate stub
		
		System.out.println("Client     : call method arguments: (" + arg1 + ", " + arg2 + ")");
		System.out.println();
		
		try {
			System.out.println("Client     : get the result of add: " + cal.add(arg1, arg2) + "\n");
			System.out.println("Client     : get the result of minus: " + cal.minus(arg1, arg2) + "\n");
			System.out.println("Client     : get the result of multiply: " + cal.multiply(arg1, arg2) + "\n");
		} catch (RemoteException e) {
			System.out.println(e.getStackTrace());
		}
		
		if (arg2 != 0) { // if the second operand is not zero, then we can do division
			try {
				System.out.println("Client     : get the result divide: " + cal.divide(arg1, arg2) + "\n");
			} catch (RemoteException e) {
				System.out.println(e.getStackTrace());
			}
		} else { // otherwise we provide feedback information
			System.out.println("For DIVIDE operation, the second parameter should not be 0.");
			System.out.println();
		}
	}
}