/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

import org.glassfish.grizzly.http.server.ErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class TCPTimeCounterRESTServer {
	public static void main(String[] args) throws IOException, InterruptedException {
		String baseUrl = (args.length > 0) ? args[0] : "http://0.0.0.0:36042";

		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl),
				new ResourceConfig(TCPTimeCounterRESTService.class), false);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				server.shutdownNow();
			}
		}));

		ErrorPageGenerator epg = new ErrorPageGenerator() {
			@Override
			public String generate(Request request, int status, String reasonPhrase, String description,
					Throwable exception) {
				StringBuilder sb = new StringBuilder();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				exception.printStackTrace(ps);
				ps.close();
				sb.append(new String(baos.toByteArray()));
				System.out.println(sb.toString());
				return sb.toString();
			}
		};
		server.getServerConfiguration().setDefaultErrorPageGenerator(epg);

		server.start();

		System.out.println("Grizzly-HTTP-Server gestartet");
		System.out.println("Stoppen des Grizzly-HTTP-Servers mit: Strg+C\n");
		System.out.println("RESTful Web Service URL: " + baseUrl + TCPTimeCounterRESTService.webContextPath);

		Thread.currentThread().join();
	}
}