package verteiltesysteme.aws;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class TCPTimeCounterClient {

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
		String hostname = "cloudcomp-loadbalancer-1492513132.eu-central-1.elb.amazonaws.com";
		//String hostname = "52.68.46.135";
		//String hostname = "";
		int tcpPort = 36038;
		//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		long timestampStart, timestampSent, timestampEnd, delay;

		// Hostname vom Benutzer anfragen auf dem der Server läuft
		//System.out.println("Bitte geben Sie die Server-Adresse ein: ");
		//hostname = inFromUser.readLine();

		// Socket erzeugen
		Socket clientSocket = new Socket(hostname, tcpPort);

		// Zahl vom Benutzer abfragen, die dann vom Server auf den aktuellen Zählerstand
		// addiert wird
		System.out.println("Bitte geben Sie eine Zahl ein: ");
		//Long number = new Long(inFromUser.readLine());
		Long number = new Long(10);

		// Zahl an Server senden
		System.out.println(System.lineSeparator());
		timestampStart = System.currentTimeMillis();
		printFormattedTimestamp("Zeit vor Versand", timestampStart);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outToServer.writeBytes(number.toString() + '\n');

		timestampSent = System.currentTimeMillis();
		printFormattedTimestamp("Zeit nach Versand", timestampSent);

		// Antwort mit neuem Zählerstand und Timestamp vom Server empfangen
		String result = inFromServer.readLine();
		timestampEnd = System.currentTimeMillis();
		printFormattedTimestamp("Zeit nach Empfang", timestampEnd);

		// Delay als Zeit zwischen timestampSent und timestampEnd berechnen und
		// ausgeben
		delay = timestampEnd - timestampStart;
		System.out.println("Übertragungsdauer = " + delay + " ms");
		System.out.println(System.lineSeparator());

		// Antwort vom Server auswerten und ausgeben
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

		// Socket schließen
		clientSocket.close();
	}
}
