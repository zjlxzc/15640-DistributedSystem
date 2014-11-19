package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a data node table.
 * It has 
 */

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataNodeTable {
	private static HashMap<String, NodeRef> nodeMap; // a hash map to store a data node's ip address and its reference
	
	public DataNodeTable() {
		nodeMap = new HashMap<String, NodeRef>();
	}
	
	// add a data node information to the table
	public void addNode(String ip, int port) throws UnknownHostException {	
		NodeRef ref = new NodeRef(ip, port); // create data node reference
		nodeMap.put(ref.getIp().getHostAddress(), ref);
	}
	
	// return all data nodes
	public ArrayList<NodeRef> getDataNodes() {
		ArrayList<NodeRef> slaves = new ArrayList<NodeRef>();
		for (NodeRef node : nodeMap.values()) {
			slaves.add(node);
		}
		return slaves;
	}
	
	// get node reference according to ip address
	public NodeRef getDataNode(String ip) {
		return nodeMap.get(ip);
	}
	
	// print out all data node 's ip address and port
	public void list() {
		for (String key : nodeMap.keySet()) {
			NodeRef node = nodeMap.get(key);
			System.out.println(node.getIp() + " : " + node.getPort());
		}		
	}	
}
