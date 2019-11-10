/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.aws;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Path(TCPTimeCounterRESTService.webContextPath)
public class TCPTimeCounterRESTService {
	static final String webContextPath = "/counter";

	static final String bucketName = "vertsys-counter";
	static final String bucketRegion = "eu-central-1";

	private Long getCounter() {
		// Verbindung zu S3
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(bucketRegion).build();
		return new Long(s3Client.getObjectAsString(bucketName, bucketRegion));
	}

	private boolean setCounter(Long counter) {
		// Verbindung zu S3
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(bucketRegion).build();
		s3Client.putObject(bucketName, bucketRegion, new Long(counter).toString());
		return true;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getCounterPlain() {
		return "Plain-Text: " + getCounter();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getCounterHtml() {
		return "<html><head><title>TCPTimeCounter REST Service</title>" +
				"<meta http-equiv=\"refresh\" content=\"5\"/></head><body>" +
				"<h2>HS Fulda - TCPTimeCounter REST Service</h2>"+
				"<p><b>HTML-Output:</b> " + getCounter() + "</p></body>"+
				"<form method=POST action=\"/counter\">" +
				"<input type=\"hidden\" name=\"input\" value=\"1\">"+
				"<input type=\"submit\" value=\"Increment\"></form></body></html>";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getCounterJson() {
		return "{\n  \"type\": \"JSON\",\n  \"output\": \"" + getCounter() + "\"\n}";
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String incrCounterPlain(@FormParam("input") String input) {
		Long currentCounterValue = getCounter();
		currentCounterValue = currentCounterValue + (new Long(input)).longValue();
		setCounter(currentCounterValue);
		return "Plain-Text: TCPTimeCounter counter increased by " + input + " to " + currentCounterValue;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String incrCounterHtml(@FormParam("input") String input) {
		Long currentCounterValue = getCounter();
		currentCounterValue = currentCounterValue + (new Long(input)).longValue();
		setCounter(currentCounterValue);
		return "<html><head><title>TCPTimeCounter REST Service</title>" +
			"<meta http-equiv=\"refresh\" content=\"5\"/></head><body>" +
			"<h2>HS Fulda - TCPTimeCounter REST Service</h2>"+
			"<p><b>HTML-Output:</b> " + getCounter() + "</p></body>"+
			"<form method=POST action=\"/counter\">" +
			"<input type=\"hidden\" name=\"input\" value=\"1\">"+
			"<input type=\"submit\" value=\"Increment\"></form></body></html>";
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String incrCounterJson(@QueryParam("input") String input) {
		Long currentCounterValue = getCounter();
		currentCounterValue = currentCounterValue + (new Long(input)).longValue();
		setCounter(currentCounterValue);
		return "{\n  \"type\": \"JSON\",\n  \"output\": \"Plain-Text: TCPTimeCounter counter increased by " + input
				+ " to " + currentCounterValue + "\"\n}";
	}

}