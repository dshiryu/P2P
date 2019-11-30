package verteiltesysteme.socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class P2PNode {
	

	public static void main(String argv[]) throws Exception {
		// sets connection information
		String hostname = "localhost";
		int tcpPort = 13337;
		boolean leader = false;
		
		//sets the file information
		
		//String entrypointListFile = "/home/suess/P2P/EntryGr1.dat"; //linux
		String peerList = "EntryGr1.dat"; //windows
		List<SocketAddress> availablePeers = new ArrayList<SocketAddress>();
		String localIP = InetAddress.getLocalHost().toString().split("/")[1];
		
		
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
				P2PServerSide ss = new P2PServerSide(connectionSocket);
				ss.start();
			}
			
		} else {
			String sentence;
			String modifiedSentence;

			while(true) {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				Socket clientSocket = new Socket(hostname, tcpPort);
	
				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
				System.out.println("Enter message to sent to the server:");
				
				sentence = inFromUser.readLine();
				
				if(sentence.equals("quit")) {
					System.exit(0);
				}
				
				outToServer.writeBytes(sentence + '\n');
				modifiedSentence = inFromServer.readLine();
				System.out.println("FROM SERVER: " + modifiedSentence);
				clientSocket.close();
				
			}
			
		}
		
	}

}
