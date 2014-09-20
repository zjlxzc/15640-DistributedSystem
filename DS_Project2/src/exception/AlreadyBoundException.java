package exception;

/* An AlreadyBoundException is thrown if an attempt is made to bind an object
 * in the registryto a name that already has an associated binding.
 * 
 */

public class AlreadyBoundException extends Exception{

	private static final long serialVersionUID = -2228644711711276144L;

	public AlreadyBoundException() {
		super();
	}
	
	public AlreadyBoundException(String s) {
		super(s);
	}
}
