package launchingProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import migratableProcess.MigratableProcess;

public class ProcessManagerMaster extends ProcessManager{
	
	// This is the master server socket for slave handshaking and reporting, it's pre-binded
	private static final int listenPort = 12345;
	
	// This maps the processes and the combination of process name and arguments
	private Hashtable<String, Object> processTable;
	private Hashtable<String, Thread> stats;
		
	// This maps the slave and their load;
	private HashMap<InetAddress, Integer> slaveLoad;
	
	// This maps the slave and their port;
	private HashMap<InetAddress, Integer> slaveMap;
		
	private Thread listen;
	private Thread update;
	
	public ProcessManagerMaster() throws IOException {
		super();
		processTable = new Hashtable<String, Object>();
		stats = new Hashtable<String, Thread>();
		slaveMap = new HashMap<InetAddress, Integer>();
		slaveLoad = new HashMap<InetAddress, Integer>();	
		ServerSocket listenSocket = new ServerSocket(listenPort);
		listen = new Thread(new socketListen(listenSocket));		
		listen.start();
		update = new Thread(new updateStatus());
		update.start();
		System.out.println("I'm the master");
	}
	
	public boolean migrate(String process, InetAddress des) throws Exception{
		
		// Get the accept socket to the destination slave
		Socket masterToDes = new Socket(des, slaveMap.get(des));
		System.out.println(slaveMap.get(des));
		System.out.println(masterToDes.getInetAddress() + ":" + masterToDes.getPort());
		ObjectOutputStream desOut = new ObjectOutputStream(masterToDes.getOutputStream());
		ObjectInputStream desIn = new ObjectInputStream(masterToDes.getInputStream());		
		
		boolean isMigrated = false; 
		
		desOut.writeObject("Migration Start");
		desOut.flush();	
		
		// If get the return confirmation from destination,then start to migrate
		String desRes = (String)desIn.readObject();
		System.out.println(desRes);
		if (desRes.equals("Des Confirm")) {
			System.out.println(processTable.get(process));
			MigratableProcess midProcess = (MigratableProcess) processTable.get(process);
			// suspend the process
			midProcess.suspend();
			Thread.sleep(1000);
			// send the process to the slave, then wait
			desOut.writeObject(midProcess);	
			desOut.flush();
			
			// Get the confirm information from destination, inform the source to kill the process
			String response = (String)desIn.readObject();
			if (response.equals("success")) {
				isMigrated = true;
			}			
		}
		masterToDes.close();
		if (isMigrated) {
			System.out.println("Migration success!!!!");
		} else {
			System.out.println("Not success, trying again");
		}
		return isMigrated;
	}
	
	public Hashtable<String, Object> getProcessTable() {
		return processTable;
	}
	
	public Hashtable<String, Thread> getStats() {
		return stats;
	}
	
	public void showSlaves() {
		for (InetAddress i : slaveMap.keySet()) {
			System.out.println(i.getHostName() + ":" + slaveMap.get(i) + " current running process:" + slaveLoad.get(i) + "\n");
		}
	}
	/**
	 * A separte thread for the master to accept slave handshaking and updating their status
	 * @author zjlxz
	 *
	 */
	private class socketListen implements Runnable{
		ServerSocket listenSocket;
		public socketListen(ServerSocket listenSocket){
			this.listenSocket = listenSocket;
		}
		@Override
		public void run() {
			try {
				while(true) {					
					// Waiting for the slave to update port and load information
					Socket slave = listenSocket.accept();
					InputStreamReader inStream = new InputStreamReader(slave.getInputStream());
					BufferedReader br = new BufferedReader(inStream);
					String inMessage = "";  
					String inLine = "";
			        while((inLine = br.readLine()) != null) {
			        	inMessage += inLine;
			        }			        
			        
			        // The information is constructed by two parts: port and process nums, splited by space
			        String[] inArray = inMessage.split(" ");
			        slaveMap.put(slave.getInetAddress(), Integer.valueOf(inArray[0]));			 
			        slaveLoad.put(slave.getInetAddress(), Integer.valueOf(inArray[1]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					listenSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		} 
	}
	
	private class updateStatus implements Runnable{
		
		@Override
		public void run() {			
			while (true) {				
				// every time will check if the process is running, if not, it will update the processTable as well 
				if (stats != null && !stats.isEmpty()) {
					for (String cur : stats.keySet()) {
						if(!stats.get(cur).isAlive()) {
							processTable.remove(cur);
						}
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
