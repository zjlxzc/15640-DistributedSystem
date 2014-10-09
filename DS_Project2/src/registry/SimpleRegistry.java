/**
 * File name: SimpleRegistry.java
 * @author Chun Xu (chunx), Jialing Zhou (jialingz)
 * Course/Section: 15640/A
 * 
 * Description: Lab 2: RMI
 * 
 * This class is to provide a static method for client and server to get the Registry.
 */

package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import remote.RemoteObjectRef;
import exception.AlreadyBoundException;
import exception.NotBoundException;
import exception.RemoteException;

public class SimpleRegistry {

	String host;
	int port;

	public SimpleRegistry(String host, int port) {
		this.host = host;
		this.port = port;
	}

	// bind service
	public void bind(String serviceName, RemoteObjectRef ror)
			throws AlreadyBoundException {
		// open socket. same as before.
		Socket soc;
		try {
			soc = new Socket(host, port);

			// get TCP streams and wrap them.
			BufferedReader in = new BufferedReader(new InputStreamReader(
					soc.getInputStream()));
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

			// it is a bind request, with a service name and ROR.
			out.println("bind");
			out.println(serviceName);
			out.println(ror.ip_adr);
			out.println(ror.port);
			out.println(ror.obj_key);
			out.println(ror.remote_Interface_Name);

			// it also gets an ack, but this is not used.
			String ack = in.readLine();
			if (ack.equals("Bind success!")) {
				//System.out.println("Registry : bind success");
			} else if (ack.equals("The service already bound")){
				throw new AlreadyBoundException();
			} 
			// close the socket.
			soc.close();
		} catch (UnknownHostException e) {
			System.out.println("Registry error : cannot reach the server");
		} catch (IOException e) {
			System.out.println("Registry error : IOException");
		}
	}

	// lookup service
	public RemoteObjectRef lookup(String name) throws NotBoundException {
		Socket soc;
		RemoteObjectRef ror = null;
		try {
			soc = new Socket(host, port);
			PrintWriter strOut = new PrintWriter(soc.getOutputStream(), true);
			strOut.println("Lookup");
			
			// get input and output stream
			ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
		
			out.writeObject(name);
			String ack = (String)in.readObject();
						
			if (ack.equals("Find the service")) {
				System.out.println("Registry   : found the service: \"" + name + "\"");
				ror = (RemoteObjectRef)in.readObject();
			} else if (ack.equals("The target service does not exist")){
				System.out.println(ack);
				throw new NotBoundException();				
			} 
			soc.close();					
		} catch (UnknownHostException e) {
			System.out.println("Registry error : cannot reach the server");
		} catch (IOException e) {
			System.out.println("Registry error : IOException");
		} catch (ClassNotFoundException e) {
			System.out.println("Registry error : return value error");
		}
		return ror; 
	}
	
	// rebind service
	public void rebind(String serviceName, RemoteObjectRef ror)
			throws RemoteException, AlreadyBoundException {
		// open socket. same as before.
		Socket soc;
		try {
			soc = new Socket(host, port);

			// get TCP streams and wrap them.
			BufferedReader in = new BufferedReader(new InputStreamReader(
					soc.getInputStream()));
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

			// it is a bind request, with a service name and ROR.
			out.println("rebind");
			out.println(serviceName);
			out.println(ror.ip_adr);
			out.println(ror.port);
			out.println(ror.obj_key);
			out.println(ror.remote_Interface_Name);

			// it also gets an ack, but this is not used.
			String ack = in.readLine();
			if (ack.equals("Rebind success!")) {
				System.out.println("Registry :rebind success");
			} else {
				throw new RemoteException();
			}
			// close the socket.
			soc.close();
		} catch (UnknownHostException e) {
			System.out.println("Registry error : cannot reach the server");
		} catch (IOException e) {
			System.out.println("Registry error : IOException");
		}
	}
	
	// unbind service
	public void unbind(String serviceName)
			throws NotBoundException {
		// open socket. same as before.
		Socket soc;
		try {
			soc = new Socket(host, port);

			// get TCP streams and wrap them.
			BufferedReader in = new BufferedReader(new InputStreamReader(
					soc.getInputStream()));
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

			// it is a bind request, with a service name and ROR.
			out.println("unbind");
			out.println(serviceName);

			// it also gets an ack, but this is not used.
			String ack = in.readLine();
			if (ack.equals("Unbind success!")) {
				System.out.println("Registry : unbind success");
			} else if (ack.equals("The service has not been bound")){
				throw new NotBoundException();
			}
			// close the socket.
			soc.close();
		} catch (UnknownHostException e) {
			System.out.println("Registry error : cannot reach the server");
		} catch (IOException e) {
			System.out.println("Registry error : IOException");
		}
	}
	
	// list all service
	public void list() throws RemoteException {
		
		Socket soc;	
		try {
			soc = new Socket(host, port);

			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
			
			out.println("List");
			
			String line = "";
			while((line = in.readLine()) != null) {
				System.out.println(line);
			}
			soc.close(); // close socket
		} catch (UnknownHostException e) {
			System.out.println("Registry error : cannot reach the server");
		} catch (IOException e) {
			System.out.println("Registry error : IOException");
		}
	}
}
