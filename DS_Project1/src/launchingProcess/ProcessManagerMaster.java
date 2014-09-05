package launchingProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import migratableProcess.MigratableProcess;

public class ProcessManagerMaster extends ProcessManager{
			
	// This maps the processes and the combination of process name and arguments
	private HashMap<String, Object> processMap;
		
	// This maps the slave and their load;
	private HashMap<InetAddress, Integer> slaveLoad;
	
	// This maps the slave and their load;
	private HashMap<InetAddress, Integer> slaveMap;
	
	private static final int listenPort = 12345;
	
	private Thread listen;
	
	public ProcessManagerMaster() throws IOException {
		super();
		ServerSocket listenSocket = new ServerSocket(listenPort);
		listen = new Thread(new socketListen(listenSocket));		
		listen.start();
	}
	
	public boolean migrate(String process, InetAddress des) throws Exception{
		
		if (slaveLoad.get(des) > 5) {
			System.out.println("The destination host is full loaded, please try to find another");
			return false;
		}
		listen.wait();		
		
		// Get the master socket to different slave
		Socket masterToDes = new Socket(des, slaveMap.get(des));		
		ObjectInputStream desIn = new ObjectInputStream(masterToDes.getInputStream());
		ObjectOutputStream desOut = new ObjectOutputStream(masterToDes.getOutputStream());
		
		boolean isMigrated = false; 
		
		// Start to migration, send the process name to launch new process in destination
		desOut.writeObject("Migration Start");
		desOut.flush();	
		
		// If get the return confirmation from destination,then start to get the process from src
		String desRes = (String)desIn.readObject();
		
		if (desRes.equals("Des Confirm")) {			
			MigratableProcess midProcess = (MigratableProcess) processMap.get(process);
			midProcess.suspend();
			desOut.writeObject(midProcess);	
			desOut.writeObject("send completed");
			desOut.flush();
			Thread.sleep(10000);
			
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
					Socket client = listenSocket.accept();
					InputStreamReader inStream = new InputStreamReader(client.getInputStream());
					BufferedReader br = new BufferedReader(inStream);
					String inMessage = "";  
					String inLine = "";
			        while((inLine = br.readLine()) != null) {
			        	inMessage += inLine;
			        }
			        
			        // The information is constructed by two parts: port and process nums, splited by space
			        String[] inArray = inMessage.split(" ");
			        slaveMap.put(client.getLocalAddress(), Integer.valueOf(inArray[0]));			 
			        slaveLoad.put(client.getLocalAddress(), Integer.valueOf(inArray[1]));
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
}
