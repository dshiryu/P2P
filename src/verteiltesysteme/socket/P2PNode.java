package verteiltesysteme.socket;

import java.io.*;
import java.net.*;

public class P2PNode implements Runnable {
	Socket connectionSocket;

	P2PNode(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public static void main(String argv[]) throws Exception {
		// sets connection information
		String hostname = "localhost";
		int tcpPort = 13337;
		boolean leader = false;
		
		// starts connection as Client, checks if it's the leader
		try (Socket usedPort = new Socket("localhost", tcpPort)) {
		
			System.out.println("There is already a leader.");
		
		} catch (IOException usedPort) {
	        
			System.out.println("There was no leader, creating server...");
	        leader = true;
	    }
		
		
		
	
		
		// starts connection as Server
		if (leader == true) {
			@SuppressWarnings("resource")
			ServerSocket welcomeSocket = new ServerSocket(tcpPort);
	
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				new Thread(new P2PNode(connectionSocket)).start();
				
			}
		} else {
			String sentence;
			String modifiedSentence;

			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket(hostname, tcpPort);

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			System.out.println("Enter message to sent to the server:");
			
			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence + '\n');
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();
		}
		
		
		
		
		
	}

	// this does the server things
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
