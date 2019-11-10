package verteiltesysteme.socket.multicast;

import java.io.*;
import java.net.*;

class UDPMulticastProducer {
	public static void main(String args[]) throws Exception {
		String groupname = "224.1.1.1";
		int udpPort = 36041;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		MulticastSocket mcastSocket = new MulticastSocket(udpPort);
		InetAddress IPAddress = InetAddress.getByName(groupname);
		mcastSocket.joinGroup(IPAddress);
		
		byte[] sendData = new byte[1024];

		System.out.println("Geben Sie eine Nachricht ein: ");
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);
		mcastSocket.send(sendPacket);
		mcastSocket.leaveGroup(IPAddress);
		mcastSocket.close();
	}
}
