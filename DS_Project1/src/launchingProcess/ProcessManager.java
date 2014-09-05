package launchingProcess;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.Arrays;
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
	private Hashtable<String, Object> processTable;
	private Hashtable<String, Thread> stats;
	
	public ProcessManager() {}
	
	/**
	 * Constructor of Manager, define the master and slave role
	 * @param args
	 * @throws Exception
	 */
	public ProcessManager(String args[]) throws Exception {
		if (args.length == 0) {
			processTable = new Hashtable<String, Object>();
			stats = new Hashtable<String, Thread>();
			new ProcessManagerMaster();			
		} else if (args[0].equals("-c")) {	
			String hostname = args[1];
			processTable = new Hashtable<String, Object>();
			stats = new Hashtable<String, Thread>();
			new ProcessManagerSlave(hostname);
		}
	}
	
	/**
	 * Main function
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String args[]) throws Exception {
		ProcessManager manager = new ProcessManager(args);		
		manager.control();
	}
	
	/**
	 * This method is used to receive user's input and call according methods
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void control() throws Exception {
		usage();		
		Scanner in = new Scanner(System.in);
		while (true) {
			String[] command = in.nextLine().split(" ");
			
			// List all the process
			if (command[0].equals("ps")) {
				this.listProcess();
			}
			
			// quit the manager
			if (command[0].equals("quit")) {
				System.out.println("Bye Bye");
				System.exit(0);
			}
			if (command[0].equals("Launch")) {
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
			}
			if (command[0].equals("Migrate")) {
				if (this instanceof ProcessManagerMaster) {
					int len = command.length;
					String des = command[len - 1];
					String process = "";
					for(int i = 1; i < command.length - 1; i++) {
						process += command[i] + " ";
					}
					InetAddress desAdd = InetAddress.getByName(des.trim());
					boolean isMigrated = this.migrate(process,desAdd);
					if (isMigrated) {
						System.out.println("The process is successfully migrated");
					} else {
						System.out.println("Migration failed");
					}
				}			
				
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
		try {
			String[] nameArgs = process.split(" ");
			String className = nameArgs[0]; 
			newProcessClass = Class.forName(className);
			Object[] args = {Arrays.copyOfRange(nameArgs, 1, nameArgs.length)};
			Constructor<?> con = newProcessClass.getConstructor(String[].class);		
			MigratableProcess newProcess = (MigratableProcess)con.newInstance(args);
			Thread thread = new Thread(newProcess);
			thread.start();			
			processTable.put(process, newProcess);
			stats.put(process, thread);
		} catch (Exception e) {
			System.out.println("Process launching failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected void listProcess() {
		if (stats != null && !stats.isEmpty()) {
			Iterator<String> it = stats.keySet().iterator();
			while (it.hasNext()) {
				String curProcess = it.next();
				if(stats.get(curProcess).isAlive()) {
					System.out.println(curProcess);
				} else {
					System.out.println("The process" + curProcess + "has been terminated");
				}
			}
			return;
		}
		System.out.println("No process running");
	}
	
	
	protected boolean migrate(String process, InetAddress desAdd) throws Exception {
		return false;
	}
	
}
