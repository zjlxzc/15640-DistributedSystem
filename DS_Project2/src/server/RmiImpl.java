package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		
		System.out.println("Server: start");
		System.out.println("Server: initialClassName : " + initialClassName);
		System.out.println("Server: registry         : " + registryHost + " : " + registryPort);
		System.out.println("Server: serviceName      : " + serviceName);
		
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
			
			System.out.println("Server: request a registry from " + registryHost + " : " + registryPort);
			SimpleRegistry registry = LocateRegistry.getRegistry(registryHost, registryPort);
			
			registry.bind(serviceName, ror);
			System.out.println("Server: bind service " + serviceName + " to registry");
			
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
				System.out.println("Server: get service request from " + client.getInetAddress());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				
				RMIMessage message = (RMIMessage)in.readObject();
				System.out.println("Server: read in RMIMessage");
				
				RemoteObjectRef ror = message.getRef();
				System.out.println("Server: get the Remote Object Reference of " + ror.getRemote_Interface_Name());
				
				Object obj = table.findObj(ror);
				Class<?> c = obj.getClass();
				System.out.println("Server: get the local Object of " + c.getName());

				Method method = null;
				method = c.getDeclaredMethod(message.getMethodName(), message.getTypes());
				System.out.println("Server: get the method name:  " + method.getName());	
				
				Object[] args = message.getParameters();
				System.out.println("Server: get the parameters of " + method.getName());
				
				System.out.println("Server: call the method and get the result");
				Object ret = method.invoke(obj,args);
				message.setResult(ret);			
				
				out.writeObject(message);
				out.flush();
				System.out.println("Server: send back the message");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
