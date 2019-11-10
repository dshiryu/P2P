/* Beispiel angelehnt an http://www.torsten-horn.de/techdocs/jee-rest.htm */
package verteiltesysteme.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path( RESTToUpperCaseService.webContextPath )
public class RESTToUpperCaseService
{
   static final String webContextPath = "/touppercase";
   
   @GET
   @Produces( MediaType.TEXT_PLAIN )
   public String toLowerCasePlain( @QueryParam("input") String input )
   {
      return "Plain-Text: " + input.toUpperCase();
   }

   @GET
   @Produces( MediaType.TEXT_HTML )
   public String toLowerCaseHtml( @QueryParam("input") String input )
   {
      return "<html><title>RESTService</title><body><h2>HTML: " + input.toUpperCase() + "</h2></body></html>";
   }
   
   @GET
   @Produces( MediaType.APPLICATION_JSON )
   public String toLowerCaseJson( @QueryParam("input") String input )
   {
      return "{\n  \"type\": \"JSON\",\n  \"output\": \"" + input.toUpperCase() + "\"\n}";
   }  
   
}