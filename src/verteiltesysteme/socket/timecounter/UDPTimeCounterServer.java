package verteiltesysteme.socket.timecounter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class UDPTimeCounterServer {
	// Variable f�r Z�hlerstand des Servers, dieser Z�hler wird durch Anfragen der
	// Clients erh�ht
	private static long counter = 0;

	public static void main(String args[]) throws Exception {
		int udpPort = 36038;
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		// Socket erzeugen
		// Warnung, dass Socket nicht geschlossen wird unterdr�cken ;)
		@SuppressWarnings("resource")
		DatagramSocket serverSocket = new DatagramSocket(udpPort);
		System.out.println("UDP Time Counter Server ready... waiting for packets...");

		while (true) {
			// Anfrage vom Client empfangen
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			// Vom Client gesendete Zahl einlesen und auf aktuellen Z�hlerstand addieren
			long number = new Long(new String(receivePacket.getData(), 0, receivePacket.getLength()));
			counter += number;

			// Absender IP-Adresse und Port f�r R�ckversand (Antwort) ermitteln
			InetAddress IPAddress = receivePacket.getAddress();
			int clientSrcPort = receivePacket.getPort();
			System.out.println("Anfrage von Client " + IPAddress + ":" + clientSrcPort + " Zahl: " + number + " Z�hlerstand: " + counter);
			
			// Antwort vom Server senden
			String output = (Long.toString(counter) + " Zeit: " + System.currentTimeMillis() + " "
					+ df.format(new Date(System.currentTimeMillis())));
			System.out.println("Antwort des Servers: " + output);
			sendData = output.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientSrcPort);
			serverSocket.send(sendPacket);
		}
	}
}
