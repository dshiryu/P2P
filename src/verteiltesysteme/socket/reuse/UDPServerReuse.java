/* Angelehnt an Kurose/Ross Computernetzwerke (bis 5e Java, ab 6e Python) */

package verteiltesysteme.socket.reuse;

import java.net.*;

class UDPServerReuse {
	public static void main(String args[]) throws Exception {
		int udpPort = 36037;
		@SuppressWarnings("resource")
		DatagramSocket datagramSocket = new DatagramSocket(null);
		// erlaubt das Wiederverwenden des Ports, mehrere Server-Prozesse können gleichen Port nutzen
		datagramSocket.setReuseAddress(true);
		datagramSocket.bind(new InetSocketAddress(udpPort));
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		System.out.println("UDP Server started. Waiting for incoming requests... (reuseAddress="+datagramSocket.getReuseAddress()+", timeout="+datagramSocket.getSoTimeout() +", buffersize="+datagramSocket.getReceiveBufferSize()+")");
		
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			datagramSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

			InetAddress IPAddress = receivePacket.getAddress();
			int clientSrcPort = receivePacket.getPort();

			System.out.println("Received request from client " + IPAddress + ":" + clientSrcPort + " generating response...");
			
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientSrcPort);
			datagramSocket.send(sendPacket);
		}
	}
}
