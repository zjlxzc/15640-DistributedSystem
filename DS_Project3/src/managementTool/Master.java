package managementTool;
import java.net.UnknownHostException;

public class Master extends Node{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6518450721543405765L;
	private static Scheduler scheduler;
	public Master(String ip, int port) throws UnknownHostException {
		super(ip, port);
		scheduler = new Scheduler();
	}

	@Override
	public void excuteJob() {
		// TODO Auto-generated method stub
		
	}	
}
