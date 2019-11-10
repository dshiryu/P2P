/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path( RESTToLowerCaseService.webContextPath )
public class RESTToLowerCaseService
{
   static final String webContextPath = "/tolowercase";
   
   @GET
   @Produces( MediaType.TEXT_PLAIN )
   public String toUpperCasePlain( @QueryParam("input") String input )
   {
      return "Plain-Text: " + input.toLowerCase();
   }

   @GET
   @Produces( MediaType.TEXT_HTML )
   public String toUpperCaseHtml( @QueryParam("input") String input )
   {
      return "<html><title>RESTService</title><body><h2>HTML: " + input.toLowerCase() + "</h2></body></html>";
   }
   
   @GET
   @Produces( MediaType.APPLICATION_JSON )
   public String toUpperCaseJson( @QueryParam("input") String input )
   {
      return "{\n  \"type\": \"JSON\",\n  \"output\": \"" + input.toLowerCase() + "\"\n}";
   }  

}