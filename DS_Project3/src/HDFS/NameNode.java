package HDFS;
import java.net.UnknownHostException;

import managementTool.JobTracker;

public class NameNode extends Node{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6518450721543405765L;
	private static JobTracker jobTracker;
	public NameNode(String ip, int port) throws UnknownHostException {
		super(ip, port);
		jobTracker = JobTracker.getInstance();
	}
	@Override
	public void excuteJob() {
		// TODO Auto-generated method stub
		
	}
	public JobTracker getJobTracker() {
		return jobTracker;
	}
}
