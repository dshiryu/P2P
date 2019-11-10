package verteiltesysteme.socket.multicast;

import java.net.*;

class UDPMulticastConsumer {
	public static void main(String args[]) throws Exception {
		String groupname = "224.1.1.1";
		int udpPort = 36041;
		@SuppressWarnings("resource")
		MulticastSocket mcastSocket = new MulticastSocket(udpPort);
		InetAddress IPAddress = InetAddress.getByName(groupname);
		mcastSocket.joinGroup(IPAddress);
		
		byte[] receiveData = new byte[1024];

		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			mcastSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
			System.out.println("Empfangene Multicast-Nachricht (an: " + groupname + ":" + udpPort + "): " + sentence);
		}
	}
}
