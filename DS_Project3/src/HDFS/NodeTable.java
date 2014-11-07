package HDFS;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NodeTable {
	private static HashMap<String, Node> nodeMap;
	private static NameNode master = null;
	Random rand = new Random();
	public NodeTable() {
		nodeMap = new HashMap<String, Node>();
	}
	
	public void addNode(String ip, int port, String type) throws UnknownHostException {	
		Node node = null;
		if (type.equals("master")) {
			node = new NameNode(ip, port);
			master = (NameNode) node;
		} else if (type.equals("slave")) {
			node = new DataNode(ip, port);
		}
		nodeMap.put(ip, node);
	}
	
	public NameNode getMaster() {
		return master;
	}
	
	public ArrayList<Node> getSlaves() {
		ArrayList<Node> slaves = new ArrayList<Node>();
		for (Node node : nodeMap.values()) {
			if (node.getClass().getSimpleName().equals("Slave")) {
				slaves.add(node);
			}
		}
		return slaves;
	}
	
	public Node getNode(String ip) {
		return nodeMap.get(ip);
	}
	
	public void list() {
		for (String key : nodeMap.keySet()) {
			Node node = nodeMap.get(key);
			System.out.println(node.getIp() + " : " + node.getPort() 
					+ " : " + node.getClass().getSimpleName());
		}		
	}	
}
