/**
 * File name: RegistryServer.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is the registry server to register object.
 */

package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import exception.AlreadyBoundException;
import remote.RemoteObjectRef;

public class RegistryServer implements Runnable{
	
	int port;
	
	// the table stores service name and corresponding ror
	Hashtable<String, RemoteObjectRef> regTable;
	
	public RegistryServer(int port) {
		this.port = port;
		regTable = new Hashtable<String, RemoteObjectRef>();
	}
	
	// the main thread listening to the client, when get a request, 
	// start a correspond thread to excute and continue listening
	public void run() {
		ServerSocket reg = null;	
		try {			
			reg = new ServerSocket(port);
			System.out.println("Registry Server	: start at	: " + InetAddress.getLocalHost() + ":" + port);
			while(true) {
				Socket client = reg.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				
				String req = in.readLine();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				
				if (req.equals("I want a registry")) {
					out.println("I am a simple registry.");
					System.out.println("Registry Server	: send a simple registry to " + client.getInetAddress());
				} else if (req.equals("bind")) {
					new Thread(new Bind(in, out)).start();
				} else if (req.equals("rebind")) {
					new Thread(new Rebind(in, out)).start();
				} else if (req.equals("unbind")) {
					new Thread(new Unbind(in, out)).start();
				} else if (req.equals("Lookup")) {
					System.out.println("Registry Server	: get look up request");
					new Thread(new Lookup(client)).start();
				} else if (req.equals("List")) {
					new Thread(new List(out)).start();
				} else {
					out.println("Wrong request");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reg.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This class servers to bind a registry with its service name 
	 * on the registry server 
	 *
	 */
	private class Bind implements Runnable {
		
		BufferedReader in;
		PrintWriter out;
		
		public Bind(BufferedReader in, PrintWriter out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public void run() {						
			System.out.println("Registry Server	: start bind");
			String serviceName = null;
			
			// get the service name
			try {
				serviceName = in.readLine();
			} catch (IOException e) {
				System.out.println("Bind failed : cannot get the service name");
			}
			
			// get the service ip address
			String ip_adr = null;
			try {
				ip_adr = in.readLine();
			} catch (IOException e) {
				System.out.println("Bind failed : cannot get the ip address");
			}
			
			// get the service port
			int port = 0;
			try {
				port = Integer.parseInt(in.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Bind failed : port number is not valid");
			} catch (IOException e) {
				System.out.println("Bind failed : cannot get the port number");
			}
			
			// get the obj key
			int obj_Key = 0;
			try {
				obj_Key = Integer.parseInt(in.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Bind failed : object key is not valid");
			} catch (IOException e) {
				System.out.println("Bind failed : cannot get the object key");
			}
			
			// get the remote interface name of the service
			String remote_Interface_Name = null;
			try {
				remote_Interface_Name = in.readLine();
			} catch (IOException e) {
				System.out.println("Bind failed : cannot get the remote interface name");
			}
			
			// initialize a ror from the above arguments and register it to the table
			RemoteObjectRef ror = new RemoteObjectRef(ip_adr, port, obj_Key, remote_Interface_Name);
			if (!regTable.containsKey(serviceName)) {
				regTable.put(serviceName, ror);
				out.println("Bind success!");
				System.out.println("Registry Server	: bind success");
				System.out.println();
			} else {
				out.println("The service already bound");
			}								
		}
	}
	
	/**
	 * 
	 * the class serves to look up a service which is bound in the registry server
	 *
	 */
	private class Lookup implements Runnable {
		
		Socket client;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		public Lookup(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {				
			System.out.println("Registry Server	: start look up");
			
			try {
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());
				
				// get the service name
				String serviceName = null;
				try {
					serviceName = (String)in.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("Lookup failed : cannot get class");
				}
				System.out.println("Registry Server	: get servicename : " + serviceName);
				
				// look it up in the table, if find it then return to the client, if not
				// then return a message
				if (regTable.containsKey(serviceName)) {
					RemoteObjectRef ror = regTable.get(serviceName);
				
					out.writeObject("Find the service");
					out.writeObject(ror);
					
					System.out.println("Registry Server	: finished look up and return");
					System.out.println();
				} else {
					
					out.writeObject("The target service does not exist");
					System.out.println();
				}
			} catch (IOException e) {
				System.out.println("Lookup failed : stream failed");
			}
		}
	}
	
	/**
	 * 
	 * the class serves to rebind a service which is bound in the registry server,
	 * if the service is not bound, it will bind it
	 *
	 */
	private class Rebind implements Runnable {
		
		BufferedReader in;
		PrintWriter out;
		
		public Rebind(BufferedReader in, PrintWriter out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public void run() {			
			String serviceName = null;
			
			// get the service name
			try {
				serviceName = in.readLine();
			} catch (IOException e) {
				System.out.println("Rebind failed : cannot get the service name");
			}
			
			// get the service ip address
			String ip_adr = null;
			try {
				ip_adr = in.readLine();
			} catch (IOException e) {
				System.out.println("Rebind failed : cannot get the ip address");
			}
			
			// get the service port
			int port = 0;
			try {
				port = Integer.parseInt(in.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Rebind failed : port number is not valid");
			} catch (IOException e) {
				System.out.println("Rebind failed : cannot get the port number");
			}
			
			// get the obj key
			int obj_Key = 0;
			try {
				obj_Key = Integer.parseInt(in.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Rebind failed : object key is not valid");
			} catch (IOException e) {
				System.out.println("Rebind failed : cannot get the object key");
			}
			
			// get the remote interface name of the service
			String remote_Interface_Name = null;
			try {
				remote_Interface_Name = in.readLine();
			} catch (IOException e) {
				System.out.println("Rebind failed : cannot get the remote interface name");
			}			
				
			RemoteObjectRef ror = new RemoteObjectRef(ip_adr, port, obj_Key, remote_Interface_Name);
			regTable.put(serviceName, ror);
			out.println("Rebind success!");
			System.out.println("Registry Server: rebind success");	
			System.out.println();
			
		}
	}
	
	
	/**
	 * 
	 * the class serves to unbind a service which is bound in the registry server,
	 * if the service is not bound, it will return a warning message
	 *
	 */
	private class Unbind implements Runnable {
		
		BufferedReader in;
		PrintWriter out;
		
		public Unbind(BufferedReader in, PrintWriter out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public void run() {			
			System.out.println("Registry Server: start unbind");
			String serviceName = null;
			try {
				serviceName = in.readLine();
			} catch (IOException e) {
				System.out.println("Unbind failed : cannot get the service name");
			}				
			
			// look up the service name in the table, if exists, remove it, if not
			// return a message to client
			if (regTable.containsKey(serviceName)) {
				regTable.remove(serviceName);
				out.println("Unbind success!");
				System.out.println("Registry Server: unbind success");
				System.out.println();
			} else {
				out.println("The service has not been bound");
				System.out.println();
			}								
		} 
	}
	
	
	/**
	 * 
	 * the class serves to list the services which are bound in the registry server
	 *
	 */
	private class List implements Runnable {
		
		PrintWriter out;
		
		public List(PrintWriter out) {
			this.out= out;
		}
		
		@Override
		public void run() {		
			System.out.println("Registry Server: start list");
			if (!regTable.isEmpty()) {
				for (String serviceName : regTable.keySet()) {
					out.println(serviceName);
				}
			System.out.println("Registry Server: end of the list");
			System.out.println();
			} else {
				out.println("No service");
				System.out.println();
			}
		}
	}
}
