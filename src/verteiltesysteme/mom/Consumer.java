/*
 *  Beispiel f�r MOM / AMQP
 *   
 *  Message Consumer, bezieht Messages von Queue aus ActiveMQ
 *  
 *  Bevor diese Klasse gestartet wird, muss zuvor ActiveMQ gestartet werden! 
 *  - z.B. apache-activemq-5.15.2-bin.zip von http://activemq.apache.org/activemq-5152-release.html herunterladen
 *  - entpacken, und im enthaltenen bin Ordner activemq.bat (sofern Windows) starten mit:
 *    - activemq start
 *    - Zugriff auf Web-Oberfl�che von ActiveMQ �ber http://localhost:8161/admin (user: admin, password: admin ;) ) m�glich
 *    
 *  Das Beispiel ActiveMQHelloWorld funktioniert auch ohne Server. 
 *  
 *  Beispiele, siehe z.B. http://activemq.apache.org/hello-world.html
 *  oder http://activemq.apache.org/getting-started.html
 */

package verteiltesysteme.mom;

//Achtung: Damit das Beispiel funktioniert, muss zuvor ActiveMQ installiert und gestartet werden! Siehe auch Kommentar oben!

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {

	public static void main(String[] args) {
		try {
			// ConnectionFactory erzeugen
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// Connection erzeugen
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// Session erzeugen
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Destination erzeugen (Queue oder Topic)
			Destination destination = session.createQueue("VerteilteSysteme.TimestampJobs");

			// MessageConsumer f�r die Session zur Destination (hier eine Queue) erzeugen
			MessageConsumer consumer = session.createConsumer(destination);

			// max. 10 Sekunden auf Nachricht warten
			Message message = consumer.receive(10000);

			// Einzene Naxchricht als TextMessage aus der Queue nehmen, sofern verf�gbar
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				System.out.println("Empfangene Nachricht: " + text);
			} else {
				if (message == null)
				{
					System.out.println("Queue leer - keine Nachricht vorhanden (" + message + ")");
				}
				else
				{
					System.out.println("Empfangene Nachricht: " + message);
				}
			}

			consumer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
