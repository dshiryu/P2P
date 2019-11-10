package verteiltesysteme.socket.perf;

import java.io.*;
import java.net.*;

class TCPPerfServer {
	public static void main(String argv[]) throws Exception {
		int tcpPort = 36039;
		Long clientNumberOfMegabytes;

		// Server-Socket erzeugen
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(tcpPort);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

			char[] buffer = new char[1024 * 1024];
			clientNumberOfMegabytes = new Long(inFromClient.readLine());
			//System.out.println(clientNumberOfMegabytes);
			for (long i = 0; i < clientNumberOfMegabytes; i++) {
				outToClient.write(buffer);
				//System.out.print(".");
			}
			outToClient.close();
			System.out.print("\n");
		}
	}
}
