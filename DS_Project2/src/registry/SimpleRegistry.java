package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.RemoteObjectRef;
import exception.AccessException;
import exception.AlreadyBoundException;
import exception.NotBoundException;
import exception.RemoteException;

public class SimpleRegistry implements Registry {

	String host;
	int port;

	public SimpleRegistry(String host, int port) {
		host = this.host;
		port = this.port;
	}

	@Override
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

			// it is a rebind request, with a service name and ROR.
			out.println("rebind");
			out.println(serviceName);
			out.println(ror.IP_adr);
			out.println(ror.Port);
			out.println(ror.Obj_Key);
			out.println(ror.Remote_Interface_Name);

			// it also gets an ack, but this is not used.
			String ack = in.readLine();

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

	@Override
	public Remote lookup(String name) throws RemoteException,
			NotBoundException, AccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
