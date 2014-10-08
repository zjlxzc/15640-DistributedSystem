/**
 * File name: NotBoundException.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is a exception may be thrown by RMI.
 */

package exception;

/*A NotBoundException is thrown if an attempt is made to lookup
 * or unbind in the registry a name that has no associated binding.
 */

public class NotBoundException extends Exception{

	private static final long serialVersionUID = -2725584755869388752L;

	// Constructs a NotBoundException with no specified detail message.
	public NotBoundException() {
		super();
	}
	
	/** Constructs a NotBoundException with the specified detail message.
	 * @param s - the detail message
	 */
	public NotBoundException(String s) {
		super(s);
	}
}
