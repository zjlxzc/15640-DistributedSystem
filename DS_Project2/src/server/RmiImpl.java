/**
 * File name: RmiImpl.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is primary part on server.
 */

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
		if (args.length != 4) {
			System.out.println("Invalid Arguments, please try again");
			Usage();
			System.exit(1);
		}
		
		System.out.println("Server		: Initialize");
		System.out.println("Server		: InitialClassName : " + args[0]);
		System.out.println("Server		: Registry         : " + args[1] + " : " + args[2]);
		System.out.println("Server		: ServiceName      : " + args[3]);
		
		String initialClassName = args[0];
		registryHost = args[1];
		try {
			registryPort = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("Server initialization failed: port number is not valid");
		}
		String serviceName = args[3];
		
		new Thread(new RegistryServer(registryPort)).start();	
		ServerSocket serverSoc = null;
		// The service port
		try {
			serviceHost = (InetAddress.getLocalHost()).getHostName();
		} catch (UnknownHostException e) {
			System.out.println("Server initialization failed : Unknown server host");
		}		
		servicePort = 12345;			
		Class<?> initialclass = null;
		try {
			initialclass = Class.forName(initialClassName);
		} catch (ClassNotFoundException e1) {
			System.out.println("Server initialization failed : Unknown initialClassName");
		}
					
		table = new RORtbl();
		
		Object o = null;
		try {
			o = initialclass.newInstance();
		} catch (InstantiationException e1) {
			System.out.println("Server initialization failed : Unable to instantiate the initial class");
		} catch (IllegalAccessException e1) {
			System.out.println("Server initialization failed : Illegal Access");
		}
		
		RemoteObjectRef ror = table.addObj(serviceHost, servicePort, o); // get remote object reference	
		
		System.out.println("Server		: request a registry from " + registryHost + " : " + registryPort);
		SimpleRegistry registry = LocateRegistry.getRegistry(registryHost, registryPort); // get registry
		
		System.out.println("Server		: bind service " + serviceName + " to registry");
		try {
			registry.bind(serviceName, ror); // bind service
		} catch (RemoteException e1) {
			System.out.println("Server initialization failed : Remote Exception");
		} catch (AlreadyBoundException e1) {
			System.out.println("Server initialization failed : The service has already bound");
		} catch (AccessException e1) {
			System.out.println("Server initialization failed : AccessException");
		}
		
		try {
			serverSoc = new ServerSocket(servicePort);
		} catch (IOException e1) {
			System.out.println("Server initialization failed : Server socket failed");
		}

		while (true) {
			Socket client = null;
			try {
				client = serverSoc.accept();
			} catch (IOException e) {
				System.out.println("Get client failed : I/O failed");
			}
			Excution excute = new Excution(client);
			new Thread(excute).start();		
		}
	}
	
	public static void Usage() {
		System.out.println("RMI Implementation Sever");
		System.out.println("Usage: java - jar RMI_Server.jar InitialClassName registryHost registryPort ServiceName");
		System.out.println();
	}

	private static class Excution implements Runnable {
		Socket client;		
		public Excution(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			System.out.println("Server		: get service request from " + client.getInetAddress());
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			try {
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());
			} catch (IOException e) {
				System.out.println("Server Excution Failed : Stream failed");
			}
			System.out.println("Server		: start new dispatcher");
			Dispatcher patcher = new Dispatcher(table, in, out);
			patcher.localize();
			patcher.dispatch();								
		}		
	}
}
