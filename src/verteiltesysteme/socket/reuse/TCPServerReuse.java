/* Angelehnt an Kurose/Ross Computernetzwerke (bis 5e Java, ab 6e Python) */

package verteiltesysteme.socket.reuse;

import java.io.*;
import java.net.*;

class TCPServerReuse {
	public static void main(String argv[]) throws Exception {
		int tcpPort = 36037;
		String clientSentence;
		String capitalizedSentence;

		// Server-Socket erzeugen
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket();
		// erlaubt das Wiederverwenden von Sockets, die für die individuellen Clients genutzt werden, auch wenn der Port nocht im Status "TIME_WAIT" ist
		welcomeSocket.setReuseAddress(true);
		welcomeSocket.bind(new InetSocketAddress(tcpPort));
		
		System.out.println("TCP Server started. Waiting for incoming requests... (reuseAddress="+welcomeSocket.getReuseAddress()+", timeout="+welcomeSocket.getSoTimeout() +", buffersize="+welcomeSocket.getReceiveBufferSize()+")");
		
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();

			System.out.println("Received request from client " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + " generating response...");
			
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			// Try/Catch hinzugefügt, nachdem bei Einsatz Amazon AWS (Healthcheck des ELB) clientSentence null war  
			try {
				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					capitalizedSentence = clientSentence.toUpperCase() + '\n';
					outToClient.writeBytes(capitalizedSentence);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			connectionSocket.close();
		}
	}
}
