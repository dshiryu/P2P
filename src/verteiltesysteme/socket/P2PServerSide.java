package verteiltesysteme.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class P2PServerSide extends Thread {
	
	Socket connectionSocket;

	P2PServerSide(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
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
