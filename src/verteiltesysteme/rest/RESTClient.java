/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

public class RESTClient {
	public static void main(String[] args) {
		String baseUrl = (args.length > 1) ? args[1] : "http://localhost:36040";
		String webContextPathUpper = "/touppercase";
		String webContextPathLower = "/tolowercase";

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		try {
			System.out.println("Bitte geben Sie eine Zeichenkette ein: ");
			input = inFromUser.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Nutzung des RESTful Web Service für toUpperCase()
		System.out.println("****************************");
		System.out.println("* toUpperCase              *");
		System.out.println("****************************");

		System.out.println("\nAngefragte URL: " + baseUrl + webContextPathUpper + "?input=" + input);

		Client c = ClientBuilder.newClient();
		WebTarget target = c.target(baseUrl);

		System.out.println("\nTextausgabe:");
		System.out.println(
				target.path(webContextPathUpper).queryParam("input", input).request(MediaType.TEXT_PLAIN).get(String.class));
		System.out.println("\nJSON-Ausgabe:");
		System.out.println(target.path(webContextPathUpper).queryParam("input", input).request(MediaType.APPLICATION_JSON)
				.get(String.class));
		System.out.println("\nHTML-Ausgabe:");
		System.out.println(
				target.path(webContextPathUpper).queryParam("input", input).request(MediaType.TEXT_HTML).get(String.class));

		// Nutzung des RESTful Web Service für toLowerCase()
		System.out.println("\n****************************");
		System.out.println("* toLowerCase              *");
		System.out.println("****************************");

		System.out.println("\nAngefragte URL: " + baseUrl + webContextPathLower + "?input=" + input);

		System.out.println("\nTextausgabe:");
		System.out.println(
				target.path(webContextPathLower).queryParam("input", input).request(MediaType.TEXT_PLAIN).get(String.class));
		System.out.println("\nJSON-Ausgabe:");
		System.out.println(target.path(webContextPathLower).queryParam("input", input).request(MediaType.APPLICATION_JSON)
				.get(String.class));
		System.out.println("\nHTML-Ausgabe:");
		System.out.println(
				target.path(webContextPathLower).queryParam("input", input).request(MediaType.TEXT_HTML).get(String.class));
	
	}
}