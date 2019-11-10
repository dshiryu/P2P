package verteiltesysteme.rmi;

/* damit der Server gestartet werden kann, muss die rmiregistry (Verzeichnisdienst
 *  für bereitgestellte Funktionen und deren Parameter etc., vgl. Folien, laufen. 
 *  Hierfür kann in Eclipse über "Run --> External Tools" eine neue "External Tool
 *  Configuration" erstellt werden, die die rmiregistry aufruft:
 *  
 *  - New Configuration, "Progam" auswählen, (+ Symbol)
 *  
 *  - Im Tab "Main" --> Location z.B. für Standard Java 8 JRE unter Windows: 
 *  	C:\Program Files\Java\jre1.8.0_181\bin\rmiregistry.exe
 *  
 *  - Im Tab "Environment" --> Variable "CLASSPATH" anlegen und auf das bin
 *    Verzeichnis des Projects z.B.
 *      C:\Users\<username>\git\verteilte-systeme-bsc-ai-examples\
 *      VerteilteSysteme-Examples\bin" 
 *    zeigen lassen. Danach mittels "Run" rmiregistry starten.
 *    
 *  - Run Configuration für die in diesem File gezeigte Klasse RMIEchoServer muss
 *    im Tab "Arguments" im Feld "VM Arguments:" folgendes eingetragen haben:
 *      -Djava.rmi.server.codebase=file:${workspace_loc:VerteilteSysteme-Examples/bin/}
 *    damit die für den RMIEchoServer erfoderlichen Klassen als codebase in RMI
 *    verfügbar sind    
 */

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIEchoServer implements RMIEchoInterface {
	public RMIEchoServer() {
	}

	public static void main(String args[]) {

		try {
			RMIEchoServer obj = new RMIEchoServer();
			RMIEchoInterface stub = (RMIEchoInterface) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("RMIEchoInterface", stub);

			System.out.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public String toLowerCase(String input) throws RemoteException {
		return input.toLowerCase();
	}

	@Override
	public String toUpperCase(String input) throws RemoteException {
		return input.toUpperCase();
	}
}