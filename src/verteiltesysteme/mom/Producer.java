/*
 *  Beispiel für MOM / AMQP
 *   
 *  Message Consumer, bezieht Messages von Queue aus ActiveMQ
 *  
 *  Bevor diese Klasse gestartet wird, muss zuvor ActiveMQ gestartet werden! 
 *  - z.B. apache-activemq-5.15.2-bin.zip von http://activemq.apache.org/activemq-5152-release.html herunterladen
 *  - entpacken, und im enthaltenen bin Ordner activemq.bat (sofern Windows) starten mit:
 *    - activemq start
 *    - Zugriff auf Web-Oberfläche von ActiveMQ über http://localhost:8161/admin (user: admin, password: admin ;) ) möglich
 *    
 *  Das Beispiel ActiveMQHelloWorld funktioniert auch ohne Server. 
 *  
 *  Beispiele, siehe z.B. http://activemq.apache.org/hello-world.html
 *  oder http://activemq.apache.org/getting-started.html
 */

package verteiltesysteme.mom;

//Achtung: Damit das Beispiel funktioniert, muss zuvor ActiveMQ installiert und gestartet werden! Siehe auch Kommentar oben!

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {

	static int jobNumber = 0;

	public static void main(String[] args) {
		try {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			String jobMessage;

			// ConnectionFactory erzeugen
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Connection erzeugen
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// Session erzeugen
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination erzeugen (Queue oder Topic)
			Destination destination = session.createQueue("VerteilteSysteme.TimestampJobs");

			// MessageProducer für die Session zur Destination (hier eine Queue) erzeugen
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			System.out.println("Geben Sie eine Nachricht ein (leere Zeile zum Abbrechen): ");
			jobMessage = inFromUser.readLine();

			// Erzeugt eine neue Nachricht, im Beispiel könnte dies ein Job (Arbeitsauftrag
			// an die Consumer) sein inkl. Zeitstempel etc.
			while (!jobMessage.isEmpty()) {
				jobNumber++;
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				String text = "Neuer Arbeitsauftrag (Job) (von Thread: " + Thread.currentThread().getName() + ", " +
						"Producer: " + producer.hashCode() + "), " + 
						"Job-Zeit: " + df.format(new Date(System.currentTimeMillis())) + ", " + 
						" Job-Nr: " + jobNumber + ", " + 
						"Job-Nachricht: " + jobMessage;
				TextMessage message = session.createTextMessage(text);

				// Tell the producer to send the message
				System.out.println("Nachricht gesendet! HashCode: " + message.hashCode() + " Inhalt: \""
						+ message.getText() + "\"");
				producer.send(message);

				System.out.println("\nGeben Sie eine Nachricht ein (leere Zeile zum Abbrechen): ");
				jobMessage = inFromUser.readLine();
			}

			// Clean up
			session.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
