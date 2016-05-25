
/*
 * Sreenidhi Krishna
 * v01 01/28/2016
 * Source: Server.java
 * Aim: Implement Web Server
 */

import java.util.LinkedList;
import java.util.Queue;
import java.net.ServerSocket;
import java.net.Socket;

public final class Server {

	static ServerSocket socket;
	
	public static void main(String argv[]) throws Exception {
		
		String rootPath = " ";	

		 int portNumber;

		portNumber =Integer.parseInt(argv[3]);
		
		if(portNumber>=8000 && portNumber<= 9999)
		{
						
	      rootPath = argv[1];
				
		// Create a Socket
		// listen for request on that port
		socket = new ServerSocket(portNumber);
		System.out.println("Server listening on port" + portNumber);

		while (true) {

			// Accept a TCP request
			Socket connSocket = socket.accept();

			// Construct object to process HTTP request message
			Request request = new Request(connSocket, rootPath);

			// Thread created for each request
			Thread thread = new Thread(request);
			//System.out.println("Thread created for each request");

                       //start the thread
			thread.start();

		}
		}
		else
		{
			System.out.println("enter valid port number in the range  8000- 9999");
		}
	}

}
