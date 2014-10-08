/**
 * File name: RORtbl.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is used to maintain a remote object reference table.
 */

package remote;

import java.util.Hashtable;
import java.util.Random;

public class RORtbl
{
    Hashtable<Integer, Object> table = new Hashtable<Integer, Object>();
    // make a new table. 
    public RORtbl(){
    }
    
    // given the object, construct a RemoteObjectRef and put them into hashtable
    public RemoteObjectRef addObj(String host, int port, Object o){	
    	String remoteInterface = o.getClass().getInterfaces()[0].getName();
    	Random rand = new Random();
    	int obj_key = 0;
    	while (true) {
    		obj_key = rand.nextInt(900000) + 100000; // generate a random number
    		if (!table.containsKey(obj_key)) {
    			break;
    		}
    	}
    	
    	RemoteObjectRef ror = new RemoteObjectRef(host, port, obj_key, remoteInterface);
    	table.put(obj_key, o);
    	return ror;
	}

    // given a RemoteObjectRef, find the corresponding object.
    public Object findObj(RemoteObjectRef ror)
	{
	    return table.get(ror.obj_key);
	}
}
