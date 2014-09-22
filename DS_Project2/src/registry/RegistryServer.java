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

public class RegistryServer{
	int port;
	Hashtable<String, RemoteObjectRef> regTable;
	
	public RegistryServer(int port) {
		this.port = port;
		regTable = new Hashtable<String, RemoteObjectRef>();
	}
	
	public void start() {
		ServerSocket reg = null;
		try {
			reg = new ServerSocket(port);
			while(true) {
				Socket client = reg.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				
				if ((in.readLine()).equals("I want a registry")) {
					out.println("I am a simple registry.");
				} else if (in.readLine().equals("bind")) {
					new Thread(new Bind(in, out)).start();
				} else if (in.readLine().equals("Look up")) {
					new Thread(new Lookup(client)).start();
				} else if (in.readLine().equals("List")) {
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
				in = new ObjectInputStream(client.getInputStream());
				out = new ObjectOutputStream(client.getOutputStream());
				String serviceName = (String)in.readObject();
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
