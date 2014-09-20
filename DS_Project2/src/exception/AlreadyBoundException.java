package exception;

public class AlreadyBoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2228644711711276144L;

	public AlreadyBoundException() {
		super();
	}
	
	public AlreadyBoundException(String s) {
		super(s);
	}
}
