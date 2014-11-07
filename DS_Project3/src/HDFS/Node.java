package HDFS;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1982072159825855419L;
	private InetAddress ip;
	private int port;
	public Node(String ip, int port) throws UnknownHostException {
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
	public abstract void excuteJob();
	
	
}
