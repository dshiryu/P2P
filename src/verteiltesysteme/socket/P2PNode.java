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
		
		static boolean clientPortAvailable = false;
		static boolean serverPortAvailable = false;
		static boolean leader = false;
		
		//InetAddress inetAddress = InetAddress.getLocalHost();
		static String hostname;// = inetAddress.getHostAddress();

	public static void main(String argv[]) throws Exception {
		
		
		//sets the file information to save the peers list (IPs connected)
		//String entrypointListFile = "/home/suess/P2P/EntryGr1.dat"; //linux
		String peerList = "EntryGr1.dat"; //windows
		List<SocketAddress> availablePeers = new ArrayList<SocketAddress>();
		String givenIP = InetAddress.getLocalHost().toString().split("/")[1];
		
		
		// sets hostname as the IP address typed
		while(!clientPortAvailable) {
			// uses already set configuration or set an IP
			System.out.println("Local test (y/n)?");
			BufferedReader answerIP = new BufferedReader(new InputStreamReader(System.in)); // gets input
			String aIP = answerIP.readLine();
			
			if (aIP.toLowerCase().equals("y")) {
				System.out.println("Local test confirmed. \n");
				InetAddress inetAddress = InetAddress.getLocalHost();
				hostname = inetAddress.getHostAddress();
				
			} else if (aIP.toLowerCase().equals("n")) {
				// gets IP
				System.out.println("External connection, type the destination IP:");
				BufferedReader clientIP = new BufferedReader(new InputStreamReader(System.in)); // gets input
				hostname = clientIP.readLine();
				
			} else {
				System.out.println("Command not recognized, try again.");
			}
			
			
			
			
			
			// port to connect to
			System.out.println("Connect to port# as client:");
			BufferedReader clientBR = new BufferedReader(new InputStreamReader(System.in)); // gets input
			cPort = clientBR.readLine(); 
			System.out.println("Checking availability...");
			
			try { // checks if it's a int of string
				clientPort = Integer.parseInt(cPort); // convert to int
				
				try { // checks if the first given port is open or not
					(new Socket(hostname, clientPort)).close();
					clientPortAvailable = true;
				    System.out.println("Connecting to " + clientPort + ". \n");
				    
				} catch(SocketException e) { // if the connection wasn't successful, checks if the user wants to try again
					System.out.println("Port " + clientPort + " isn't responding. Do you want to try another (y/n)?");
					
					BufferedReader clientPortTryAgain = new BufferedReader(new InputStreamReader(System.in));
					String clientPortAnswer = clientPortTryAgain.readLine();
					
					if (clientPortAnswer.toLowerCase().equals("n")) {
						System.out.println("Giving up... \n");
						
						
						
						// TO-DO: check if there's the leader
						
						/*if (leader == true) {
						//something
						
						} else {
						
						}*/
						
						
						
						break;
					} else if (clientPortAnswer.toLowerCase().equals("y")) {
						System.out.print("Type again. \n\n");
					} else {
						System.out.println("Command not recognized, try again.");
					}
				} 
			} catch (NumberFormatException e) {
				System.out.println("Wrong input, please try a port number. \n");
		    }
		}
			
		// gets input for server port number and checks if it is available and if it was typed correctly
		while(!serverPortAvailable) {
			System.out.println("Insert which port# should be opened as a server:");
			BufferedReader serverBR = new BufferedReader(new InputStreamReader(System.in));
			sPort = serverBR.readLine();
			
			try {
				serverPort = Integer.parseInt(sPort);
				
				try {
					(new ServerSocket(serverPort)).close();
					serverPortAvailable = true;
					
				} catch(SocketException e) {
					System.out.println("Port " + serverPort + " is already being used, try another.\n");
					
				}
			} catch (NumberFormatException e) {
				System.out.println("Wrong input, please try a port number.");
				
		    }
		}
		
		// new thread to connect to the client port
		
		if(clientPortAvailable) {
			Thread t = new Thread() {
				
				@Override
				public void run() {
					
					while(true) {
						
						try {
							@SuppressWarnings({ "resource", "unused" })
							Socket s = new Socket(hostname, clientPort); // starts connection
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						try { // starts sequence that sends requests to the server
							sendToServer();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// yields for a short time so the server can also work on the response, otherwise the code stops here
						// and no further responses are given
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
		t.start(); // without this the thread won't do anything
		}
		 
		// Starting the server side
		System.out.println("Opening port# " + serverPort +". Waiting for external input... \n");
				
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(serverPort); // starts the server socket
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept(); // makes the server receive requests
			P2PServerSide ss = new P2PServerSide(connectionSocket); // instance the server response into a new thread
			ss.start();
		}
	}
	
	public static void sendToServer() throws Exception {
		
		// here repeated the IP detection, tried using it as static but the methods weren't working
		InetAddress inetAddress = InetAddress.getLocalHost();
		String hostname = inetAddress.getHostAddress();

		// connects to the given socket
		Socket clientSocket = new Socket(hostname, serverPort);
		
		// variables to store user input
		String sentence;
		String modifiedSentence;
		
		// gets input
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		// sends data to server and get its response
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		System.out.println("Enter message to sent to the server:");
		sentence = inFromUser.readLine();
		outToServer.writeBytes(sentence + '\n');
		
		// prints answer from the server
		modifiedSentence = inFromServer.readLine();
		System.out.println("FROM SERVER: " + modifiedSentence);
		
		if(sentence.equals("quit")) {
			System.out.println("Connection closed.");
			System.exit(0);
			
		}
		//clientSocket.close(); // this was on the original code from socket.multithread, but there it breaks everything
	}

}
