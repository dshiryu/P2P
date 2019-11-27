package verteiltesysteme.socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class P2PNode {
	// sets connection information
		static String hostname = "localhost";
		static String cPort; // to save what is typed on BufferedReader 
		static String sPort;
		static int clientPort; // to save the converted string into int from BufferedReader
		static int serverPort;
		
		static boolean leader = false;

	public static void main(String argv[]) throws Exception {
		
		
		//sets the file information to save the peers list (IPs connected)
		//String entrypointListFile = "/home/suess/P2P/EntryGr1.dat"; //linux
		String peerList = "EntryGr1.dat"; //windows
		List<SocketAddress> availablePeers = new ArrayList<SocketAddress>();
		String localIP = InetAddress.getLocalHost().toString().split("/")[1];
		
		// port to connect to
		System.out.println("Connect to port:");
		BufferedReader clientBR = new BufferedReader(new InputStreamReader(System.in));
		cPort = clientBR.readLine();
		clientPort = Integer.parseInt(cPort);
				
		// open a port to receive connections
		System.out.println("Open port:");
		BufferedReader serverBR = new BufferedReader(new InputStreamReader(System.in));
		sPort = clientBR.readLine();
		serverPort = Integer.parseInt(sPort);
		
		// starts connection as Client, checks if it's the leader
		try (Socket usedPort = new Socket("localhost", clientPort)) {
			System.out.println("Connecting to given port");
			
		
		} catch (IOException usedPort) { 
	        System.out.println("Opening server port");
	        //leader = true;
	        createServer();
	    }
		
		sendToServer();
		
		/*if (leader == true) {
			//something
			
		} else {
			
		}*/
		
	}
	
	public static void createServer() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(serverPort);
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			P2PServerSide ss = new P2PServerSide(connectionSocket);
			Thread thread = new Thread();
			ss.start();
			thread.start();
		}
	}
	
	public static void sendToServer() throws Exception {
		String sentence;
		String modifiedSentence;

		while(true) {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket(hostname, serverPort);

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
