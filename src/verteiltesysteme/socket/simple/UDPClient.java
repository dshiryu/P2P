/* Angelehnt an Kurose/Ross Computernetzwerke (bis 5e Java, ab 6e Python) */

package verteiltesysteme.socket.simple;

import java.io.*;
import java.net.*;

class UDPClient {
	public static void main(String args[]) throws Exception {
		String hostname = "localhost";
		int udpPort = 36037;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(hostname);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		System.out.println("Enter message to sent to the server:");
		
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);

		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("FROM UDP SERVER:" + modifiedSentence);
		clientSocket.close();
	}
}
