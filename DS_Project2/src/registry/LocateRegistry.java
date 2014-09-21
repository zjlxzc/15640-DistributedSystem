package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * The locateRegistry provides a static method for client and server to 
 * get the Registry
 *
 */

public class LocateRegistry {
	public static SimpleRegistry getRegistry(String host, int port) {
		// open socket.
		Socket soc = null;
		try {
			soc = new Socket(host, port);
			// get TCP streams and wrap them.
			BufferedReader in = new BufferedReader(new InputStreamReader(
					soc.getInputStream()));
			PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

			// ask.
			out.println("who are you?");

			// gets answer.
			if ((in.readLine()).equals("I am a simple registry.")) {
				return new SimpleRegistry(host, port);
			} else {
				System.out.println("somebody is there but not a registry!");
				return null;
			}
		} catch (Exception e) {
			System.out.println("nobody is there!" + e);
			return null;
		} finally {
			try {
				soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
