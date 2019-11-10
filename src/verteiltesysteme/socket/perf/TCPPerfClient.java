package verteiltesysteme.socket.perf;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class TCPPerfClient {
	public static void main(String argv[]) throws Exception {
		String hostname = "";
		int tcpPort = 36039;
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Bitte geben Sie die Server-Adresse ein: ");
		hostname = inFromUser.readLine();
		System.out.println("Bitte geben Sie die Anzahl zu übertragender Megabytes ein: ");
		Long number = new Long(inFromUser.readLine());
		
		Socket clientSocket = new Socket(hostname, tcpPort);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		long timestampStart = System.currentTimeMillis();
		System.out.println("Zeit vor Versand = " + timestampStart + " ms, " + df.format(new Date(timestampStart)));
		outToServer.writeBytes(number.toString() + "\n");

		char[] buffer = new char[1024 * 1024];
		long totalBytesReceived = 0;
		int charsRead = inFromServer.read(buffer);
		while (charsRead != -1)
		{
			totalBytesReceived += charsRead;
			charsRead = inFromServer.read(buffer);
		}
	
		long timestampEnd = System.currentTimeMillis();
		System.out.println("Zeit nach Empfang = " + timestampEnd + " ms, " + df.format(new Date(timestampEnd)));
		long duration = timestampEnd - timestampStart;
		System.out.println("Delay = " + duration + " ms");
		double seconds = (duration / 1000.0);
		double throughput = (totalBytesReceived / seconds);
		System.out.println("Rate = " + (long)throughput + " B/s");
		clientSocket.close();
	}
}
