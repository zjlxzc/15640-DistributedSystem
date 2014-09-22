package registry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import exception.AccessException;
import exception.AlreadyBoundException;
import exception.RemoteException;
import server.RMIMessage;
import server.RemoteObjectRef;

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
		
		new RegistryServer(registryPort).start();	
		ServerSocket serverSoc = null;
		// The service port
		try {
			serviceHost = (InetAddress.getLocalHost()).getHostName();		
			servicePort = 12345;			
			Class<?> initialclass = Class.forName(initialClassName);
			table = new RORtbl();
			Object o = initialclass.newInstance();
			RemoteObjectRef ror = table.addObj(serviceHost, servicePort, o);			
			SimpleRegistry registry = LocateRegistry.getRegistry(registryHost, registryPort);
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
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				
				RMIMessage message = (RMIMessage)in.readObject();
				RemoteObjectRef ror = message.getRef();
				Method method = message.getMethod();
				Object o = table.findObj(ror);
				Object[] args = message.getParameters();
				Object ret = method.invoke(o,args);
				message.setResult(ret);
				
				out.writeObject(message);
				out.flush();
				
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
			}
		}		
	}
}