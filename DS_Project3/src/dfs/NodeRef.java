package dfs;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class NodeRef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InetAddress ip;
	private int port;
	public NodeRef(String ip, int port) throws UnknownHostException {
		this.ip = InetAddress.getByName(ip);
		this.port = port;		 
	}
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
	
	public boolean equals(NodeRef node) {
		return this.ip.toString().equals(node.getIp()) &&
				this.port == node.getPort();
	}
}
