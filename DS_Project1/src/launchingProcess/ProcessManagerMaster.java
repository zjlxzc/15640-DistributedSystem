package launchingProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import migratableProcess.MigratableProcess;

public class ProcessManagerMaster extends ProcessManager{
	
	// This is the master server socket for slave handshaking and reporting, it's pre-binded
	private static final int listenPort = 12345;
	
	// This maps the processes and the combination of process name and arguments
	private Hashtable<String, Object> processTable;
	private Hashtable<String, Thread> stats;
		
	// This maps the slave and their load;
	private HashMap<InetAddress, ArrayList<String>> slaveLoad;
	
	// This maps the slave and their port;
	private HashMap<InetAddress, Integer> slaveMap;
		
	private Thread listen;
	private Thread update;
	
	public ProcessManagerMaster() throws IOException {
		super();
		processTable = new Hashtable<String, Object>();
		stats = new Hashtable<String, Thread>();
		slaveMap = new HashMap<InetAddress, Integer>();
		slaveLoad = new HashMap<InetAddress, ArrayList<String>>();	
		ServerSocket listenSocket = new ServerSocket(listenPort);
		listen = new Thread(new socketListen(listenSocket));		
		listen.start();
		update = new Thread(new updateStatus());
		update.start();
		System.out.println("I'm the master");
	}
	
	public void migrate(String process, InetAddress desAdd){
		Thread migrateThread = new Thread(new Migration(process.trim(),desAdd));		
		migrateThread.start();
	}
	
	
	public Hashtable<String, Object> getProcessTable() {
		return processTable;
	}
	
	public Hashtable<String, Thread> getStats() {
		return stats;
	}
	
	public void showSlaves() {
		for (InetAddress i : slaveMap.keySet()) {
			System.out.println(i.getHostName() + ":" + slaveMap.get(i) + " current running process: \n");
			for (String s : slaveLoad.get(i)) {
				System.out.println(s + "\n");
			}
			System.out.println("*****************************************************************************");
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
					slaveMap.put(slave.getInetAddress(), Integer.valueOf(br.readLine()));					
					ArrayList<String> processOnSlave = new ArrayList<String>();
					String inLine = "";
			        while((inLine = br.readLine()) != null) {
			        	processOnSlave.add(inLine);
			        }	
			        slaveLoad.put(slave.getInetAddress(), processOnSlave);
				}
			} catch (IOException e) {
				System.out.println(e);
				this.run();
			}finally {
				try {
					listenSocket.close();
				} catch (IOException e) {
					System.out.println(e);
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
					System.out.println(e);
					this.run();
				}
			}
		}
	}
	
	public class Migration implements Runnable{
		private String process;
		private InetAddress desAdd;
		public Migration(String process, InetAddress desAdd) {	
			this.process = process;
			this.desAdd = desAdd;
		}
		
		@Override
		public void run() {
			try {
				Socket masterToDes = new Socket(desAdd.getHostName(), slaveMap.get(desAdd));
			
				ObjectOutputStream desOut = new ObjectOutputStream(masterToDes.getOutputStream());
				ObjectInputStream desIn = new ObjectInputStream(masterToDes.getInputStream());		
				
				boolean isMigrated = false; 
				
				desOut.writeObject("Migration Start");
				desOut.flush();	
				
				// If get the return confirmation from destination,then start to migrate
				String desRes = (String)desIn.readObject();
				System.out.println(desRes);
				if (desRes.equals("Destination Confirm")) {
					MigratableProcess midProcess = (MigratableProcess) processTable.get(process);
					if (midProcess == null) {
						System.out.println("The process does not exist or has been terminated");
					} else {
						System.out.println("Send: " + midProcess.toString());
						// suspend the process
						midProcess.suspend();

						// send the process to the slave, then wait
						desOut.writeObject(midProcess);	
						desOut.flush();
						System.out.println("Sent: " + midProcess.toString());
						
						// Get the confirm information from destination, inform the source to kill the process
						String response = (String)desIn.readObject();
						if (response.equals("success")) {
							isMigrated = true;
							midProcess = null;
						}
					}
					masterToDes.close();
					if (isMigrated) {
						System.out.println("Migration success!!!!");
					} else {
						System.out.println("Migration failed");
					}
				}					
			} catch (Exception e) {
				System.out.println("Migration failed");
				System.out.println(e);
			}
		}
	}
}
