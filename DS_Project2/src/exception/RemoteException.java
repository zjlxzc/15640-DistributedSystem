package exception;

import java.io.IOException;

/*
 * A RemoteException is the common superclass for a number of communication-related exceptions
 * that may occur during the execution of a remote method call.
 * Each method of a remote interface, an interface that extends java.rmi.Remote,
 * must list RemoteException in its throws clause.
 */

public class RemoteException extends IOException {

	private static final long serialVersionUID = 3122003976067446297L;
	
	private Throwable detail; // the cause of the remote exception
	
	// constructs a RemoteException
	public RemoteException() {	
	}
	
	/** constructs a RemoteException with the specified detail message
	 *
	 * @param s - the detail message
	 */
	public RemoteException(String s) {	
		super(s);	
	}
	
	/**Constructs a RemoteException with the specified detail message and cause.
	 *This constructor sets the detail field to the specified Throwable.
	 *
	 * @param s - the detail message
	 * @param cause - the cause
	 */
	public RemoteException(String s, Throwable cause) {	
		super(s);	
		detail = cause;
	}
	
	// Returns the detail message, including the message from the cause, if any, of this exception.
	public String getMessage() {
		if (detail == null) {
			return super.getMessage();
		} else {
			return "Excpetion Info In Remote Exception" + detail.toString();
		}
	}
	
	// Returns the cause of this exception. This method returns the value of the detail field.
	public Throwable getCause() {
		return detail;
	}
}
