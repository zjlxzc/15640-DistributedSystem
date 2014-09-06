/**
 * File name: MigratableProcess.java
 * @author Jialing Zhou (jialingz), Chun Xu (chunx)
 * Course/Section: 15640/A
 * 
 * Description: Lab 1: Portable, Migratable Work
 * 
 * The interface MigratableProcess extends Runnable to be run via a Thread object.
 * The interface also extends Serializable to be serialized and written to or read from a stream.
 */

package migratableProcess;
import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {
	
	/* This method will be called before the object is serialized.
	 * It affords an opportunity for the process to enter a known safe state.
	 */
	void suspend();
}
