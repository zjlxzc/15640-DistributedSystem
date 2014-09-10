package launchingProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import migratableProcess.MigratableProcess;


public class ProcessManagerSlave extends ProcessManager{
	
	// The server socket of master, this is pre-bound
	private final static int masterListenPort = 12345;
	
	// This maps the running processes and the combination of process name and arguments
	private Hashtable<String, Object> processTable = new Hashtable<String, Object>();
	
	// This maps the processes and the their threads
	private Hashtable<String, Thread> stats = new Hashtable<String, Thread>();
	
	// The slave thread to continually report to the master
	Thread reportThread;
	String hostname;
	
	/**
	 * The constructor of ProcessManagerSlave, it first starts a server socket to wait for the 
	 * migration request from master. Then start a socket to connect the server reporting the 
	 * listen socket and its own status.
	 */
	public ProcessManagerSlave(String hostname){							
		this.hostname = hostname;
		// The listen socket waiting for the master requests
		try {
			ServerSocket slaveListenSocket = new ServerSocket(0);
			
			Thread listenThread = new Thread(new Listen(slaveListenSocket));		
			listenThread.start();
			
			// The report socket sends the listen socket as well as the current status of slave
			reportThread = new Thread(new Report(hostname, slaveListenSocket.getLocalPort()));
			reportThread.start();
			
			System.out.println("I'm the salve");
			
		} catch (IOException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public Hashtable<String, Object> getProcessTable() {
		return processTable;
	}
	
	public Hashtable<String, Thread> getStats() {
		return stats;
	}
	
	public void showSlaves() {
		System.out.println("This method is not supported by slave");
	}
	
	public void exit() {
		try {
			Socket byebyeSocket = new Socket(hostname, masterListenPort);
			PrintWriter byebyeOut = new PrintWriter(byebyeSocket.getOutputStream(), true);
			InputStreamReader byebyeIn = new InputStreamReader(byebyeSocket.getInputStream());
			BufferedReader br = new BufferedReader(byebyeIn); 
			byebyeOut.write("Bye Bye\n");
			byebyeOut.flush();
			String res = br.readLine();
			if (res.equals("Bye Bye")) {
				byebyeSocket.close();
				System.out.println("Disconnect from master");
				System.exit(1);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}	
	}
	
	
	/**
	 * A separate thread for start the listen socket and keep listening from master
	 * This socket is used for receive the migrate request from master
	 *
	 */
	private class Listen implements Runnable {
		ServerSocket slaveListenSocket;
		public Listen(ServerSocket slaveListenSocket) {
			this.slaveListenSocket = slaveListenSocket;
		}
		
		public void run() {
			while (true) {
				Socket master;
				try {
					master = slaveListenSocket.accept();	
					
					// When get the master's request, start another thread to migrate
					Thread migrateThread = new Thread(new Migrate(master));
					migrateThread.start();					
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		}		
	}
	
	/**
	 * A separate thread to do the process migration, the slave can be the source or the destination
	 * of one migration, when a slave get the request from master, it will act accordingly.
	 * A source slave will first check if the process exists, then connect to the destination, suspend 
	 * the process and send the process object to the destination, then respond to master.
	 * A destination slave will first open a server socket waiting for source slave, then receive the
	 * suspended process, then resume the process and respond to source.
	 *
	 */
	private class Migrate implements Runnable {
		Socket master, toDes;
		ServerSocket toSrc;
		Migrate(Socket master) {
			this.master = master;
		}
		public void run() {		
			try {
				System.out.println("Get Master Migration Request");
				ObjectOutputStream masterOut = new ObjectOutputStream(master.getOutputStream());				
				ObjectInputStream masterIn = new ObjectInputStream(master.getInputStream());
				
				// Get the message from master and determine the roles
				String message = (String) masterIn.readObject();				
				System.out.println(message);
				
				// If the slave is source
				if (message.equals("Migration as Source")) {
					
					// send back confirmation
					masterOut.writeObject("Source Confirm");
					masterOut.flush();
					
					// get the target process name and confirm it existence
					String process = (String) masterIn.readObject();
					MigratableProcess midProcess = (MigratableProcess) processTable.get(process);
					if (midProcess == null) {
						masterOut.writeObject("No such process");
					} else {
						masterOut.writeObject("Process Confirm");
						// get the destination address and socket and connect to it
						message = (String) masterIn.readObject();
						String[] mArr = message.split(":");
						toDes = new Socket(mArr[0], Integer.parseInt(mArr[1]));						
						masterOut.writeObject("Source connected to Destination");
			
						ObjectOutputStream desOut = new ObjectOutputStream(toDes.getOutputStream());
						ObjectInputStream desIn = new ObjectInputStream(toDes.getInputStream());	
						
						// suspend the process and start to migrate
						System.out.println("Send: " + midProcess.toString() + " to " + toDes.getInetAddress().getHostName() + ":" + toDes.getLocalPort());
						midProcess.suspend();
						desOut.writeObject(midProcess);	
						desOut.flush();
						System.out.println("The process has been sent");
						
						// Get the confirm information from destination, inform the master and kill the process
						String desRes = (String)desIn.readObject();
						if (desRes.equals("success")) {
							System.out.println("Source complete successfully");
							masterOut.writeObject("success");
							midProcess = null;
						}
					} 
					toDes.close();
				}					
				
				// if the slave is destination
				if (message.equals("Migration as Destination")) {
					
					// send back confirmation
					masterOut.writeObject("Destination Confirm");
					
					// new a server socket to receive process and send the port to master
					toSrc = new ServerSocket(0);
					masterOut.writeObject("" + toSrc.getLocalPort());
					masterOut.flush();
					
					// get the connection from source
					Socket source = toSrc.accept();
					ObjectOutputStream srcOut = new ObjectOutputStream(source.getOutputStream());
					ObjectInputStream srcIn = new ObjectInputStream(source.getInputStream());	
										
					// then read the process object and restart it in a new thread
					MigratableProcess process = (MigratableProcess) srcIn.readObject();
					System.out.println("Receive:" + process.toString() + " from " + source.getInetAddress().getHostName() + ":" + source.getLocalPort());
					Thread thread = new Thread(process);
					thread.start();
					System.out.println("Start:" + process.toString());
					
					// put it into tables for status
					String processNameArg = process.toString();
					processTable.put(processNameArg, process);
					stats.put(processNameArg, thread);
					
					// send back message to source
					System.out.println("Destination complete successfully");
					srcOut.writeObject("success");
					srcOut.flush();
					toSrc.close();
				}				
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				try {
					master.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
	}
	


	/**
	 * A separate thread that keep reporting the slave's current status at a heart beating rate of 5 seconds.
	 * The report includes the listen port of slave, and the running process numbers.
	 *
	 */
	private class Report implements Runnable {
		Socket reportSocket;
		int slaveListenPort;
		String hostname;
		public Report(String hostname, int slaveListenPort) throws Exception {
			this.slaveListenPort = slaveListenPort;		
			this.hostname = hostname;
		}

		@Override
		public void run() {
			try {								
				while (true) {
					reportSocket = new Socket(hostname, masterListenPort);
					PrintWriter out = new PrintWriter(reportSocket.getOutputStream());
					String processInfo = "";
					for (String p : stats.keySet()) {
						if (!stats.get(p).isAlive()) {
							processTable.remove(p);
						} else {
							processInfo += p + "\n";
						}
					}					
					out.write(slaveListenPort + "\n");
					out.write(processInfo);
					out.flush();
					reportSocket.close();
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}			
		}		
	}
}
