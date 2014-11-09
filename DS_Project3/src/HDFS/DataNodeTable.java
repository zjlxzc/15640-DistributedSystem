package HDFS;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DataNodeTable {
	private static HashMap<String, NodeRef> nodeMap;
	private static NameNode master = null;
	Random rand = new Random();
	public DataNodeTable() {
		nodeMap = new HashMap<String, NodeRef>();
	}
	
	public void addNode(String ip, int port) throws UnknownHostException {	
		NodeRef ref = new NodeRef(ip, port);
		nodeMap.put(ip, ref);
	}
	
	public NameNode getMaster() {
		return master;
	}
	
	public ArrayList<NodeRef> getDataNodes() {
		ArrayList<NodeRef> slaves = new ArrayList<NodeRef>();
		for (NodeRef node : nodeMap.values()) {
			slaves.add(node);
		}
		return slaves;
	}
	
	public NodeRef getDataNode(String ip) {
		return nodeMap.get(ip);
	}
	
	public void list() {
		for (String key : nodeMap.keySet()) {
			NodeRef node = nodeMap.get(key);
			System.out.println(node.getIp() + " : " + node.getPort());
		}		
	}	
}
