package launchingProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

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
	
	/**
	 * Starts another thread to do the migration
	 */
	public void migrate(String process, InetAddress srcAdd, InetAddress desAdd){
		Thread migrateThread = new Thread(new Migration(process.trim(),srcAdd, desAdd));		
		migrateThread.start();
	}	
	
	public Hashtable<String, Object> getProcessTable() {
		return processTable;
	}
	
	public Hashtable<String, Thread> getStats() {
		return stats;
	}
	
	/**
	 * Shows the globe information for all the slaves, help user to decide where and what to migrate
	 */
	public void showSlaves() {
		if (slaveMap.isEmpty()) {
			System.out.println("No slave connected now");
		}
		for (InetAddress i : slaveMap.keySet()) {
			System.out.println(i.getHostName() + ":" + slaveMap.get(i) + " current running process:");
			if (slaveLoad.isEmpty()) {
				System.out.println("none");
			} else {
				for (String s : slaveLoad.get(i)) {
					System.out.println(s);
				}
			}			
			System.out.println("*****************************************************************************");
		}
	}
	
	/**
	 * A separte thread for the master to accept slave handshaking and updating their status
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
					PrintWriter outStream = new PrintWriter(slave.getOutputStream());				
					String inLine = br.readLine();
					if (inLine.equals("Bye Bye")) {						
						System.out.println(slave.getInetAddress().getHostName() + " disconnected");
						slaveMap.remove(slave.getInetAddress());
						outStream.write("Bye Bye\n");
						outStream.flush();
					} else {
						slaveMap.put(slave.getInetAddress(), Integer.valueOf(inLine));					
						ArrayList<String> processOnSlave = new ArrayList<String>();
				        while((inLine = br.readLine()) != null) {
				        	processOnSlave.add(inLine);
				        }	
				        slaveLoad.put(slave.getInetAddress(), processOnSlave);
					}
					
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
	
	/**
	 * 
	 * A separated thread that allow master to migrate one process from one slave to another. 
	 * The master will first make sure the source slave and the process existence, then it will
	 * confirm the destination. Then it will send the destination's address and server socket to
	 * source, let them connect and migrate the process. Finally it will get the response from
	 * source to judge if the migration is successful.
	 *
	 */
		
	private class Migration implements Runnable{
		private String process;
		private InetAddress srcAdd;
		private InetAddress desAdd;
		public Migration(String process, InetAddress srcAdd, InetAddress desAdd) {	
			this.process = process;
			this.srcAdd = srcAdd;
			this.desAdd = desAdd;
		}
		
		@Override
		public void run() {
			try {
				boolean isMigrated = false; 
				Socket masterToSrc = new Socket(srcAdd.getHostName(), slaveMap.get(srcAdd));
				Socket masterToDes = new Socket(desAdd.getHostName(), slaveMap.get(desAdd));
				
				ObjectOutputStream srcOut = new ObjectOutputStream(masterToSrc.getOutputStream());
				ObjectInputStream srcIn = new ObjectInputStream(masterToSrc.getInputStream());
				ObjectOutputStream desOut = new ObjectOutputStream(masterToDes.getOutputStream());
				ObjectInputStream desIn = new ObjectInputStream(masterToDes.getInputStream());		
				
				String srcRes, desRes;
				
				// Confirm the source slave
				System.out.println("Migration source: " + srcAdd.getHostName());
				srcOut.writeObject("Migration as Source");
				srcOut.flush();					
				srcRes = (String)srcIn.readObject();
				
				if (srcRes.equals("Source Confirm")) {
					
					System.out.println("Source Confirm: " + srcAdd.getHostName());
					
					// Confirm the target process
					srcOut.writeObject(process);
					srcOut.flush();
					srcRes = (String)srcIn.readObject();
					
					if (srcRes.equals("No such process")) {
						System.out.println("The process does NOT exist on source, please check again");
					} else {
						System.out.println(srcRes);
						// Confirm the destination
						System.out.println("Migration destination: " + desAdd.getHostName());
						desOut.writeObject("Migration as Destination");
						desOut.flush();	
						desRes = (String)desIn.readObject();
						
						if (desRes.equals("Destination Confirm")) {
							
							// Get the destination address and socket and send to source
							desRes = (String)desIn.readObject();
							System.out.println("Destination Confirm: " +  desAdd.getHostName());							
							srcOut.writeObject(desAdd.getHostName() + ":" + desRes);
							srcOut.flush();
							srcRes = (String)srcIn.readObject();
							
							if (srcRes.equals("Source connected to Destination")) {	
								
								// Let the slaves migrate the process, and then get the response
								System.out.println("Source connected to Destination: start to migrate");
								srcRes = (String)srcIn.readObject(); 
								if (srcRes.equals("success")) {
									isMigrated = true;
								} else {
									
								}
							}
						}	
					}							
					masterToSrc.close();
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
