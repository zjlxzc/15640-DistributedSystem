package launchingProcess;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import migratableProcess.MigratableProcess;


public class ProcessManagerSlave extends ProcessManager{
	
	// This maps the processes and the combination of process name and arguments
	private HashMap<String, Object> processMap;
	int masterListenPort = 12345;
	Thread reportThread;
	ObjectInputStream masterIn;
	ObjectOutputStream masterOut;
	
	public ProcessManagerSlave(String hostname) throws Exception{					
		
		@SuppressWarnings("resource")
		ServerSocket slaveListenPort = new ServerSocket();		
		reportThread = new Thread(new report(hostname, slaveListenPort.getLocalPort()));
		reportThread.start();
		
		while (true) {
			Socket master = slaveListenPort.accept();
			masterIn = new ObjectInputStream(master.getInputStream());
			masterOut = new ObjectOutputStream(master.getOutputStream());
			String message = (String) masterIn.readObject();
			if (message.equals("Migration Start")) {
				this.migrate();
			}
		}		
	}

	private void migrate() throws Exception {
		MigratableProcess process = (MigratableProcess) masterIn.readObject();
		new Thread(process).start();
	}

	private class report implements Runnable {
		Socket reportSocket;
		int slaveListenPort;
		public report(String hostname, int slaveListenPort) throws Exception {
			this.slaveListenPort = slaveListenPort;
			reportSocket = new Socket(hostname, masterListenPort);
		}

		@Override
		public void run() {
			String message;
			try {
				PrintWriter out = new PrintWriter(reportSocket.getOutputStream());
				while (true) {
					message = "" + slaveListenPort + processMap.size();
					out.write(message);
					out.flush();
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
}
