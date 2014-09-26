package exception;

/*
 * A MarshalException is thrown if a java.io.IOException occurs
 * while marshalling the remote call header, arguments or return value for a remote method call.
 */

public class MarshalException extends RemoteException {

	private static final long serialVersionUID = 1L;

	// Constructs a MarshalException with no parameters.
	public MarshalException() {
	}
	
	/** Constructs a MarshalException with the specified detail message.
	 * @param s - the detail message
	 */
	public MarshalException(String s) {
		super(s);
	}
	
	/**Constructs a MarshalException with the specified detail message and nested exception.
	 *
	 * @param s - the detail message
	 * @param cause - the cause
	 */
	public MarshalException(String s, Exception ex) {
		super(s, ex);
	}
}
