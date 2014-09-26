package exception;

/*
 * An UnmarshalException can be thrown while unmarshalling 
 * the parameters or results of a remote method call under certain conditions.
 */
public class UnmarshalException extends RemoteException {

	private static final long serialVersionUID = 1L;

	// Constructs a UnmarshalException with no parameters.
	public UnmarshalException() {
	}

	/**
	 * Constructs a UnmarshalException with the specified detail message.
	 * 
	 * @param s - the detail message
	 */
	public UnmarshalException(String s) {
		super(s);
	}

	/**
	 * Constructs a UnmarshalException with the specified detail message and
	 * nested exception.
	 * 
	 * @param s - the detail message
	 * @param cause - the cause
	 */
	public UnmarshalException(String s, Exception ex) {
		super(s, ex);
	}
}
