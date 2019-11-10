package verteiltesysteme.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIEchoClient {

    private RMIEchoClient() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            final String[] boundNames = registry.list();
            System.out.println(
               "Names bound to RMI registry at host " + host + " and default TCP port 1099:");
            for (final String name : boundNames)
            {
               System.out.println("\t" + name);
            }
            RMIEchoInterface stub = (RMIEchoInterface) registry.lookup("RMIEchoInterface");
            System.out.println("\nStub: " + stub.toString());
            System.out.println("\nSending request to convert hAlLo to lower case...");
            String response = stub.toLowerCase("hAlLo");
            System.out.println("response: " + response);
            System.out.println("\nSending request to convert hAlLo to upper case...");
            response = stub.toUpperCase("hAlLo");
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}