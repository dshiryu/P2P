package verteiltesysteme.socket.timecounter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPTimeCounterClientPing {
	public static void main(String args[]) throws Exception {
		String hostname = "";
		int udpPort = 36038;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		// Socket erzeugen
		DatagramSocket clientSocket = new DatagramSocket();

		// Hostname vom Benutzer anfragen auf dem der Server läuft
		System.out.println("Bitte geben Sie die Server-Adresse ein: ");
		hostname = inFromUser.readLine();
		InetAddress IPAddress = InetAddress.getByName(hostname);

		// Gewünschte Anzahl von Pings vom Benutzer abfragen
		System.out.println("Bitte geben Sie die Anzahl gewünschter Pings ein: ");
		Long pingCount = new Long(inFromUser.readLine());
		// bei jedem Ping wird Zählerstand auf dem Server um 1 erhöht
		Long number = new Long(1);
		sendData = number.toString().getBytes();

		// Paket für Empfang erzeugen
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		long average = 0; // Variable zur Ermittlung des durchschnittlichen Delays

		// Pings ausführen
		for (long i = 0; i < pingCount; i++) {
			// Anfrage an Server senden
			long timestampStart = System.currentTimeMillis();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);
			clientSocket.send(sendPacket);

			// Antwort von Server empfangen
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			// Delay aus Zeit zwischen Zeitstempeln vor Versand und nach Empfang berechnen
			long timestampEnd = System.currentTimeMillis();
			long delay = timestampEnd - timestampStart;
			// Delay auf Variable für Ermittlung des durchschnittlichen Delays addieren
			average += delay;
			System.out.println("Delay = " + delay + " ms");

			// 1 Sekunde warten
			Thread.sleep(1000);
		}

		// Durchschnitt berechnen und ausgeben
		System.out.println("Durchschnitt: " + (average / (float) pingCount) + " ms");
		System.out.println(System.lineSeparator());

		// Antwort vom Server ausgeben 
		String result = new String(receivePacket.getData());
		System.out.println("Letzte Ausgabe vom Server:" + System.lineSeparator() + result);
		System.out.println(System.lineSeparator());

		// Socket schließen
		clientSocket.close();
	}
}
