package verteiltesysteme.socket.multithread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPServerMulti implements Runnable {
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	
	UDPServerMulti(DatagramSocket serverSocket, DatagramPacket receivePacket) {
		this.receivePacket = receivePacket;
		this.serverSocket = serverSocket;
	}

	public static void main(String argv[]) throws Exception {
		int udpPort = 36037;

		DatagramSocket serverSocket = new DatagramSocket(udpPort);
		byte[] receiveData = new byte[1024];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			// IST DAS SINNVOLL? Unter welchen Bedingungen? ;-)
			new Thread(new UDPServerMulti(serverSocket, receivePacket)).start();
		}
	}

	public void run() {
		byte[] sendData = new byte[1024];
		String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

		InetAddress IPAddress = receivePacket.getAddress();
		int clientSrcPort = receivePacket.getPort();

		String capitalizedSentence = sentence.toUpperCase();
		sendData = capitalizedSentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientSrcPort);
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
