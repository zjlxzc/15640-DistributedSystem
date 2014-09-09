package launchingProcess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
	
	/**
	 * The constructor of ProcessManagerSlave, it first starts a server socket to wait for the 
	 * migration request from master. Then start a socket to connect the server reporting the 
	 * listen socket and its own status.
	 */
	public ProcessManagerSlave(String hostname){							
		
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
	
	/**
	 * A separate thread for start the listen socket and keep listening from master
	 * @author zjlxz
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
					Thread migrateThread = new Thread(new Migrate(master));
					migrateThread.start();					
				} catch (Exception e) {
					System.out.println(e);
				}				
			}
		}		
	}
	
	/**
	 * A seperate thread to do the process migration, it read the serialized object from master, 
	 * then start a new thread to recover the process, and put it into its own processTable and 
	 * stats table. When finishes the procedure, it sends a confirm message back to the master.
	 *
	 */
	private class Migrate implements Runnable {
		Socket master;
		Migrate(Socket master) {
			this.master = master;
		}
		public void run() {
			ObjectOutputStream masterOut;
			try {
				masterOut = new ObjectOutputStream(master.getOutputStream());
			
				ObjectInputStream masterIn = new ObjectInputStream(master.getInputStream());
				String message = (String) masterIn.readObject();
				
				
				System.out.println("Get Master connection");
				// first read the message					
				System.out.println(message);
				//ObjectOutputStream masterOut = null;
				
				if (message.equals("Migration Start")) {
										
					masterOut.writeObject("Destination Confirm");
					masterOut.flush();
					
					// then read the process object and restart it in a new thread
					MigratableProcess process = (MigratableProcess) masterIn.readObject();
					System.out.println("Receive:" + process.toString());
					Thread thread = new Thread(process);
					thread.start();
					System.out.println("Start:" + process.toString());
					
					// put it into tables for status
					String processNameArg = process.toString();
					processTable.put(processNameArg, process);
					stats.put(processNameArg, thread);
					
					// send back message
					masterOut.writeObject("success");
					masterOut.flush();
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				try {
					master.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * A seperate thread that keep reporting the slave's current status at a heartbeating rate.
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
			String message = "";
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
