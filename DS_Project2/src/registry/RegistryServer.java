package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import server.RemoteObjectRef;

public class RegistryServer implements Runnable{
	int port;
	Hashtable<String, RemoteObjectRef> regTable;
	
	public RegistryServer(int port) {
		this.port = port;
		regTable = new Hashtable<String, RemoteObjectRef>();
	}
	
	public void run() {
		ServerSocket reg = null;
		System.out.println("Registry Server: start " + port);
		try {
			reg = new ServerSocket(port);
			while(true) {
				Socket client = reg.accept();
				System.out.println("Registry Server get Client at" + client.getInetAddress());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				
				String req = in.readLine();
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				
				
				System.out.println(req);
				if (req.equals("I want a registry")) {
					out.println("I am a simple registry.");
					System.out.println("Registry Server: send a simple registry");
				} else if (req.equals("bind")) {
					new Thread(new Bind(in, out)).start();
				} else if (req.equals("Lookup")) {
					System.out.println("Registry Server: get look up request");
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
	
	private class Bind implements Runnable {
		
		BufferedReader in;
		PrintWriter out;
		
		public Bind(BufferedReader in, PrintWriter out) {
			this.in = in;
			this.out = out;
		}
		
		@Override
		public void run() {			
			try {
				System.out.println("Registry Server: bind");
				String serviceName = in.readLine();
				String ip_adr = in.readLine();
				int port = Integer.parseInt(in.readLine());
				int obj_Key = Integer.parseInt(in.readLine());
				String remote_Interface_Name = in.readLine();
				
				RemoteObjectRef ror = new RemoteObjectRef(ip_adr, port, obj_Key, remote_Interface_Name);
				if (!regTable.containsKey(serviceName)) {
					regTable.put(serviceName, ror);
					out.println("Bind success!");
				} else {
					out.println("The service already bound");
				}								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	private class Lookup implements Runnable {
		
		Socket client;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		public Lookup(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {				
			try {
				System.out.println("Registry Server: Look up");
				
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());
				
				String serviceName = (String)in.readObject();
				System.out.println("Registry Server: get servicename" + serviceName);
				
				if (regTable.containsKey(serviceName)) {
					RemoteObjectRef ror = regTable.get(serviceName);
					out.writeObject("Find the service");
					out.writeObject(ror);	
				} else {
					out.writeObject("The target service does not exist");
				}
							
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
private class List implements Runnable {
		
		PrintWriter out;
		
		public List(PrintWriter out) {
			this.out= out;
		}
		
		@Override
		public void run() {		
			System.out.println("Registry Server: list");
			if (!regTable.isEmpty()) {
				for (String serviceName : regTable.keySet()) {
					out.println(serviceName);
				}
			} else {
				out.println("No service");
			}
		}
	}
}
