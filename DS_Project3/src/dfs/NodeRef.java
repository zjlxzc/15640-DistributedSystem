package dfs;

/*
 * @author Chun Xu (chunx)
 * @author Jialing Zhou (jialingz)
 *
 * This class is the structure of a data node reference.
 */

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeRef implements Serializable {

	private static final long serialVersionUID = 1L; // generated id
	private InetAddress ip; // data node ip address
	private int port; // data node port number
	
	public NodeRef(String ip, int port) throws UnknownHostException {
		this.ip = InetAddress.getByName(ip);
		this.port = port;		 
	}
	
	// getters and setters
	public InetAddress getIp() {
		return ip;
	}
	
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}
