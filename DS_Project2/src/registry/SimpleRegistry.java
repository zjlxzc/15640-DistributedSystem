package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.RemoteObjectRef;
import exception.AccessException;
import exception.AlreadyBoundException;
import exception.NotBoundException;
import exception.RemoteException;

public class SimpleRegistry {

	String host;
	int port;

	public SimpleRegistry(String host, int port) {
		host = this.host;
		port = this.port;
	}

	public void bind(String serviceName, RemoteObjectRef ror)
			throws RemoteException, AlreadyBoundException, AccessException {
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
			if (ack.equals("bind success!")) {
				System.out.println("bind success!");
			} else if (ack.equals("The service already bound")){
				throw new AlreadyBoundException();
			} else {
				throw new RemoteException();
			}
			// close the socket.
			soc.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RemoteObjectRef lookup(String name) throws RemoteException,
			NotBoundException, AccessException {
		Socket soc;
		RemoteObjectRef ror = null;
		try {
			soc = new Socket(host, port);

			ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
			
			out.writeObject("Lookup");
			out.flush();
			
			String ack = (String)in.readObject();
						
			if (ack.equals("Find the service")) {
				ror = (RemoteObjectRef)in.readObject();
			} else if (ack.equals("The target service does not exist")){
				System.out.println(ack);
			} 
			soc.close();
					
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ror; 
	}
	
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
			soc.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
