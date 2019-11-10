package verteiltesysteme.aws;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

class TCPTimeCounterServer {
	public static void main(String args[]) throws Exception {
		int tcpPort = 36038;
		String bucketName = "vertsys-counter";
		String bucketRegion = "eu-central-1";
		//String redisClusterURL = "vertsys-ec1-0001-002.71rxr9.0001.euc1.cache.amazonaws.com";
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		// Socket erzeugen
		// Warnung, dass Socket nicht geschlossen wird unterdrücken ;)
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(tcpPort);
		System.out.println("TCP Time Counter Server ready... waiting for packets...");

		while (true) {
			// Auf Anfrage vom Client warten
			Socket connectionSocket = serverSocket.accept();

			// Anfrage vom Client empfangen
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			String input = null;

			// Vom Client gesendete Zahl einlesen und auf aktuellen Zählerstand addieren
			long number;
			try {
				input = inFromClient.readLine();
			} catch (IOException ie) {
				// TODO Auto-generated catch block
				ie.printStackTrace();
			}

			if (input != null) {
				number = new Long(input);

				// Verbindung zum Redis Cluster (Amazon ElastiCache)
				//JedisPool pool = new JedisPool(new JedisPoolConfig(), redisClusterURL);
				//Jedis jedis = pool.getResource();
				//jedis.connect();
				//JedisCluster jedisCluster = new JedisCluster(new HostAndPort(redisClusterURL, 6379));
				//System.out.println("Connected to redis server");
	            // check whether server is running or not
	            //System.out.println("redis server is running: " + jedis.ping());
				//System.out.println("redis server cluster info: " + jedis.clusterInfo());
				// Variable für Zählerstand des Servers, dieser Zähler wird durch Anfragen der
				// Clients erhöht
				//jedisCluster.incrBy("vertsys-counter", number);
				//long counter = new Long(jedisCluster.get("vertsys-counter"));
				//jedisCluster.close();
				//System.out.println("TTL:" + jedisCluster.ttl("vertsys-counter"));

				//Verbindung zu S3
				AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
				if (!s3Client.doesObjectExist("vertsys-counter", bucketRegion))
				{
					s3Client.putObject(bucketName, bucketRegion, new Long(0).toString());
				}
				long counter = new Long(s3Client.getObjectAsString(bucketName, bucketRegion));
				counter = counter + number;
				s3Client.putObject(bucketName, bucketRegion, new Long(counter).toString());

				System.out.println("Request from Client " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + " Zahl: " + number + " Zählerstand: " + counter);
				
				// Try/Catch hinzugefügt, nachdem bei Einsatz Amazon AWS (Healthcheck des ELB) clientSentence null war  
				try {
					// Antwort vom Server senden
					String output = (Long.toString(counter) + " Zeit: " + System.currentTimeMillis() + " "
							+ df.format(new Date(System.currentTimeMillis())) + " " + InetAddress.getLocalHost());
					System.out.println("Reply from Server: " + output);
					// Zeilenumbruch anfügen, da Client mit readLine auf komplette Zeile wartet
					outToClient.writeBytes(output + '\n');
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}		
			
			}
			connectionSocket.close();
			
		}
	}
}
