/* Angelehnt an Kurose/Ross Computernetzwerke (bis 5e Java, ab 6e Python) */

package verteiltesysteme.socket.simple;

import java.net.*;

class UDPServer {
	public static void main(String args[]) throws Exception {
		int udpPort = 36037;
		@SuppressWarnings("resource")
		DatagramSocket serverSocket = new DatagramSocket(udpPort);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		System.out.println("UDP Server started. Waiting for incoming requests...");
		
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

			InetAddress IPAddress = receivePacket.getAddress();
			int clientSrcPort = receivePacket.getPort();

			System.out.println("Received request from client " + IPAddress + ":" + clientSrcPort + " generating response...");
			
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientSrcPort);
			serverSocket.send(sendPacket);
		}
	}
}
