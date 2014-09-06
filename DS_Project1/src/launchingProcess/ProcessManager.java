package launchingProcess;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

import migratableProcess.MigratableProcess;

/**
 * 
 * @author Jialing Zhou, Chun Xu
 * 
 * The ProcessManager is designed to monitor for requests to launch, remove, and migrate processes.
 *
 */
public class ProcessManager {
	
	// This maps the processes and the combination of process name and arguments
	private ProcessManager manager;
	public ProcessManager() {}
	
	/**
	 * Constructor of Manager, define the master and slave role
	 * @param args
	 * @throws Exception
	 */
	public ProcessManager(String args[]) throws Exception {
		if (args.length == 0) {			
			manager = new ProcessManagerMaster();	
		} else if (args.length == 2 && args[0].equals("-c")) {	
			String hostname = args[1];
			manager = new ProcessManagerSlave(hostname);			
		} else {
			System.out.print("Illigel Arguments\n");
			System.exit(1);
		}
		manager.control();
	}
	
	/**
	 * Main function
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String args[]) throws Exception {	
		new ProcessManager(args);
	}
	
	/**
	 * This method is used to receive user's input and call according methods
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void control() throws Exception {		
		Scanner in = new Scanner(System.in);
		usage();
		while (true) {
						
			String[] command = in.nextLine().split(" ");
			
			// List all the process
			if (command[0].equals("ps")) {
				this.listProcess();
			} else if (command[0].equals("quit")) {  // quit the manager
				System.out.println("Bye Bye");
				System.exit(0);
			} else if (command[0].equals("launch")) { // launch new process
				String process = "";
				for(int i = 1; i < command.length; i++) {
					process += command[i] + " ";
				}
				
				boolean isLaunched = this.launchProcess(process.trim());
				if (isLaunched) {
					System.out.println("The process is successfully launched");
				} else {
					System.out.println("Launch Failed");
				}
			} else if (command[0].equals("migrate")) {  // migarate process
				if (this instanceof ProcessManagerMaster) {
					int len = command.length;
					String des = command[len - 1];
					String process = "";
					for(int i = 1; i < command.length - 1; i++) {
						process += command[i] + " ";
					}
					InetAddress desAdd = InetAddress.getByName(des.trim());
					System.out.println(process);
					boolean isMigrated = this.migrate(process,desAdd);
					if (isMigrated) {
						System.out.println("The process is successfully migrated");
					} else {
						System.out.println("Migration failed");
					}
				}							
			} else if (command[0].equals("slaves")) {
				this.showSlaves();
			}
			else {
				System.out.println("Wrong input, please try again");
				usage();
			}
		}
	}


	private static void usage() {
		System.out.println("Process Manager for Migrable process");
		System.out.println("Usage: launch <ProcessName> [Arg1] [Arg2]...[ArgN]: Launch process, processName must exist)");
		System.out.println("Usage: ps: print all the processes with their arguments");
		System.out.println("Usage: migrate <ProcessName> [Arg1] [Arg2]...[ArgN] <Source> <Destination> (Migrate process from source to destination)");
		System.out.println("Usage: quit (Quit Process Manager)");
		System.out.println("");
	}	
	
	private boolean launchProcess(String process)  {
		Class<?> newProcessClass;
		if (this.getProcessTable() != null && !this.getProcessTable().contains(process)){
			try {
				String[] nameArgs = process.split(" ");			
				String className = nameArgs[0]; 			
				newProcessClass = Class.forName(className);
				Object[] args = {Arrays.copyOfRange(nameArgs, 1, nameArgs.length)};
				Constructor<?> con = newProcessClass.getConstructor(String[].class);
				MigratableProcess newProcess = (MigratableProcess)con.newInstance(args);
				Thread thread = new Thread(newProcess);
				thread.start();				
				this.getProcessTable().put(process, newProcess);
				this.getStats().put(process, thread);
				System.out.println("Process lauching successfull");
			} catch (Exception e) {
				System.out.println("Process launching failed");
				e.printStackTrace();
				return false;
			}
			return true;
		} 
		if (this.getProcessTable().contains(process)) {
			System.out.println("This Process is already launched");
		}
		return false;
	}
	
	protected void listProcess() {
		if (this.getStats() != null && !this.getStats().isEmpty()) {
			Iterator<String> it = this.getStats().keySet().iterator();
			while (it.hasNext()) {
				String curProcess = it.next();
				if(this.getStats().get(curProcess).isAlive()) {
					System.out.println(curProcess);
				} else {
					System.out.println("The process " + curProcess + " has been terminated");
				}
			}
			return;
		}
		System.out.println("No process running");
	}
	
	
	protected boolean migrate(String process, InetAddress desAdd) throws Exception {
		return false;
	}
	
	public Hashtable<String, Object> getProcessTable() {
		return null;
	}
	
	public Hashtable<String, Thread> getStats() {
		return null;
	}
	
	public void showSlaves() {
	}
}
