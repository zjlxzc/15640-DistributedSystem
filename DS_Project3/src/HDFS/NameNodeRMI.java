package HDFS;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface NameNodeRMI extends Remote{
	
	public ArrayList<NodeRef> addBlock(String fileName, NodeRef sourceNode) throws RemoteException;
	public void update(Hashtable<String, ArrayList<BlockRef>> nodeTable) throws RemoteException;
}
