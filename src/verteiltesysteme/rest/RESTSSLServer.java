/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.rest;

import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RESTSSLServer
{
    private static final String KEYSTORE_SERVER_FILE = "./build/keystore_server";
    private static final String KEYSTORE_SERVER_PWD = "notsecure";
    
	public static void main( String[] args ) throws IOException, InterruptedException 
   {
      String baseUrl = ( args.length > 0 ) ? args[0] : "http://localhost:36040";

      // Grizzly ssl configuration
      SSLContextConfigurator sslContext = new SSLContextConfigurator();

      // set up security context
      sslContext.setKeyStoreFile(KEYSTORE_SERVER_FILE); // contains server keypair
      sslContext.setKeyStorePass(KEYSTORE_SERVER_PWD);
      
      // keytool -genkey -keystore ./keystore_server -alias serverKey -dname "CN=localhost, OU=VertSys, O=HS-Fulda, L=Fulda, ST=Hessen, C=DE"
      
      final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
            URI.create( baseUrl ), new ResourceConfig( RESTToUpperCaseService.class, RESTToLowerCaseService.class ), true, new SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(false) );
      Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
         @Override
         public void run() {
            server.shutdownNow();
         }
      } ) );
      server.start();

      System.out.println("Grizzly-HTTP-Server gestartet");
      System.out.println("Stoppen des Grizzly-HTTP-Servers mit: Strg+C\n");
      System.out.println("RESTful Web Service URL: " + baseUrl + RESTToUpperCaseService.webContextPath);
      System.out.println("RESTful Web Service URL: " + baseUrl + RESTToLowerCaseService.webContextPath);

      Thread.currentThread().join();
   }
}