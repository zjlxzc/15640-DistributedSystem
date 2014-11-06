package managementTool;

import java.net.UnknownHostException;

public class Slave extends Node{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3414381471893878548L;
	private static TaskTracker coordinator;
	public Slave(String ip, int port) throws UnknownHostException {
		super(ip, port);
		coordinator = new TaskTracker();
	}

	@Override
	public void excuteJob() {
		// TODO Auto-generated method stub
		
	}

}
