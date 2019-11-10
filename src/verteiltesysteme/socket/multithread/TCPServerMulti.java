package verteiltesysteme.socket.multithread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServerMulti implements Runnable {
	Socket connectionSocket;

	TCPServerMulti(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public static void main(String argv[]) throws Exception {
		int tcpPort = 36037;

		// Server-Socket erzeugen
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(tcpPort);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			new Thread(new TCPServerMulti(connectionSocket)).start();
		}
	}

	public void run() {
		String clientSentence;
		String capitalizedSentence;

		BufferedReader inFromClient;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			clientSentence = inFromClient.readLine();
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
