package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import registry.LocateRegistry;
import registry.RegistryServer;
import registry.SimpleRegistry;
import remote.RORtbl;
import remote.RemoteObjectRef;
import exception.AccessException;
import exception.AlreadyBoundException;
import exception.RemoteException;

public class RmiImpl {

	static String serviceHost;
	static int servicePort;
	static String registryHost;
	static int registryPort;
	private static RORtbl table;
	
	public static void main(String args[]) {
		String initialClassName = args[0];
		registryHost = args[1];
		registryPort = Integer.parseInt(args[2]);
		String serviceName = args[3];
		
		System.out.println("Server		: Initialize");
		System.out.println("Server		: initialClassName : " + initialClassName);
		System.out.println("Server		: registry         : " + registryHost + " : " + registryPort);
		System.out.println("Server		: serviceName      : " + serviceName);
		
		new Thread(new RegistryServer(registryPort)).start();	
		ServerSocket serverSoc = null;
		// The service port
		try {
			serviceHost = (InetAddress.getLocalHost()).getHostName();		
			servicePort = 12345;			
			Class<?> initialclass = Class.forName(initialClassName);
						
			table = new RORtbl();
			
			Object o = initialclass.newInstance();
			
			RemoteObjectRef ror = table.addObj(serviceHost, servicePort, o);		
			
			System.out.println("Server		: request a registry from " + registryHost + " : " + registryPort);
			SimpleRegistry registry = LocateRegistry.getRegistry(registryHost, registryPort);
			
			System.out.println("Server		: bind service " + serviceName + " to registry");
			registry.bind(serviceName, ror);
			
			serverSoc = new ServerSocket(servicePort);
	
			while (true) {
				Socket client = serverSoc.accept();				
				Excution excute = new Excution(client);
				new Thread(excute).start();		
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				serverSoc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static class Excution implements Runnable {
		Socket client;		
		public Excution(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			try {
				System.out.println("Server		: get service request from " + client.getInetAddress());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				
				System.out.println("Server		: start new dispatcher ");
				Dispatcher patcher = new Dispatcher(table, in, out);
				patcher.localize();
				patcher.dispatch();				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
