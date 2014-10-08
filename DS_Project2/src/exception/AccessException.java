/**
 * File name: AccessException.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a exception may be thrown by RMI.
 */

package exception;

/*
 * An AccessException is thrown by certain methods of the classes (specifically bind, rebind, and unbind)
 * and methods of the to indicate that the caller does not have permission to perform the action
 * requested by the method call.
 * If the method was invoked from a non-local host, then an AccessException is thrown.
 */

public class AccessException extends Exception{

	private static final long serialVersionUID = 4420704323626163955L;

	public AccessException() {
		super();
	}
	
	/** Constructs an AccessException with the specified detail message.
	 *
	 * @param s - the detail message
	 */
	public AccessException(String s) {
		super(s);
	}
	
	/** Constructs an AccessException with the specified detail message and nested exception.
	 *
	 * @param s - the detail message
	 * @param ex - the nested exception
	 */
	public AccessException(String s, Exception ex) {
		super(s, ex);
		
	}
}
