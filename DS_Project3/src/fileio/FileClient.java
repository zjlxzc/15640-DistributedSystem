package fileio;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 * 
 * This class is used to connect to name node of distributed file system.
 */

import HDFS.BlockRef;
import HDFS.NameNode;
import HDFS.NodeRef;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class FileClient {

    private NameNode nameNode; // Name Node of DFS
    private String nameNodeIP; // IP address
    private int nameNodePort; // port

    public FileClient(String ip, int port){
        this.nameNodeIP = ip;
        this.nameNodePort = port;
    }

    public void connectNameNode()
            throws RemoteException, NotBoundException {
    	Registry registry = LocateRegistry.getRegistry(nameNodeIP, nameNodePort);
        nameNode = (NameNode)registry.lookup(NameNode.class.getCanonicalName()); // get Name Node
    }

    public ArrayList<BlockRef> getFileInfo(String fileName)
            throws RemoteException{
    	return nameNode.getFileInfo(fileName); // get Block infor of this file
    }
}
