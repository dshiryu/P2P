package verteiltesysteme.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIEchoInterface extends Remote {
	public String toLowerCase(String input) throws RemoteException;
	public String toUpperCase(String input) throws RemoteException;
}