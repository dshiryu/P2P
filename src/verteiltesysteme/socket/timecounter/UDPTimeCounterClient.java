package verteiltesysteme.socket.timecounter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class UDPTimeCounterClient {

	/**
	 * Gibt einen Zeitstempel (timestamp) formatiert auf der Konsole aus. Der
	 * Zeitstempel wird in aktuellen Millisekunden seit 1.1.1970 (UTC) und als
	 * formatiertes Datum inkl. Uhrzeit ausgegeben. Der Ausgabe wird der übergebene
	 * String outputPrefix vorangestellt.
	 * 
	 * @param outputPrefix
	 *            String der als Beschreibung des Zeitpunkts vor die Ausgabe des
	 *            timestamp gestellt wird.
	 * @param timestamp
	 *            Zeitstempel basierend auf System.currentTimeMillis()
	 */
	private static void printFormattedTimestamp(String outputPrefix, long timestamp) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.out.println(outputPrefix + " = " + timestamp + " ms, " + df.format(new Date(timestamp)));
	}

	public static void main(String args[]) throws Exception {
		String hostname = "127.0.0.1";
		int udpPort = 36038;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		long timestampStart, timestampSent, timestampEnd, delay;

		// Socket erzeugen
		DatagramSocket clientSocket = new DatagramSocket();

		// Hostname vom Benutzer anfragen auf dem der Server läuft
		//System.out.println("Bitte geben Sie die Server-Adresse ein: ");
		//hostname = inFromUser.readLine();
		InetAddress IPAddress = InetAddress.getByName(hostname);

		// Zahl vom Benutzer abfragen, die dann vom Server auf den aktuellen Zählerstand
		// addiert wird
		System.out.println("Bitte geben Sie eine Zahl ein: ");
		Long number = new Long(inFromUser.readLine());
		sendData = number.toString().getBytes();

		// Zahl an Server senden
		System.out.println(System.lineSeparator());
		timestampStart = System.currentTimeMillis();
		printFormattedTimestamp("Zeit vor Versand", timestampStart);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);
		clientSocket.send(sendPacket);
		timestampSent = System.currentTimeMillis();
		printFormattedTimestamp("Zeit nach Versand", timestampSent);

		// Antwort mit neuem Zählerstand und Timestamp vom Server empfangen
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		timestampEnd = System.currentTimeMillis();
		printFormattedTimestamp("Zeit nach Empfang", timestampEnd);

		// Delay als Zeit zwischen timestampSent und timestampEnd berechnen und
		// ausgeben
		delay = timestampEnd - timestampStart;
		System.out.println("Übertragungsdauer = " + delay + " ms");
		System.out.println(System.lineSeparator());

		// Antwort vom Server auswerten und ausgeben
		String result = new String(receivePacket.getData());
		System.out.println("Ausgabe vom Server:" + System.lineSeparator() + result);
		// Vom Server übergebenen Zeitstempel aus der Antwort herausschneiden
		//
		// Format Ausgabe vom Server "<Zählerstand> Zeit: <Zeitstempel> <Datum/Uhrzeit>"
		// Beispiel: 60 Zeit: 1510735274021 15.11.2017 08:41:14
		long timestampServer = new Long(result.split(" ")[2]).longValue();
		// One-Way-Delay (Bitverzögerung) aus Rount-Trip-Time (RTT) ermitteln
		float oneWayDelayEstimate = (timestampEnd - timestampSent) / 2;
		// Zeitabweichung zwischen Client und Server ermitteln (Zeitstempel vom Server
		// minus Zeitstempel bei Versand am Client minus One-Way-Delay)
		float timestampSkew = timestampServer - timestampSent - oneWayDelayEstimate;
		System.out.println("Zeitversatz zwischen Server und Client ca. = " + timestampSkew + " ms");
		System.out.println(System.lineSeparator());
		
		
		
		long seconds = number;
		printFormattedTimestamp("Starting time ", System.currentTimeMillis());
		while(true) {
			long timeNow = System.currentTimeMillis();
			long reference = number * 1000;
			
			Thread.sleep(number);
			
			if(timeNow % reference == 0) {
				printFormattedTimestamp("Time after " + seconds + " second(s)", timeNow);
				seconds += number;
			}
			
			if(timeNow - timestampStart > 1000000) break;
			
		}
		
		
		
		
		

		// Socket schließen
		clientSocket.close();
	}
}
