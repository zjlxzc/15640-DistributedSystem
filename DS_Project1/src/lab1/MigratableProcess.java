package lab1;
import java.io.Serializable;

/* The interface extends Runnable to be run via a Thread object.
 * The interface also extends Serializable to be serialized and written to or read from a stream.
*/

public interface MigratableProcess extends Runnable, Serializable {
	/* This method will be called before the object is serialized.
	 * It affords an opportunity for the process to enter a known safe state.
	 */
	void suspend();
}
