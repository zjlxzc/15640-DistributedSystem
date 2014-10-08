/**
 * File name: AlreadyBoundException.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a exception may be thrown by RMI.
 */

package exception;

/* An AlreadyBoundException is thrown if an attempt is made to bind an object
 * in the registryto a name that already has an associated binding.
 * 
 */

public class AlreadyBoundException extends Exception{

	private static final long serialVersionUID = -2228644711711276144L;

	// Constructs an AlreadyBoundException with no specified detail message.
	public AlreadyBoundException() {
		super();
	}
	
	/** Constructs an AlreadyBoundException with the specified detail message.
	 * 
	 * @param s - the detail message
	 */
	public AlreadyBoundException(String s) {
		super(s);
	}
}
