package exception;

import java.io.IOException;

public class RemoteException extends IOException {

	private Throwable detail; // the cause of the remote exception
	
	// constructs a RemoteException
	public RemoteException() {	
	}
	
	// constructs a RemoteException with the specified detail message
	public RemoteException(String s) {	
		super(s);	
	}
	
	//Constructs a RemoteException with the specified detail message and cause.
	//This constructor sets the detail field to the specified Throwable.
	public RemoteException(String s, Throwable cause) {	
		super(s);	
		detail = cause;
	}
	
	public String getMessage() {
		if (detail == null) {
			return super.getMessage();
		} else {
			return "Excpetion Info In Remote Exception" + detail.toString();
		}
	}
	
	public Throwable getCause() {
		return detail;
	}
}
