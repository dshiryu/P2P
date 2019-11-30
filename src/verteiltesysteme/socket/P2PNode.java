package verteiltesysteme.socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class P2PNode {
	// sets connection information
		
		static String cPort; // to save what is typed on BufferedReader 
		static String sPort;
		static int clientPort; // to save the converted string into int from BufferedReader
		static int serverPort;
		
		static boolean portAvailable = false;
		static boolean leader = false;

	public static void main(String argv[]) throws Exception {
		
		
		//sets the file information to save the peers list (IPs connected)
		//String entrypointListFile = "/home/suess/P2P/EntryGr1.dat"; //linux
		String peerList = "EntryGr1.dat"; //windows
		List<SocketAddress> availablePeers = new ArrayList<SocketAddress>();
		String localIP = InetAddress.getLocalHost().toString().split("/")[1];
		
		InetAddress inetAddress = InetAddress.getLocalHost();
		String hostname = inetAddress.getHostAddress();
		
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
		
		
		
		
		// checks if the first given port is open or not
		try {
			(new Socket(hostname, clientPort)).close();
		    portAvailable = true;
		    System.out.println("Connecting to " + clientPort);
		} catch(SocketException e) {
			System.out.println("Port " + clientPort + " isn't open");
		}
		
		
		// if the port is open, connect to it, in both cases the server connection with the second port should open (new thread?)
		
		Thread t = new Thread() {
			@Override
			public void run() {
				if(portAvailable) {
				try {
					Socket s = new Socket(hostname, clientPort);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					sendToServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			
		};
		t.start();
		 
		
		System.out.println("Opening port " + serverPort +" to external connections" );
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(serverPort);
		Socket connectionSocket = welcomeSocket.accept();
		while(true) {
			P2PServerSide ss = new P2PServerSide(connectionSocket);
			ss.start();
		}
		
		
		
		
		
		/*if (leader == true) {
			//something
			
		} else {
			
		}*/
		
	}
	
	public static void sendToServer() throws Exception {
		String sentence;
		String modifiedSentence;
		// here repeated the IP detection, tried using it as static but the methods weren't working
		InetAddress inetAddress = InetAddress.getLocalHost();
		String hostname = inetAddress.getHostAddress();

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
			//clientSocket.close();
			
		}
	}
	
	

}
