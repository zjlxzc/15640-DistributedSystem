/**
 * File name: Calculator.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This is an interface of the example - "calculator".
 * This function of it is to provide methods that
 * need to be implemented by other classes that implement the interface.
 */

package exampleServer;

import remote.Remote;

public interface Calculator extends Remote {
   
	// these methods will be called from remote clients
   int add (int x, int y);
   int minus (int x, int y);
   int multiply (int x, int y);
   int divide (int x, int y);
}