package HDFS;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class NameNodeRMIServent implements NameNodeRMI{
	
	private Hashtable<String, ArrayList<BlockRef>> fileTable;
	private DataNodeTable dataNodeTable;
	private int factor;
	public NameNodeRMIServent(Hashtable<String, ArrayList<BlockRef>> fileTable, 
			DataNodeTable dataNodeTable, int factor){
		this.fileTable = fileTable;
		this.dataNodeTable = dataNodeTable;
		this.factor = factor;
	}
	
	public ArrayList<NodeRef> addBlock(String fileName, NodeRef sourceNode) throws RemoteException {
		ArrayList<NodeRef> ret = new ArrayList<NodeRef>();
		ArrayList<NodeRef> nodeList = dataNodeTable.getDataNodes();
		if (!fileTable.contains(fileName)) {
			int cnt = 0;
			int total = nodeList.size();
			while (cnt < factor) {
				int index = cnt % total;
				NodeRef des = nodeList.get(index);
				if (!des.getIp().equals(sourceNode.getIp())) {
					ret.add(des);
				}				
			}
		} else {
			HashMap<NodeRef, Integer> freq = new HashMap<NodeRef, Integer>();
			ArrayList<BlockRef> blockList = fileTable.get(fileName);
			for (BlockRef block : blockList) {
				if (!freq.containsKey(block.getNodeRef())) {
					freq.put(block.getNodeRef(), 1);
				} else {
					freq.put(block.getNodeRef(), freq.get(block.getNodeRef()) + 1);
				}
			}
			for (NodeRef node : nodeList) {
				if (!freq.containsKey(node)) {
					freq.put(node, 0);
				}
			}
			List<Map.Entry<NodeRef,Integer>> sort=new ArrayList<>();  
			sort.addAll(freq.entrySet());  
			Collections.sort(sort, new ValueComparator());
			int cnt = 0;
			int total = sort.size();
			while (cnt < factor) {
				int index = cnt % total;
				NodeRef des = sort.get(index).getKey();
				if (!des.getIp().equals(sourceNode.getIp())) {
					ret.add(des);
				}	
			}
		}
		return ret;
	}
	
	private static class ValueComparator implements Comparator<Map.Entry<NodeRef, Integer>> {    
        public int compare(Map.Entry<NodeRef, Integer> mp1, Map.Entry<NodeRef, Integer> mp2) {    
            return mp1.getValue() - mp2.getValue();    
        }    
    }
   
	
	public void update(Hashtable<String, ArrayList<BlockRef>> nodeTable) throws RemoteException {
		
	}
}
